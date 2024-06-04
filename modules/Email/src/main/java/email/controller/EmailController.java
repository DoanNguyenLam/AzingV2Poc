package email.controller;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import email.dto.ClaudeMailResDTO;
import email.dto.EmailDTO;
import email.dto.EmailDTOReq;
import email.dto.User;
import email.services.EmailServiceImpl;
import email.utils.EmailConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author doannguyenlam
 */
@Controller
@RequestMapping("VIEW")
public class EmailController {

	@Autowired
	private EmailServiceImpl emailService;

	@ModelAttribute("user")
	public User getUserModelAttribute() {
		return new User();
	}

	@RenderMapping
	public String prepareView(ModelMap modelMap, PortletRequest portletRequest) throws Exception {
		_logger.info("prepareView");

		EmailConfigs emailConfigs = new EmailConfigs();
		emailConfigs.updateProps(portletRequest);
		_logger.info("Google Client Key: {}", emailConfigs.getGoogleClientKey());
		_logger.info("Google Secret Key: {}", emailConfigs.getGoggleSecretKey());
		_logger.info("Claude API Key: {}", emailConfigs.getClaudeAPIKey());

		String view = emailService.navigateRender(modelMap, portletRequest, emailConfigs, null, null);
		return view;
	}

	@ActionMapping(params = "action=fetchData")
	public void fetchData(@RequestParam("threadId") String threadId, @RequestParam("emailId") String emailId, ModelMap modelMap, ActionResponse actionResponse,
									 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("Fetch data");
		session.setAttribute("threadId", threadId);
		session.setAttribute("emailId", emailId);

		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();
		mutableRenderParameters.setValue("javax.portlet.action", "success");
		sessionStatus.setComplete();
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String updateView(ModelMap modelMap, PortletRequest portletRequest) throws ExecutionException, InterruptedException {

		_logger.info("Update view called");
		EmailConfigs emailConfigs = new EmailConfigs();
		emailConfigs.updateProps(portletRequest);

		// Retrieve the emailDTO from the session
		PortletSession session = portletRequest.getPortletSession();
		String threadId = (String) session.getAttribute("threadId", PortletSession.PORTLET_SCOPE);
		String emailId = (String) session.getAttribute("emailId", PortletSession.PORTLET_SCOPE);
		_logger.info("Thread ID: {}, Email ID: {}", threadId, emailId);

		String view = emailService.navigateRender(modelMap, portletRequest, emailConfigs, threadId, emailId);

		return view;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		EmailController.class);

	@Autowired
	private LocalValidatorFactoryBean _localValidatorFactoryBean;

	@Autowired
	private MessageSource _messageSource;


}