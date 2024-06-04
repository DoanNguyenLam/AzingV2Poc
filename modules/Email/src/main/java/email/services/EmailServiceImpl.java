package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.*;
import email.utils.EmailConfigs;
import email.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;

import javax.portlet.PortletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class EmailServiceImpl implements EmailService {
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_VERSION = "2023-06-01";
    private static final String SUMMARY_PROMPT = "This is a email body, summarize it for me: ";
    private static final String SUGGEST_PROMPT = "This is a mail body, give me the reply suggestion: ";

    private static final Logger _logger = LoggerFactory.getLogger(
            EmailServiceImpl.class);
    @Override
    public List<EmailDTO> getListOfEmails() {
        // TODO: get list mails
        List<EmailDTO> emailDTOList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setId(String.valueOf(i));
            emailDTO.setThreadId(String.valueOf(i));
            emailDTO.setSnippet("When present, contains the ID of an external attachment that can be retrieved in a separate messages.attachments.get request.");
            emailDTO.setBodyText("Body text");
            emailDTO.setBodyHtml("Body html");

            emailDTO.setSubject("Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fugit fugiat id porro, laborum dolorem minima eos nulla ratione a obcaecati non, iusto eum enim alias corrupti saepe eaque tenetur sequi.");
            emailDTO.setData("Sample email data " + i);
            emailDTO.setDate("2024-05-0" + i);
            emailDTOList.add(emailDTO);
        }
        return emailDTOList;
    }

    @Override
    public List<EmailDTO> getListEmailByThreadId(String threadId) {
        // TODO: get list mails by thread
        List<EmailDTO> emailDTOList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setId(String.valueOf(i));
            emailDTO.setThreadId(String.valueOf(i));
            emailDTO.setSnippet("When present, contains the ID of an external attachment that can be retrieved in a separate messages.attachments.get request.");
            emailDTO.setBodyText("Body text");
            emailDTO.setBodyHtml("Body html");

            emailDTO.setSubject("Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fugit fugiat id porro, laborum dolorem minima eos nulla ratione a obcaecati non, iusto eum enim alias corrupti saepe eaque tenetur sequi.");
            emailDTO.setData("Sample email data " + i);
            emailDTO.setDate("2024-05-0" + i);
            emailDTOList.add(emailDTO);
        }
        return emailDTOList;
    }

    @Override
    public EmailDTO getEmailById(String id) {
        return null;
    }

    @Override
    public ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey) {

        _logger.info("KEY {}", claudeApiKey);

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", claudeApiKey);
        headers.set("anthropic-version", API_VERSION);
        headers.set("User-Agent", "Application");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content;
        ClaudeMailType mailType = ClaudeMailType.SUMMARY;
        if (isSummary){
            content = SUMMARY_PROMPT + mailBody;
        }else{
            content = SUGGEST_PROMPT + mailBody;
            mailType = ClaudeMailType.SUGGESTION;
        }

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
            _logger.error("Error serializing request body", e);
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
                return new ClaudeMailResDTO(mailType, result);
            } catch (IOException e) {
                _logger.error("Error parsing API response", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<ClaudeMailResDTO> getSummaryAndSuggestEmail(EmailConfigs emailConfigs, String threadId) throws ExecutionException, InterruptedException {
        if (threadId == null) {
            return new ArrayList<>();
        }

        List<ClaudeMailResDTO> claudeMailResDTOList = new ArrayList<>();
        String mailBody = "Hey cabien1307!\n" +
                "\n" +
                "You’ve just enabled two-factor authentication.\n" +
                "\n" +
                "Please take a moment to check that you have saved your recovery codes in a safe place. You can\n" +
                "download your recovery codes at:\n" +
                "\n" +
                "https://github.com/settings/auth/recovery-codes\n" +
                "\n" +
                "Recovery codes are the only way to access your account again. By saving your\n" +
                "recovery codes, you’ll be able to regain access if you:\n" +
                "\n" +
                "* Lose your phone\n" +
                "* Delete your authenticator app\n" +
                "* Change your phone number\n" +
                "\n" +
                "GitHub Support will not be able to restore access to your account.\n" +
                "\n" +
                "To disable two-factor authentication, visit\n" +
                "https://github.com/settings/security\n" +
                "\n" +
                "More information about two-factor authentication can be found on GitHub Help at\n" +
                "https://docs.github.com/articles/about-two-factor-authentication\n" +
                "\n" +
                "If you have any questions, please visit https://support.github.com.\n" +
                "\n" +
                "Thanks,\n" +
                "Your friends at GitHub";
        CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, true, emailConfigs.getClaudeAPIKey()));
        CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, false, emailConfigs.getClaudeAPIKey()));

        CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

        ClaudeMailResDTO summaryResponse = summaryFuture.get();
        ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
        if (summaryResponse == null || suggestionResponse == null) {
            _logger.error("One of the responses is null: summaryResponse={}, suggestionResponse={}", summaryResponse, suggestionResponse);
            return new ArrayList<>();
        }

        claudeMailResDTOList.add(suggestionResponse);
        claudeMailResDTOList.add(summaryResponse);

        return claudeMailResDTOList;
    };



    @Override
    public String navigateRender(ModelMap modelMap, PortletRequest portletRequest, EmailConfigs emailConfigs, String threadId, String emailId) throws InterruptedException, ExecutionException {

        List<EmailDTO> emailDTOList;
        if (threadId != null && emailId != null) {
            emailDTOList = getListEmailByThreadId(threadId);
            // Filter email by id
            EmailDTO emailDTO = new EmailDTO();
            modelMap.put(ModelMapKey.ORGINAL_EMAIL.getValue(), emailDTO);

        } else {
            emailDTOList = getListOfEmails();
        }
        modelMap.put(ModelMapKey.LIST_EMAIL.getValue(), emailDTOList);


        List<ClaudeMailResDTO> claudeMailResDTOList = this.getSummaryAndSuggestEmail(emailConfigs, threadId);
        this.updateClaudeModelMap(modelMap, claudeMailResDTOList);
        return "main";
    }

    @Override
    public void updateClaudeModelMap(ModelMap modelMap, List<ClaudeMailResDTO> claudeMailResDTOList) {
        if (!claudeMailResDTOList.isEmpty()) {
            for (ClaudeMailResDTO claudeMailResDTO: claudeMailResDTOList) {
                switch (claudeMailResDTO.getType()) {
                    case SUMMARY:
                        modelMap.put(ModelMapKey.SUMMARY_EMAIL.getValue(), claudeMailResDTO.getContent());
                        break;
                    case SUGGESTION:
                        modelMap.put(ModelMapKey.SUGGESTION_EMAIL.getValue(), claudeMailResDTO.getContent());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
