package email.services;

import email.dto.EmailDTO;

import java.util.List;

public interface EmailService {
    List<EmailDTO> getListOfEmails();

    EmailDTO getEmailById(Long id);

    String summaryAndSuggestEmail(String mailBody, Boolean isSummary);

    String replySuggestionEmail();

    String firstRenderLayout();

    String updateRenderLayout();
}
