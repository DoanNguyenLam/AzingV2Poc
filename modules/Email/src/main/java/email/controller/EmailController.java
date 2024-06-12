package email.controller;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import email.dto.EmailDTO;
import email.dto.User;
import email.services.EmailServiceImpl;
import email.utils.EmailPortletConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
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

		EmailPortletConfigs emailPortletConfigs = new EmailPortletConfigs();
		emailPortletConfigs.updateProps(portletRequest);

		try {
            return emailService.renderService(modelMap, portletRequest, emailPortletConfigs, null);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}

	@ActionMapping(params = "action=fetchData")
	public void fetchData(@RequestParam("id") String id, @RequestParam("thread-id") String threadId, ModelMap modelMap, ActionResponse actionResponse,
									 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("[FETCH DATA ACTION] - id: [{}] - threadId: [{}]", id, threadId);

		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		session.setAttribute("id", id);
		session.setAttribute("threadId", threadId);

		mutableRenderParameters.setValue("javax.portlet.action", "success");
		sessionStatus.setComplete();
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String updateView(ModelMap modelMap, PortletRequest portletRequest) throws ExecutionException, InterruptedException {
		_logger.info("Update view called");

		PortletSession session = portletRequest.getPortletSession();
		String threadId = (String) session.getAttribute("threadId", PortletSession.PORTLET_SCOPE);
		String emailId = (String) session.getAttribute("id", PortletSession.PORTLET_SCOPE);
		_logger.info("[UPDATE VIEW] - Thread ID: {}, Email ID: {}", threadId, emailId);

		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setId(emailId);
		emailDTO.setThreadId(threadId);

		EmailPortletConfigs emailPortletConfigs = new EmailPortletConfigs();
		emailPortletConfigs.updateProps(portletRequest);

		String view = emailService.renderService(modelMap, portletRequest, emailPortletConfigs, emailDTO);

		return view;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		EmailController.class);


}