package email.utils;

import lombok.Getter;
import lombok.Setter;

import javax.portlet.PortletRequest;

import static email.configs.EmailConfigKeys.*;

@Getter
@Setter
public class EmailConfigs {
    private String googleClientKey;
    private String goggleSecretKey;
    private String claudeAPIKey;

    public void updateProps(PortletRequest portletRequest) {
        String googleClientKey = portletRequest.getPreferences().getValue(GG_CLIENT_KEY, "");
        String goggleSecretKey = portletRequest.getPreferences().getValue(GG_SECRET_KEY, "");
        String claudeAPIKey = portletRequest.getPreferences().getValue(CLAUDE_API_KEY, "");

        setGoogleClientKey(googleClientKey);
        setGoggleSecretKey(goggleSecretKey);
        setClaudeAPIKey(claudeAPIKey);
    }
}
