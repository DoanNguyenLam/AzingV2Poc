package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.ClaudeRequestDTO;
import email.dto.ClaudeResponseDTO;
import email.dto.EmailDTO;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

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


    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_KEY = "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";
    private static final String API_VERSION = "2023-06-01";
    private static final String SUMMARY_PROMPT = "This is a email body, summarize it for me: ";
    private static final String SUGGEST_PROMPT = "This is a mail body, give me the reply suggestion: ";

    @Override
    public String summaryAndSuggestEmail(String mailBody, Boolean isSummary)  {
        RestTemplate restTemplate = new RestTemplate();

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        headers.set("anthropic-version", API_VERSION);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content;
        if (isSummary){
            content = SUMMARY_PROMPT + mailBody;
        }else{
            content = SUGGEST_PROMPT + mailBody;
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
            return null;
        }

        // Create the HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Execute the request
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        // Deserialize the response

        try {
            return mapper.readValue(response.getBody(), ClaudeResponseDTO.class).getContent().get(0).getText();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
