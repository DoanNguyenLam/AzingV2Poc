package aidabot.configs;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import static aidabot.configs.AIdaBOTConfigKeys.SCRIPT;

@Component(
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        immediate = true,
        properties = "javax.portlet.name=" + AIdaBOTConfigKeys.PORTLET_NAME,
        service = ConfigurationAction.class

)
public class AIdaBOTPortletActionImpl extends DefaultConfigurationAction {

    private static final Logger log = LoggerFactory.getLogger(AIdaBOTPortletActionImpl.class);

    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        String script = ParamUtil.getString(actionRequest, SCRIPT);
        log.info("processAction [{}]", script);

        setPreference(actionRequest, SCRIPT, script);

        super.processAction(portletConfig, actionRequest, actionResponse);
    }
}
