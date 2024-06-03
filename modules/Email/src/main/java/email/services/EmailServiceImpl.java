package email.services;

import email.dto.EmailDTO;

import java.util.Collections;
import java.util.List;

public class EmailServiceImpl implements EmailService {
    @Override
    public List<EmailDTO> getListOfEmails() {
        return Collections.emptyList();
    }

    @Override
    public EmailDTO getEmailById(Long id) {
        return null;
    }

    @Override
    public String summaryEmail() {
        return "";
    }

    @Override
    public String replySuggestionEmail() {
        return "";
    }

    @Override
    public String firstRenderLayout() {
        return "";
    }

    @Override
    public String updateRenderLayout() {
        return "";
    }
}
