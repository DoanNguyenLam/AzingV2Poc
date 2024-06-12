package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.ClaudeMailResDTO;
import email.dto.ClaudeRequestDTO;
import email.dto.ClaudeResponseDTO;
import email.dto.EmailDTO;
import email.utils.EmailPortletConfigs;
import email.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.portlet.PortletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private GmailService gmailService;

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_VERSION = "2023-06-01";

    private static final String SUMMARY_PROMPT = " Ensure the summary is within 200 words and presented in bullet points by html tag. ";
    private static final String SUGGEST_PROMPT = " Ensure the suggestion is within 200 words and presented in bullet points by html tag. ";

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public List<EmailDTO> getListOfEmails(String accessToken) {
        return gmailService.getListMail(accessToken);
    }

    @Override
    public EmailDTO getEmailById(Long id) {
        return null;
    }

    @Override
    public ClaudeMailResDTO summaryAndSuggestEmail(EmailPortletConfigs emailPortletConfigs, String mailBody, Boolean isSummary, Boolean isThread) {

        LOGGER.info("[{} EMAIL] - running ...", isSummary ? "SUMMARY" : "SUGGESTION");
        String claudeApiKey = emailPortletConfigs.getClaudeAPIKey();
        String summaryPrompt = emailPortletConfigs.getPromptSummary();
        String suggestionPrompt = emailPortletConfigs.getPromptSuggestion();
        if (claudeApiKey.isEmpty() || suggestionPrompt.isEmpty() || summaryPrompt.isEmpty()) {
            LOGGER.info("[{} EMAIL] - Some configs not found", isSummary ? "SUMMARY" : "SUGGESTION");
            return null;
        }
        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", claudeApiKey);
        headers.set("anthropic-version", API_VERSION);
        headers.set("User-Agent", "Application");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content = (isSummary ? summaryPrompt + SUMMARY_PROMPT : suggestionPrompt + SUGGEST_PROMPT) + mailBody;
        String mailType = isSummary ? "SUMMARY" : "SUGGESTION";

        LOGGER.info("[{} EMAIL] - Prompt - [{}]", isSummary ? "SUMMARY" : "SUGGESTION", content);
        // Create the request body using the model class
        ClaudeRequestDTO request = new ClaudeRequestDTO(
                Collections.singletonList(new ClaudeRequestDTO.Message("user", content))
        );

        // Serialize the request body to JSON
        ObjectMapper mapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(request);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error serializing request body", e);
            return null;
        }

        // Create the HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String response = Utils.callApi(API_URL, HttpMethod.POST, entity, String.class);
        if (response == null || response.isEmpty()) {
            return null;
        } else {
            try {
                String result = mapper.readValue(response, ClaudeResponseDTO.class).getContent().get(0).getText();
                LOGGER.info("[{} EMAIL] - done!", isSummary ? "SUMMARY" : "SUGGESTION");
                return new ClaudeMailResDTO(mailType, result);
            } catch (IOException e) {
                LOGGER.info("[{} EMAIL] - error!", isSummary ? "SUMMARY" : "SUGGESTION");
                LOGGER.error("Error parsing API response", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getThreadDetail(String accessToken, String threadId){
        return gmailService.getGmailThreadDetail(accessToken, threadId);
    }

    @Override
    public String renderService(ModelMap modelMap, PortletRequest portletRequest, EmailPortletConfigs emailPortletConfigs, EmailDTO currentEmail) throws InterruptedException, ExecutionException {
        LOGGER.info("[RENDER SERVICE] - running ...");

        String accessToken = emailPortletConfigs.getGgAccessToken();
        List<EmailDTO> emailDTOList;

        if (currentEmail != null) {
            LOGGER.info("[RENDER SERVICE] - id = {}, thread id = {}", currentEmail.getId(), currentEmail.getThreadId());

            emailDTOList = getListOfEmails(accessToken);
            Optional<EmailDTO> emailDTOOptional = emailDTOList
                    .stream()
                    .filter(emailDTO1 -> emailDTO1.getId().equals(currentEmail.getId()))
                    .findFirst();
            if (emailDTOOptional.isPresent()) {
                EmailDTO emailDTO = emailDTOOptional.get();
                modelMap.put("originalEmail", emailDTO.getBodyHtml());
            }
            String bodyText;
            Boolean isTheard ;
            if (currentEmail.getId().equals(currentEmail.getThreadId())) {
                bodyText = emailDTOOptional.get().getBodyPlainText();
                isTheard = false;
            } else {
                bodyText = gmailService.getGmailThreadDetail(accessToken, currentEmail.getThreadId());
                isTheard = true;
            }

            LOGGER.info("Is use claude: {}", emailPortletConfigs.isUseClaudeAI());
            if (emailPortletConfigs.isUseClaudeAI()) {
                CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(emailPortletConfigs, bodyText, true, isTheard));
                CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(emailPortletConfigs, bodyText, false, isTheard));

                CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

                ClaudeMailResDTO summaryResponse = summaryFuture.get();
                ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
                if (summaryResponse == null || suggestionResponse == null) {
                    LOGGER.info("[RENDER SERVICE] - error!");
                    LOGGER.error("One of the responses is null: summaryResponse={}, suggestionResponse={}", summaryResponse, suggestionResponse);
                    return "error";
                }
                modelMap.put("summary", summaryResponse.getContent());
                modelMap.put("suggestion", suggestionResponse.getContent());
            } else {
                LOGGER.info("[RENDER SEVICE] - Claude AI is disable");
                modelMap.put("summary", bodyText);
                modelMap.put("suggestion", bodyText);
            }


        } else {
            emailDTOList = getListOfEmails(accessToken);
        }
        modelMap.put("listMails", emailDTOList);
        LOGGER.info("[RENDER SERVICE] - done!");
        return "mails";
    }
}
