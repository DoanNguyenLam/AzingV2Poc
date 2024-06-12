package email.services;

import email.dto.ClaudeMailResDTO;
import email.dto.EmailDTO;
import email.utils.EmailPortletConfigs;
import org.springframework.ui.ModelMap;

import javax.portlet.PortletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface EmailService {
    List<EmailDTO> getListOfEmails(String accessToken);

    EmailDTO getEmailById(Long id);

    String getThreadDetail(String accessToken, String threadId);

    ClaudeMailResDTO summaryAndSuggestEmail(EmailPortletConfigs emailPortletConfigs, String mailBody, Boolean isSummary, Boolean isThread) throws IOException;

    String renderService(ModelMap modelMap, PortletRequest portletRequest, EmailPortletConfigs emailPortletConfigs, EmailDTO currentEmail) throws InterruptedException, ExecutionException;
}
