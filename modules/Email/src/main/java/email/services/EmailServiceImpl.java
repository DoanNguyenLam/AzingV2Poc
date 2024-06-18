package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private GmailService gmailService;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_VERSION = "2023-06-01";

    private static final String INTRODUCE_PROMPT_SINGLE_MAIL = "Here is the email content:";
    private static final String INTRODUCE_PROMPT_CONVERSATION = "Here is the conversation thread content containing multiple emails exchanged between parties:";

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public List<EmailDTO> getListOfEmails(String accessToken) {
        return gmailService.getListMail(accessToken);
    }

    @Override
    public List<LabelDTO> getListLabels(String accessToken) {
        return gmailService.getListLabels(accessToken);
    }

    @Override
    public EmailDTO getEmailById(Long id) {
        return null;
    }

    @Override
    public ClaudeMailResDTO summaryAndSuggestEmail(EmailPortletConfigs emailPortletConfigs, String mailBody, Boolean isSummary, Boolean isThread) {

        LOGGER.info("[{} EMAIL] - running ...", isSummary ? "SUMMARY" : "SUGGESTION");
        String claudeApiKey = emailPortletConfigs.getClaudeAPIKey();
        String gptAPIKey = emailPortletConfigs.getGptAPIKey();
        ModalAI modalAI = emailPortletConfigs.getModal();
        String summaryPromptSingleMail = emailPortletConfigs.getPromptSummarySingleMail();
        String suggestionPromptSingleMail = emailPortletConfigs.getPromptSuggestionSingleMail();
        String summaryPromptConversation = emailPortletConfigs.getPromptSummaryConversation();
        String suggestionPromptConversation = emailPortletConfigs.getPromptSuggestionConversation();

        if (claudeApiKey.isEmpty() || suggestionPromptSingleMail.isEmpty() || summaryPromptSingleMail.isEmpty()) {
            LOGGER.info("[{} EMAIL] - Some configs not found", isSummary ? "SUMMARY" : "SUGGESTION");
            return null;
        }

        String content = "";

        if (isSummary){
            if (isThread){
                content = INTRODUCE_PROMPT_CONVERSATION + "\" " + mailBody + "\" " + summaryPromptConversation;
            }else{
                content = INTRODUCE_PROMPT_SINGLE_MAIL +  "\" " + mailBody + "\" " + summaryPromptSingleMail;
            }
        }else{
            if (isThread){
                content = INTRODUCE_PROMPT_CONVERSATION +  "\" " + mailBody + "\" " + suggestionPromptConversation;
            }else{
                content = INTRODUCE_PROMPT_SINGLE_MAIL +  "\" " + mailBody + "\" " + suggestionPromptSingleMail;
            }
        }

        String mailType = isSummary ? "SUMMARY" : "SUGGESTION";
        LOGGER.info("[{} EMAIL] - Prompt - [{}]", isSummary ? "SUMMARY" : "SUGGESTION", content);

        // Serialize the request body to JSON
        ObjectMapper mapper = new ObjectMapper();
        String requestBody;

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Application");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String API_URL;
        switch (modalAI) {
            case CHAT_GPT:
                API_URL = GPT_API_URL;
                headers.set("Authorization", "Bearer " + gptAPIKey);

                // Create the request body using the model class
                GPTRequestDTO requestGPT = new GPTRequestDTO(
                        Collections.singletonList(new GPTRequestDTO.Message("user", content))
                );

                try {
                    requestBody = mapper.writeValueAsString(requestGPT);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error serializing request body", e);
                    return null;
                }
                break;
            default:
                API_URL = CLAUDE_API_URL;
                headers.set("x-api-key", claudeApiKey);
                headers.set("anthropic-version", API_VERSION);

                // Create the request body using the model class
                ClaudeRequestDTO requestClaude = new ClaudeRequestDTO(
                        Collections.singletonList(new ClaudeRequestDTO.Message("user", content))
                );

                try {
                    requestBody = mapper.writeValueAsString(requestClaude);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error serializing request body", e);
                    return null;
                }
                break;
        }

        // Create the HttpEntity
        LOGGER.info("Headers: {}", headers);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String response = Utils.callApi(API_URL, HttpMethod.POST, entity, String.class);
        if (response == null || response.isEmpty()) {
            return null;
        } else {
            try {
                String result;

                switch (modalAI) {
                    case CHAT_GPT:
                        result = mapper.readValue(response, GPTResponseDTO.class)
                                .getChoices()
                                .get(0)
                                .getMessage()
                                .getContent();
                        break;
                    default:
                        result = mapper.readValue(response, ClaudeResponseDTO.class).getContent().get(0).getText();
                }
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
        List<EmailDTO> emailDTOList = getListOfEmails(accessToken);

        List<LabelDTO> labelDTOS = getListLabels(accessToken);
        LOGGER.info("[RENDER SERVICE] - list labels {}", labelDTOS);

        if (currentEmail != null) {
            LOGGER.info("[RENDER SERVICE] - id = {}, thread id = {}", currentEmail.getId(), currentEmail.getThreadId());

            ;
            Optional<EmailDTO> emailDTOOptional = emailDTOList
                    .stream()
                    .filter(emailDTO1 -> emailDTO1.getId().equals(currentEmail.getId()))
                    .findFirst();
            if (emailDTOOptional.isPresent()) {
                EmailDTO emailDTO = emailDTOOptional.get();
                modelMap.put("originalEmail", emailDTO.getBodyHtml());
            }else {
                return "Mail empty";
            }

            String bodyText;
            boolean isThread ;
            if (currentEmail.getId().equals(currentEmail.getThreadId())) {
                bodyText = emailDTOOptional.get().getBodyPlainText();
                if (bodyText == null || bodyText.isEmpty()) {
                    bodyText = emailDTOOptional.get().getBodyHtml();
                }
                isThread = false;
            } else {
                bodyText = gmailService.getGmailThreadDetail(accessToken, currentEmail.getThreadId());
                isThread = true;
            }

            String finalBodyText = bodyText;
            CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(emailPortletConfigs, finalBodyText, true, isThread));
            CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(emailPortletConfigs, finalBodyText, false, isThread));

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


        }
        modelMap.put("listMails", emailDTOList);
        LOGGER.info("[RENDER SERVICE] - done!");
        return "mails";
    }
}
