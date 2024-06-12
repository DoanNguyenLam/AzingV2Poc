package email.utils;

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
    private String ggAccessToken;
    private boolean isUseClaudeAI;
    private String promptSummary;
    private String promptSuggestion;

    public void updateProps(PortletRequest portletRequest) {
        String googleClientKey = portletRequest.getPreferences().getValue(GG_CLIENT_KEY, "");
        String goggleSecretKey = portletRequest.getPreferences().getValue(GG_SECRET_KEY, "");
        String claudeAPIKey = portletRequest.getPreferences().getValue(CLAUDE_API_KEY, "");
        String gmailAccessToken = portletRequest.getPreferences().getValue(GG_ACCESS_TOKEN, "");
        String isUseClaudeAI = portletRequest.getPreferences().getValue(IS_USE_CLAUDE_AI, String.valueOf(false));
        String promptSummary = portletRequest.getPreferences().getValue(PROMPT_SUMMARY, "");
        String promptSuggestion = portletRequest.getPreferences().getValue(PROMPT_SUGGESTION, "");

        setGoogleClientKey(googleClientKey);
        setGoggleSecretKey(goggleSecretKey);
        setClaudeAPIKey(claudeAPIKey);
        setGgAccessToken(gmailAccessToken);
        setUseClaudeAI(Boolean.valueOf(isUseClaudeAI));
        setPromptSummary(promptSummary);
        setPromptSuggestion(promptSuggestion);
    }
}
