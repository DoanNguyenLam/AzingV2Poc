package email.services;

import email.dto.EmailDTO;

import java.util.List;

public interface EmailService {
    List<EmailDTO> getListOfEmails();

    EmailDTO getEmailById(Long id);

    String summaryEmail();

    String replySuggestionEmail();

    String firstRenderLayout();

    String updateRenderLayout();
}
