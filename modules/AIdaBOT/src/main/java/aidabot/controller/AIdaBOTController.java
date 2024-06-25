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


		String script = aidabotConfig.getScript();
		String url = aidabotConfig.getUrl();

		modelMap.put("script", script);
		modelMap.put("url", url);
		_logger.info(_TAG + "script: {}", script);
		_logger.info(_TAG + "url: {}", url);
		return "embed";
	}

	@Autowired
	private LocalValidatorFactoryBean _localValidatorFactoryBean;

	@Autowired
	private MessageSource _messageSource;

}