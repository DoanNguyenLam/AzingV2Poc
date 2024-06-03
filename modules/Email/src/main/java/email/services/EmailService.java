package email.services;

import email.dto.ClaudeMailResDTO;
import email.dto.EmailDTO;

import java.util.List;

public interface EmailService {
    List<EmailDTO> getListOfEmails();

    EmailDTO getEmailById(Long id);

    ClaudeMailResDTO summaryAndSuggestEmail(String mailBody, Boolean isSummary, String claudeApiKey);

    String replySuggestionEmail();

    String firstRenderLayout();

    String updateRenderLayout();
}
