package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.ClaudeMailResDTO;
import email.dto.ClaudeRequestDTO;
import email.dto.ClaudeResponseDTO;
import email.dto.EmailDTO;
import email.utils.EmailConfigs;
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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private GmailService gmailService;

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_VERSION = "2023-06-01";
    private static final String MAIL_SUMMARY_PROMPT = "This is a email body, summarize it for me: ";
    private static final String MAIL_SUGGEST_PROMPT = "This is a mail body, give me the reply suggestion: ";

    private static final String THREAD_SUMMARY_PROMPT = "this is an email conversation, summarize it for me: ";
    private static final String THREAD_SUGGEST_PROMPT = "this is an email conversation, give me the reply suggestion: ";

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
    public ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey, Boolean isThread) {

        LOGGER.info("KEY {}", claudeApiKey);

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", claudeApiKey);
        headers.set("anthropic-version", API_VERSION);
        headers.set("User-Agent", "Application");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content;
        String mailType = "SUMMARY";
        if (isThread){
            content = THREAD_SUMMARY_PROMPT + mailBody;
            if (!isSummary) {
                content = THREAD_SUGGEST_PROMPT + mailBody;
                mailType = "SUGGESTION";
            }
        } else {
            content = MAIL_SUMMARY_PROMPT + mailBody;
            if (!isSummary){
                content = MAIL_SUGGEST_PROMPT + mailBody;
                mailType = "SUGGESTION";
            }
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
                return new ClaudeMailResDTO(mailType, result);
            } catch (IOException e) {
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
    public String renderService(ModelMap modelMap, PortletRequest portletRequest, EmailConfigs emailConfigs, EmailDTO currentEmail) throws InterruptedException, ExecutionException {

        // TODO: impl get access token
        String accessToken = emailConfigs.getGmailAccessToken();
        String sampleAccessToken = "ya29.a0AXooCgvtLs-KO50VjrWvzSK6VAwrOjhETRMew_163ypF4ijhZz2vbxpYGsSW9dW-4GknpWZ2cAe0WbQ7FL5dL1hz2SvdFpJksiuQyT-AZg3-_LtAS5JQT-CONEWvaBaGKs2w3yk0M7UHpaTl11RqhtpChRnEEy7V-hWQaCgYKASwSARASFQHGX2MiNQY-rBSG43I4KMtAwKIvlg0171";
        List<EmailDTO> emailDTOList = getListOfEmails(sampleAccessToken);

        if (currentEmail != null) {
//            String mailBody = "Hey cabien1307!\n" +
//                    "\n" +
//                    "You’ve just enabled two-factor authentication.\n" +
//                    "\n" +
//                    "Please take a moment to check that you have saved your recovery codes in a safe place. You can\n" +
//                    "download your recovery codes at:\n" +
//                    "\n" +
//                    "https://github.com/settings/auth/recovery-codes\n" +
//                    "\n" +
//                    "Recovery codes are the only way to access your account again. By saving your\n" +
//                    "recovery codes, you’ll be able to regain access if you:\n" +
//                    "\n" +
//                    "* Lose your phone\n" +
//                    "* Delete your authenticator app\n" +
//                    "* Change your phone number\n" +
//                    "\n" +
//                    "GitHub Support will not be able to restore access to your account.\n" +
//                    "\n" +
//                    "To disable two-factor authentication, visit\n" +
//                    "https://github.com/settings/security\n" +
//                    "\n" +
//                    "More information about two-factor authentication can be found on GitHub Help at\n" +
//                    "https://docs.github.com/articles/about-two-factor-authentication\n" +
//                    "\n" +
//                    "If you have any questions, please visit https://support.github.com.\n" +
//                    "\n" +
//                    "Thanks,\n" +
//                    "Your friends at GitHub";

            String threadId = currentEmail.getThreadId();
            String messageId = currentEmail.getId();

            String mailBody = this.getThreadDetail(emailConfigs.getGmailAccessToken(), threadId);

            boolean isThread = !Objects.equals(threadId, messageId);

            CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, true, emailConfigs.getClaudeAPIKey(), isThread));
            CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, false, emailConfigs.getClaudeAPIKey(), isThread));

            CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

            ClaudeMailResDTO summaryResponse = summaryFuture.get();
            ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
            if (summaryResponse == null || suggestionResponse == null) {
                LOGGER.error("One of the responses is null: summaryResponse={}, suggestionResponse={}", summaryResponse, suggestionResponse);
                return "error";
            }
            modelMap.put("summary", summaryResponse.getContent());
            modelMap.put("suggestion", suggestionResponse.getContent());
        }

        modelMap.put("listEmails", emailDTOList);
        return "mails";
    }
}
