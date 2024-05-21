package aidabot.utils;

import lombok.Getter;
import lombok.Setter;

import javax.portlet.PortletRequest;

import static aidabot.configs.AIdaBOTConfigKeys.SCRIPT;

public class AIdaBOTConfig {

    @Getter
    @Setter
    private String script;

    public void updateProp(PortletRequest portletRequest) {
        String script = portletRequest.getPreferences().getValue(SCRIPT, "");
        setScript(script);
    }
}
