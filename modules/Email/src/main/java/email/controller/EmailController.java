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
import java.util.*;
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

		try {
            return emailService.renderService(modelMap, portletRequest, emailConfigs, null);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}

	@ActionMapping(params = "action=fetchData")
	public void fetchData(@RequestParam("id") String id, ModelMap modelMap, ActionResponse actionResponse,
									 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("Fetch data: {}", id);
		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		mutableRenderParameters.setValue("javax.portlet.action", "success");
		sessionStatus.setComplete();
	}

	@ActionMapping(params = "action=summaryMail")
	public List<ClaudeMailResDTO> summaryMail(@RequestParam("thread-id") String threadId, @RequestParam("message-id") String messageId,ModelMap modelMap, ActionResponse actionResponse,
											  SessionStatus sessionStatus, PortletSession session, PortletRequest portletRequest) throws Exception{
		_logger.info("[SUMMARY EMAIL] - threadId: {}", threadId);

		// TODO: config claude api key
		EmailConfigs emailConfigs = new EmailConfigs();
		emailConfigs.updateProps(portletRequest);

		String mailBody = emailService.getThreadDetail(emailConfigs.getGmailAccessToken(), threadId);

		boolean isThread = !Objects.equals(threadId, messageId);

        // summary email
		CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> emailService.summaryAndSuggestEmail(mailBody,true, emailConfigs.getClaudeAPIKey(), isThread));
		CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> emailService.summaryAndSuggestEmail(mailBody,false, emailConfigs.getClaudeAPIKey(), isThread));

		CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

		ClaudeMailResDTO summaryResponse = summaryFuture.get();
		ClaudeMailResDTO suggestionResponse = suggestionFuture.get();

		return Arrays.asList(summaryResponse, suggestionResponse);
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String updateView(ModelMap modelMap, PortletRequest portletRequest) throws ExecutionException, InterruptedException {

		_logger.info("Update view called");
		EmailDTO emailDTO = new EmailDTO();
		EmailConfigs emailConfigs = new EmailConfigs();
		emailConfigs.updateProps(portletRequest);

		String view = emailService.renderService(modelMap, portletRequest, emailConfigs, emailDTO);

		return view;
	}

	@ActionMapping(params = "action=summary")
	@ResponseBody
	public String handleSummary(@RequestBody EmailDTOReq emailDTOReq) {
		_logger.info("handleSummary");
		// Process the data
		_logger.info("emailDTOReq {}", emailDTOReq);
		return "Data received successfully!";
	}

	@ActionMapping(params = "action=submitData")
	public void handleData(
			@ModelAttribute("user") User user, BindingResult bindingResult,
			ModelMap modelMap, Locale locale, ActionResponse actionResponse,
			SessionStatus sessionStatus) {
		// Process the data
		_logger.info("FK form");
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

	private static final Logger _logger = LoggerFactory.getLogger(
		EmailController.class);

	@Autowired
	private LocalValidatorFactoryBean _localValidatorFactoryBean;

	@Autowired
	private MessageSource _messageSource;


}