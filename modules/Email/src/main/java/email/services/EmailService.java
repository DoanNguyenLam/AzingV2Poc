package email.services;

import email.dto.ClaudeMailResDTO;
import email.dto.EmailDTO;

import java.io.IOException;
import java.util.List;

public interface EmailService {
    List<EmailDTO> getListOfEmails();

    EmailDTO getEmailById(Long id);

    ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey) throws IOException;

    String replySuggestionEmail();

    String firstRenderLayout();

    String updateRenderLayout();
}
