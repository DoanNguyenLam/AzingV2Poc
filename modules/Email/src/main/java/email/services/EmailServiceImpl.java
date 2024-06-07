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
import java.util.*;
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
        String accessToken = emailConfigs.getGgAccessToken();
        List<EmailDTO> emailDTOList;

        if (currentEmail != null) {
            LOGGER.info("[RENDER SERVICE] - id = {}, thread id = {}", currentEmail.getId(), currentEmail.getThreadId());
            String mailBody = "<div id=\":pi\" class=\"ii gt\" jslog=\"20277; u014N:xr6bB; 1:WyIjdGhyZWFkLWY6MTgwMTE2OTIwODAyMjkwMzExMSJd; 4:WyIjbXNnLWY6MTgwMTE2OTIwODAyMjkwMzExMSJd\"><div id=\":pj\" class=\"a3s aiL msg6496342487879356631\"><u></u><div style=\"margin:0;padding:0\" bgcolor=\"#FFFFFF\"><table width=\"100%\" height=\"100%\" style=\"min-width:348px\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" lang=\"en\"><tbody><tr height=\"32\" style=\"height:32px\"><td></td></tr><tr align=\"center\"><td><div><div></div></div><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"padding-bottom:20px;max-width:516px;min-width:220px\"><tbody><tr><td width=\"8\" style=\"width:8px\"></td><td><div style=\"border-style:solid;border-width:thin;border-color:#dadce0;border-radius:8px;padding:40px 20px\" align=\"center\" class=\"m_6496342487879356631mdv2rw\"><img src=\"https://ci3.googleusercontent.com/meips/ADKq_NZa00tQPIx4fdFGtuHSHzYQ-whZFD7HWD3OhXR05fT4XqJ_Wca2erL9R9_382bPBUom-sDOVfi4G3FXbYaZsHsGqQUAL6-JIKgEzWlarZbeNSLveyc6EKOURFQhphfzG2ZAwLiyrJsC=s0-d-e1-ft#https://www.gstatic.com/images/branding/googlelogo/2x/googlelogo_color_74x24dp.png\" width=\"74\" height=\"24\" aria-hidden=\"true\" style=\"margin-bottom:16px\" alt=\"Google\" class=\"CToWUd\" data-bit=\"iit\"><div style=\"font-family:'Google Sans',Roboto,RobotoDraft,Helvetica,Arial,sans-serif;border-bottom:thin solid #dadce0;color:rgba(0,0,0,0.87);line-height:32px;padding-bottom:24px;text-align:center;word-break:break-word\"><div style=\"font-size:24px\"><a>azing-gmail-summary</a> đã được cấp quyền truy cập vào Tài khoản Google của&nbsp;bạn </div><table align=\"center\" style=\"margin-top:8px\"><tbody><tr style=\"line-height:normal\"><td align=\"right\" style=\"padding-right:8px\"><img width=\"20\" height=\"20\" style=\"width:20px;height:20px;vertical-align:sub;border-radius:50%\" src=\"https://lh3.googleusercontent.com/a/ACg8ocKA7xbzNLHspCNmh6h_mSForiZgGnig2qowhh1-x9TbfIoM9XE=s96-c\" alt=\"\" class=\"CToWUd\" data-bit=\"iit\"></td><td><a style=\"font-family:'Google Sans',Roboto,RobotoDraft,Helvetica,Arial,sans-serif;color:rgba(0,0,0,0.87);font-size:14px;line-height:20px\">lamdn13072000@gmail.com</a></td></tr></tbody></table> </div><div style=\"font-family:Roboto-Regular,Helvetica,Arial,sans-serif;font-size:14px;color:rgba(0,0,0,0.87);line-height:20px;padding-top:20px;text-align:left\"><br>Nếu không cấp quyền truy cập thì bạn nên kiểm tra hoạt động này và bảo mật tài khoản của mình.<div style=\"padding-top:32px;text-align:center\"><a href=\"https://accounts.google.com/AccountChooser?Email=lamdn13072000@gmail.com&amp;continue=https://myaccount.google.com/alert/nt/1717728811000?rfn%3D127%26rfnc%3D1%26eid%3D2694610908232355575%26et%3D0\" style=\"font-family:'Google Sans',Roboto,RobotoDraft,Helvetica,Arial,sans-serif;line-height:16px;color:#ffffff;font-weight:400;text-decoration:none;font-size:14px;display:inline-block;padding:10px 24px;background-color:#4184f3;border-radius:5px;min-width:90px\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://accounts.google.com/AccountChooser?Email%3Dlamdn13072000@gmail.com%26continue%3Dhttps://myaccount.google.com/alert/nt/1717728811000?rfn%253D127%2526rfnc%253D1%2526eid%253D2694610908232355575%2526et%253D0&amp;source=gmail&amp;ust=1717816796156000&amp;usg=AOvVaw2TFERvnMnx9dIh3IiJY-Sn\">Kiểm tra hoạt động</a></div></div><div style=\"padding-top:20px;font-size:12px;line-height:16px;color:#5f6368;letter-spacing:0.3px;text-align:center\">Bạn cũng có thể xem hoạt động bảo mật tại<br><a style=\"color:rgba(0,0,0,0.87);text-decoration:inherit\">https://myaccount.google.com/<wbr>notifications</a></div></div><div style=\"text-align:left\"><div style=\"font-family:Roboto-Regular,Helvetica,Arial,sans-serif;color:rgba(0,0,0,0.54);font-size:11px;line-height:18px;padding-top:12px;text-align:center\"><div>Chúng tôi gửi email này để thông báo cho bạn biết về những thay đổi quan trọng đối với Tài khoản Google và dịch vụ của bạn.</div><div style=\"direction:ltr\">© 2024 Google LLC, <a class=\"m_6496342487879356631afal\" style=\"font-family:Roboto-Regular,Helvetica,Arial,sans-serif;color:rgba(0,0,0,0.54);font-size:11px;line-height:18px;padding-top:12px;text-align:center\">1600 Amphitheatre Parkway, Mountain View, CA 94043, USA</a></div></div></div></td><td width=\"8\" style=\"width:8px\"></td></tr></tbody></table></td></tr><tr height=\"32\" style=\"height:32px\"><td></td></tr></tbody></table></div></div><div class=\"yj6qo\"></div></div>";

            emailDTOList = getListOfEmails(accessToken);
            Optional<EmailDTO> emailDTOOptional = emailDTOList.stream().filter(emailDTO1 -> emailDTO1.getId().equals(currentEmail.getId())).findFirst();
            if (emailDTOOptional.isPresent()) {
                EmailDTO emailDTO = emailDTOOptional.get();
                modelMap.put("originalEmail", emailDTO.getBodyHtml());

            }

//            CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, true, emailConfigs.getClaudeAPIKey()));
//            CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> this.summaryAndSuggestEmail(mailBody, false, emailConfigs.getClaudeAPIKey()));
//
//            CompletableFuture.allOf(summaryFuture, suggestionFuture).join();
//
//            ClaudeMailResDTO summaryResponse = summaryFuture.get();
//            ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
//            if (summaryResponse == null || suggestionResponse == null) {
//                LOGGER.error("One of the responses is null: summaryResponse={}, suggestionResponse={}", summaryResponse, suggestionResponse);
//                return "error";
//            }
//            modelMap.put("summary", summaryResponse.getContent());
//            modelMap.put("suggestion", suggestionResponse.getContent());
//            TODO: Get current email
            modelMap.put("summary", "summary");
            modelMap.put("suggestion", "suggestion");
        } else {
            emailDTOList = getListOfEmails(accessToken);
        }
        modelMap.put("listMails", emailDTOList);
        return "mails";
    }
}
