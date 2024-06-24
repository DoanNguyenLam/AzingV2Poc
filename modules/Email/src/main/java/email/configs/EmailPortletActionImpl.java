package email.configs;

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

import static email.configs.EmailConfigKeys.*;


@Component(
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        immediate = true,
        properties = "javax.portlet.name=" + EmailConfigKeys.PORTLET_NAME,
        service = ConfigurationAction.class
)
public class EmailPortletActionImpl extends DefaultConfigurationAction {
    private static final Logger _log = LoggerFactory.getLogger(EmailPortletActionImpl.class);

    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        String ggClientKey = ParamUtil.getString(actionRequest, GG_CLIENT_KEY);
        String ggSecretKey = ParamUtil.getString(actionRequest, GG_SECRET_KEY);
        String claudeAPIKey = ParamUtil.getString(actionRequest, CLAUDE_API_KEY);
        String gptAPIKey = ParamUtil.getString(actionRequest, GPT_API_KEY);
        String ggAccessToken = ParamUtil.getString(actionRequest, GG_ACCESS_TOKEN);
        String ggRefreshToken = ParamUtil.getString(actionRequest, GG_REFRESH_TOKEN);
        String modal = ParamUtil.getString(actionRequest, MODAL);
        String promptSummarySingleMail = ParamUtil.getString(actionRequest, PROMPT_SUMMARY_SINGLE_MAIL);
        String promptSuggestionSingleMail = ParamUtil.getString(actionRequest, PROMPT_SUGGESTION_SINGLE_MAIL);
        String promptSummaryConversation = ParamUtil.getString(actionRequest, PROMPT_SUMMARY_SINGLE_MAIL);
        String promptSuggestionConversation = ParamUtil.getString(actionRequest, PROMPT_SUGGESTION_SINGLE_MAIL);

        setPreference(actionRequest, GG_CLIENT_KEY, ggClientKey);
        setPreference(actionRequest, GG_SECRET_KEY, ggSecretKey);
        setPreference(actionRequest, CLAUDE_API_KEY, claudeAPIKey);
        setPreference(actionRequest, GPT_API_KEY, gptAPIKey);
        setPreference(actionRequest, GG_ACCESS_TOKEN, ggAccessToken);
        setPreference(actionRequest, GG_REFRESH_TOKEN, ggRefreshToken);
        setPreference(actionRequest, MODAL, String.valueOf(modal));
        setPreference(actionRequest, PROMPT_SUMMARY_SINGLE_MAIL, promptSummarySingleMail);
        setPreference(actionRequest, PROMPT_SUGGESTION_SINGLE_MAIL, promptSuggestionSingleMail);
        setPreference(actionRequest, PROMPT_SUMMARY_CONVERSATION, promptSummaryConversation);
        setPreference(actionRequest, PROMPT_SUGGESTION_CONVERSATION, promptSuggestionConversation);

        _log.info("GG CLIENT KEY: {}", ggClientKey);
        _log.info("GG SECRET KEY: {}", ggSecretKey);
        _log.info("CLAUDE API KEY: {}", claudeAPIKey);
        _log.info("GPT API KEY: {}", gptAPIKey);
        _log.info("GG ACCESS TOKEN: {}", ggAccessToken);
        _log.info("GG REFRESH_TOKEN: {}", ggRefreshToken);
        _log.info("USING MODAL: {}", modal);
        _log.info("PROMPT_SUMMARY_SINGLE_MAIL: {}", promptSummarySingleMail);
        _log.info("PROMPT_SUGGESTION_SINGLE_MAIL: {}", promptSuggestionSingleMail);
        _log.info("PROMPT_SUMMARY_CONVERSATION: {}", promptSummaryConversation);
        _log.info("PROMPT_SUGGESTION_CONVERSATION: {}", promptSuggestionConversation);


        super.processAction(portletConfig, actionRequest, actionResponse);
    }
}
