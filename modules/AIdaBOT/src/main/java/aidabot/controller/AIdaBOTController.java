package aidabot.controller;

import aidabot.dto.User;

import aidabot.utils.AIdaBOTConfig;
import aidabot.utils.Validation;
import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;

import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import static aidabot.configs.AIdaBOTConfigKeys.SCRIPT;

/**
 * @author doannguyenlam
 */
@Controller
@RequestMapping("VIEW")
public class AIdaBOTController {

	@ModelAttribute("user")
	public User getUserModelAttribute() {
		return new User();
	}

	private static final Logger _logger = LoggerFactory.getLogger(
			AIdaBOTController.class);

//	@RenderMapping
//	public String prepareView() {
//		return "user";
//	}

	@RenderMapping()
	public String firstRenderView(ModelMap modelMap, PortletRequest portletRequest) {
		final String _TAG = "[RENDER] - ";
		_logger.info(_TAG + "index view");

		AIdaBOTConfig aidabotConfig = new AIdaBOTConfig();
		aidabotConfig.updateProp(portletRequest);


		String script = "";
		String url = "";

		if (Validation.validateStr(aidabotConfig.getScript(), Validation.SCRIPT_PATTERN)) {
			script = aidabotConfig.getScript();
		}

		if (Validation.validateStr(aidabotConfig.getUrl(), Validation.URL_PATTERN)) {
			url = aidabotConfig.getUrl();
		}

		modelMap.put("script", script);
		modelMap.put("url", url);
		_logger.info(_TAG + "script: {}", script);
		_logger.info(_TAG + "url: {}", url);
		return "embed";
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String showGreeting(ModelMap modelMap) {

		DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy G");

		Calendar todayCalendar = Calendar.getInstance();

		modelMap.put("todaysDate", dateFormat.format(todayCalendar.getTime()));

		return "greeting";
	}

	@ActionMapping
	public void submitApplicant(
		@ModelAttribute("user") User user, BindingResult bindingResult,
		ModelMap modelMap, Locale locale, ActionResponse actionResponse,
		SessionStatus sessionStatus) {

		_localValidatorFactoryBean.validate(user, bindingResult);

		if (!bindingResult.hasErrors()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("firstName=" + user.getFirstName());
				_logger.debug("lastName=" + user.getLastName());
			}

			MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

			mutableRenderParameters.setValue("javax.portlet.action", "success");

			sessionStatus.setComplete();
		}
		else {
			bindingResult.addError(
				new ObjectError(
					"user",
					_messageSource.getMessage(
						"please-correct-the-following-errors", null, locale)));
		}
	}

	@Autowired
	private LocalValidatorFactoryBean _localValidatorFactoryBean;

	@Autowired
	private MessageSource _messageSource;

}