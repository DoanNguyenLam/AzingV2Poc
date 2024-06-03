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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

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

		List<EmailDTO> emailDTOList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			EmailDTO emailDTO = new EmailDTO();
			emailDTO.setSubject("Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fugit fugiat id porro, laborum dolorem minima eos nulla ratione a obcaecati non, iusto eum enim alias corrupti saepe eaque tenetur sequi.");
			emailDTO.setData("Sample email data " + i);
			emailDTO.setDate("2024-05-0" + i);
			emailDTOList.add(emailDTO);
		}
		modelMap.put("listEmails", emailDTOList);

		// TODO: implement get mail body by thread
		String mailBody = "Hey cabien1307!\n" +
				"\n" +
				"You’ve just enabled two-factor authentication.\n" +
				"\n" +
				"Please take a moment to check that you have saved your recovery codes in a safe place. You can\n" +
				"download your recovery codes at:\n" +
				"\n" +
				"https://github.com/settings/auth/recovery-codes\n" +
				"\n" +
				"Recovery codes are the only way to access your account again. By saving your\n" +
				"recovery codes, you’ll be able to regain access if you:\n" +
				"\n" +
				"* Lose your phone\n" +
				"* Delete your authenticator app\n" +
				"* Change your phone number\n" +
				"\n" +
				"GitHub Support will not be able to restore access to your account.\n" +
				"\n" +
				"To disable two-factor authentication, visit\n" +
				"https://github.com/settings/security\n" +
				"\n" +
				"More information about two-factor authentication can be found on GitHub Help at\n" +
				"https://docs.github.com/articles/about-two-factor-authentication\n" +
				"\n" +
				"If you have any questions, please visit https://support.github.com.\n" +
				"\n" +
				"Thanks,\n" +
				"Your friends at GitHub";


		// summary email
		CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return emailService.summaryAndSuggestEmail(mailBody,true, emailConfigs.getClaudeAPIKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
		CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return emailService.summaryAndSuggestEmail(mailBody,false, emailConfigs.getClaudeAPIKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

		CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

		ClaudeMailResDTO summaryResponse = summaryFuture.get();
		ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
		_logger.info("summaryResponse {}", summaryResponse.getContent());
		_logger.info("suggestionResponse {}", suggestionResponse.getContent());

		return "mails";
	}

	@ActionMapping(params = "action=fetchData")
	public void refreshMailSharedBox(@RequestParam("id") String id, ModelMap modelMap, ActionResponse actionResponse,
									 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("[ACTION MAIL SHARE] - refresh shared mailbox {}", id);
		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		session.setAttribute("userPrincipalName", id);
		mutableRenderParameters.setValue("action", "success");
		sessionStatus.setComplete();
	}

	@ActionMapping(params = "action=summaryMail")
	public List<ClaudeMailResDTO> summaryMail(@RequestParam("thread-id") String threadId, ModelMap modelMap, ActionResponse actionResponse,
											  SessionStatus sessionStatus, PortletSession session, PortletRequest portletRequest) throws Exception{
		_logger.info("[SUMMARY EMAIL] - threadId: {}", threadId);

		// TODO: implement get mail body by thread
		String mailBody = "sample";

		// TODO: config claude api key
		EmailConfigs emailConfigs = new EmailConfigs();
		emailConfigs.updateProps(portletRequest);

		// summary email
		CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return emailService.summaryAndSuggestEmail(mailBody,true, emailConfigs.getClaudeAPIKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
		CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return emailService.summaryAndSuggestEmail(mailBody,false, emailConfigs.getClaudeAPIKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

		CompletableFuture.allOf(summaryFuture, suggestionFuture).join();

		ClaudeMailResDTO summaryResponse = summaryFuture.get();
		ClaudeMailResDTO suggestionResponse = suggestionFuture.get();

		return Arrays.asList(summaryResponse, suggestionResponse);
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String showGreeting(ModelMap modelMap) {

		_logger.info("RenderMapping success");
		List<EmailDTO> emailDTOList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			EmailDTO emailDTO = new EmailDTO();
			emailDTO.setSubject("Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fugit fugiat id porro, laborum dolorem minima eos nulla ratione a obcaecati non, iusto eum enim alias corrupti saepe eaque tenetur sequi.");
			emailDTO.setData("Sample email data " + i);
			emailDTO.setDate("2024-05-0" + i);
			emailDTOList.add(emailDTO);
		}
		modelMap.put("listEmails", emailDTOList);
		return "mails";
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