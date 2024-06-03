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

    EmailDTO getEmailById(Long id);

    ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey) throws IOException;

    String renderService(ModelMap modelMap, PortletRequest portletRequest, EmailConfigs emailConfigs, EmailDTO currentEmail) throws InterruptedException, ExecutionException;
}
