package email.services;

import email.dto.ClaudeMailResDTO;
import email.dto.EmailDTO;
import email.utils.EmailConfigs;
import org.springframework.ui.ModelMap;

import javax.portlet.PortletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface EmailService {
    List<EmailDTO> getListOfEmails();

    EmailDTO getEmailById(String id);

    List<EmailDTO> getListEmailByThreadId(String threadId);

    ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey) throws IOException;

    List<ClaudeMailResDTO> getSummaryAndSuggestEmail(EmailConfigs emailConfigs, String threadId) throws ExecutionException, InterruptedException;

    String navigateRender(ModelMap modelMap, PortletRequest portletRequest, EmailConfigs emailConfigs, String threadId, String emailId) throws InterruptedException, ExecutionException;

    void updateClaudeModelMap(ModelMap modelMap, List<ClaudeMailResDTO> claudeMailResDTOList);
}
