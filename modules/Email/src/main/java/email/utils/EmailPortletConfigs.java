package email.utils;

import com.liferay.petra.string.StringUtil;
import email.dto.ModalAI;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.portlet.PortletRequest;

import static email.configs.EmailConfigKeys.*;

@Getter
@Setter
@ToString
public class EmailPortletConfigs {
    private String googleClientKey;
    private String goggleSecretKey;
    private String claudeAPIKey;
    private String gptAPIKey;
    private String modal;
    private String ggAccessToken;
    private String ggRefreshToken;
    private String promptSummarySingleMail;
    private String promptSuggestionSingleMail;
    private String promptSummaryConversation;
    private String promptSuggestionConversation;

    public void updateProps(PortletRequest portletRequest) {
        String googleClientKey = portletRequest.getPreferences().getValue(GG_CLIENT_KEY, "");
        String goggleSecretKey = portletRequest.getPreferences().getValue(GG_SECRET_KEY, "");
//        String gmailAccessToken = portletRequest.getPreferences().getValue(GG_ACCESS_TOKEN, "");
        String gmailRefreshToken = portletRequest.getPreferences().getValue(GG_REFRESH_TOKEN, "");
        String claudeAPIKey = portletRequest.getPreferences().getValue(CLAUDE_API_KEY, "");
        String gptAPIKey = portletRequest.getPreferences().getValue(GPT_API_KEY, "");
        String modal = portletRequest.getPreferences().getValue(MODAL, "");
        String promptSummarySingleMail = portletRequest.getPreferences().getValue(PROMPT_SUMMARY_SINGLE_MAIL, "");
        String promptSuggestionSingleMail = portletRequest.getPreferences().getValue(PROMPT_SUGGESTION_SINGLE_MAIL, "");
        String promptSummaryConversation = portletRequest.getPreferences().getValue(PROMPT_SUMMARY_CONVERSATION, "");
        String promptSuggestionConversation = portletRequest.getPreferences().getValue(PROMPT_SUGGESTION_CONVERSATION, "");

        setGoogleClientKey(googleClientKey);
        setGoggleSecretKey(goggleSecretKey);
        setClaudeAPIKey(claudeAPIKey);
        setGptAPIKey(gptAPIKey);
        setModal(modal);
//        setGgAccessToken(ggAccessToken);
        setGgRefreshToken(gmailRefreshToken);
        setPromptSummarySingleMail(promptSummarySingleMail);
        setPromptSuggestionSingleMail(promptSuggestionSingleMail);
        setPromptSummaryConversation(promptSummaryConversation);
        setPromptSuggestionConversation(promptSuggestionConversation);
    }
}
