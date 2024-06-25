package email.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.*;
import email.utils.EmailPortletConfigs;
import email.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AIService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final String INTRODUCE_PROMPT_SINGLE_MAIL = "Here is the email content:";
    private static final String INTRODUCE_PROMPT_CONVERSATION = "Here is the conversation thread content containing multiple emails exchanged between parties:";

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_VERSION = "2023-06-01";

    public ClaudeMailResDTO summaryAndSuggestEmail(EmailPortletConfigs emailPortletConfigs, String mailBody, Boolean isSummary, Boolean isThread) {

        LOGGER.info("[{} EMAIL] - running ...", isSummary ? "SUMMARY" : "SUGGESTION");
        String claudeApiKey = emailPortletConfigs.getClaudeAPIKey();
        String gptAPIKey = emailPortletConfigs.getGptAPIKey();
        String modalAI = emailPortletConfigs.getModal();

        String summaryPromptSingleMail = emailPortletConfigs.getPromptSummarySingleMail();
        String suggestionPromptSingleMail = emailPortletConfigs.getPromptSuggestionSingleMail();

        String summaryPromptConversation = emailPortletConfigs.getPromptSummaryConversation();
        String suggestionPromptConversation = emailPortletConfigs.getPromptSuggestionConversation();

        if (claudeApiKey.isEmpty() || suggestionPromptSingleMail.isEmpty() || summaryPromptSingleMail.isEmpty()) {
            LOGGER.info("[{} EMAIL] - Some configs not found", isSummary ? "SUMMARY" : "SUGGESTION");
            return null;
        }

        String content = "";

        if (isSummary){
            if (isThread){
                content = INTRODUCE_PROMPT_CONVERSATION + "\" " + mailBody + "\" " + summaryPromptConversation;
            }else{
                content = INTRODUCE_PROMPT_SINGLE_MAIL +  "\" " + mailBody + "\" " + summaryPromptSingleMail;
            }
        }else{
            if (isThread){
                content = INTRODUCE_PROMPT_CONVERSATION +  "\" " + mailBody + "\" " + suggestionPromptConversation;
            }else{
                content = INTRODUCE_PROMPT_SINGLE_MAIL +  "\" " + mailBody + "\" " + suggestionPromptSingleMail;
            }
        }

        String mailType = isSummary ? "SUMMARY" : "SUGGESTION";
        LOGGER.info("[{} EMAIL] - Prompt - [{}]", isSummary ? "SUMMARY" : "SUGGESTION", content);

        // Serialize the request body to JSON
        ObjectMapper mapper = new ObjectMapper();
        String requestBody;

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Application");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String API_URL;
        switch (modalAI) {
            case "chat-gpt":
                API_URL = GPT_API_URL;
                headers.set("Authorization", "Bearer " + gptAPIKey);

                // Create the request body using the model class
                GPTRequestDTO requestGPT = new GPTRequestDTO(
                        Collections.singletonList(new GPTRequestDTO.Message("user", content))
                );

                try {
                    requestBody = mapper.writeValueAsString(requestGPT);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error serializing request body", e);
                    return null;
                }
                break;
            default:
                API_URL = CLAUDE_API_URL;
                headers.set("x-api-key", claudeApiKey);
                headers.set("anthropic-version", API_VERSION);

                // Create the request body using the model class
                ClaudeRequestDTO requestClaude = new ClaudeRequestDTO(
                        Collections.singletonList(new ClaudeRequestDTO.Message("user", content))
                );

                try {
                    requestBody = mapper.writeValueAsString(requestClaude);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error serializing request body", e);
                    return null;
                }
                break;
        }

        // Create the HttpEntity
        LOGGER.info("Headers: {}", headers);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String response = Utils.callApi(API_URL, HttpMethod.POST, entity, String.class);
        if (response == null || response.isEmpty()) {
            return null;
        } else {
            try {
                String result;

                switch (modalAI) {
                    case "chat-gpt":
                        result = mapper.readValue(response, GPTResponseDTO.class)
                                .getChoices()
                                .get(0)
                                .getMessage()
                                .getContent();
                        break;
                    default:
                        result = mapper.readValue(response, ClaudeResponseDTO.class).getContent().get(0).getText();
                }
                LOGGER.info("[{} EMAIL] - done!", isSummary ? "SUMMARY" : "SUGGESTION");
                return new ClaudeMailResDTO(mailType, result);
            } catch (IOException e) {
                LOGGER.info("[{} EMAIL] - error!", isSummary ? "SUMMARY" : "SUGGESTION");
                LOGGER.error("Error parsing API response", e);
                throw new RuntimeException(e);
            }
        }
    }

    public List<AILabel> generateLabelFromAI(EmailPortletConfigs emailPortletConfigs, String mailBody, List<String> currentLabels) {
        List<AILabel> aiLabelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AILabel aiLabel = new AILabel();
            aiLabel.setLabelName("AI Label " + i);
            aiLabelList.add(aiLabel);
        }
        return aiLabelList;
    }
}
