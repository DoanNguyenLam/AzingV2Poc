package email.controller;

import com.liferay.portal.kernel.json.JSONObject;
import email.dto.EmailDTO;
import email.dto.EmailDTOReq;
import email.dto.User;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

/**
 * @author doannguyenlam
 */
@Controller
@RequestMapping("VIEW")
public class EmailController {

	@ModelAttribute("user")
	public User getUserModelAttribute() {
		return new User();
	}

	@RenderMapping
	public String prepareView(ModelMap modelMap) {
		_logger.info("prepareView");
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

	@ActionMapping(params = "action=fetchData")
	public void refreshMailSharedBox(@RequestParam("id") String id, ModelMap modelMap, ActionResponse actionResponse,
									 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("[ACTION MAIL SHARE] - refresh shared mailbox {}", id);
		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		session.setAttribute("userPrincipalName", id);
		mutableRenderParameters.setValue("action", "goToSharedMailBox");
		sessionStatus.setComplete();
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String showGreeting(ModelMap modelMap) {

		DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy G");

		Calendar todayCalendar = Calendar.getInstance();

		modelMap.put("todaysDate", dateFormat.format(todayCalendar.getTime()));

		return "greeting";
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