package email.controller;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import email.dto.EmailDTO;
import email.dto.LabelDTO;
import email.dto.UpdateLabelReqDTO;
import email.dto.User;
import email.services.EmailService;
import email.utils.EmailPortletConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.inject.Singleton;
import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * @author doannguyenlam
 */
@Controller
@RequestMapping("VIEW")
public class EmailController {

	@Autowired
	private EmailService emailService;

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
            return emailService.renderService(modelMap, emailPortletConfigs, null);
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

	@ActionMapping(params = "action=addLabel")
	public void addLabel(@RequestParam("id") String id,
							@RequestParam("thread-id") String threadId,
							@RequestParam("labelName") String labelName,
							ModelMap modelMap,
							ActionResponse actionResponse,
						  SessionStatus sessionStatus, PortletSession session) {
		_logger.info("[ADD LABEL AI] - id: [{}] - threadId: [{}] - labelName: [{}]", id, threadId, labelName);

		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		session.setAttribute("id", id);
		session.setAttribute("threadId", threadId);
		session.setAttribute("labelName", labelName);
		session.setAttribute("isAddLabel", true);

		mutableRenderParameters.setValue("javax.portlet.action", "updateLabelRender");
		sessionStatus.setComplete();
	}

	@ActionMapping(params = "action=removeLabel")
	public void removeLabel(@RequestParam("id") String id,
						 @RequestParam("thread-id") String threadId,
						 @RequestParam("labelId") String labelId,
						 ModelMap modelMap,
						 ActionResponse actionResponse,
						 SessionStatus sessionStatus, PortletSession session) {
		_logger.info("[REMOVE LABEL GG] - id: [{}] - threadId: [{}] - labelId: [{}]", id, threadId, labelId);

		MutableRenderParameters mutableRenderParameters =
				actionResponse.getRenderParameters();

		session.setAttribute("id", id);
		session.setAttribute("threadId", threadId);
		session.setAttribute("labelId", labelId);
		session.setAttribute("isAddLabel", false);

		mutableRenderParameters.setValue("javax.portlet.action", "updateLabelRender");
		sessionStatus.setComplete();
	}

	// TODO: It costs a lot to retrieve summary and suggestions
	@RenderMapping(params = "javax.portlet.action=updateLabelRender")
	public String updateLabelView(ModelMap modelMap, PortletRequest portletRequest) throws ExecutionException, InterruptedException {
		_logger.info("[UPDATE LABEL VIEW] - Update label view called");

		EmailPortletConfigs emailPortletConfigs = new EmailPortletConfigs();
		emailPortletConfigs.updateProps(portletRequest);

		PortletSession session = portletRequest.getPortletSession();
		String emailId = (String) session.getAttribute("id", PortletSession.PORTLET_SCOPE);
		String threadId = (String) session.getAttribute("threadId", PortletSession.PORTLET_SCOPE);
		String labelName = (String) session.getAttribute("labelName", PortletSession.PORTLET_SCOPE);
		String labelId = (String) session.getAttribute("labelId", PortletSession.PORTLET_SCOPE);
		boolean isAddLabel = (boolean) session.getAttribute("isAddLabel", PortletSession.PORTLET_SCOPE);
		_logger.info("[UPDATE LABEL VIEW] - Email ID: {}", emailId);
		_logger.info("[UPDATE LABEL VIEW] - Thread ID: {}", threadId);
		_logger.info("[UPDATE LABEL VIEW] - Is add label {}", isAddLabel);
		_logger.info("[UPDATE LABEL VIEW] - Label name: {}", labelName);
		_logger.info("[UPDATE LABEL VIEW] - Label id: {}", labelId);

		UpdateLabelReqDTO req = new UpdateLabelReqDTO();
		if (isAddLabel) {
			/**
			 * TODO
			 * - call api create new label with label name DONE
			 * - get new ID after create
			 * - call service update label (add)
			 * */
			_logger.info("[UPDATE LABEL VIEW] - Add label name: {}", labelName);
			req.setAddLabelIds(Collections.singletonList(labelName));
			req.setRemoveLabelIds(new ArrayList<>());
			emailService.addLabel(emailPortletConfigs, emailId, req);

		} else {
			_logger.info("[UPDATE LABEL VIEW] - Remove label id: {}", labelId);
			// TODO: call service update label (remove)
			req.setAddLabelIds(new ArrayList<>());
			req.setRemoveLabelIds(Collections.singletonList(labelId));
			boolean isUpdate = emailService.removeLabel(emailPortletConfigs, emailId, req);
			if (!isUpdate) return "error";
		}

		EmailDTO currentEmail = new EmailDTO();
		currentEmail.setId(emailId);
		currentEmail.setThreadId(threadId);
		currentEmail.setLabels(Collections.singletonList(new LabelDTO()));

		String view = emailService.renderService(modelMap, emailPortletConfigs, currentEmail);

		return view;
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

		String view = emailService.renderService(modelMap, emailPortletConfigs, emailDTO);

		return view;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		EmailController.class);


}