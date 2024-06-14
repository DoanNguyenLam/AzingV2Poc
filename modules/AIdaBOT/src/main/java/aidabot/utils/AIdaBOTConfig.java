package aidabot.utils;

import lombok.Getter;
import lombok.Setter;

import javax.portlet.PortletRequest;

import static aidabot.configs.AIdaBOTConfigKeys.SCRIPT;
import static aidabot.configs.AIdaBOTConfigKeys.URL;

public class AIdaBOTConfig {

    @Getter
    @Setter
    private String script;

    @Setter
    @Getter
    private String url;

    public void updateProp(PortletRequest portletRequest) {
        String script = portletRequest.getPreferences().getValue(SCRIPT, "");
        String url = portletRequest.getPreferences().getValue(URL, "");

        setScript(script);
        setUrl(url);
    }
}
