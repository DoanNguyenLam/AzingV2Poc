package email.services;

import email.dto.*;
import email.utils.EmailPortletConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class EmailService {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private AIService aiService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public String renderService(ModelMap modelMap, PortletRequest portletRequest, EmailPortletConfigs emailPortletConfigs, EmailDTO currentEmail) throws InterruptedException, ExecutionException {
        LOGGER.info("[RENDER SERVICE] - running ...");

        String accessToken = emailPortletConfigs.getGgAccessToken();
        List<EmailDTO> emailDTOList = gmailService.getListMail(accessToken);

        if (currentEmail != null) {
            LOGGER.info("[RENDER SERVICE] - id = {}, thread id = {}", currentEmail.getId(), currentEmail.getThreadId());

            Optional<EmailDTO> emailDTOOptional = emailDTOList
                    .stream()
                    .filter(emailDTO1 -> emailDTO1.getId().equals(currentEmail.getId()))
                    .findFirst();
            if (emailDTOOptional.isPresent()) {
                EmailDTO emailDTO = emailDTOOptional.get();
                List<LabelDTO> listLabel = emailDTO.getLabels();

                // TODO: call service get AI generate
                List<AILabel> aiLabelList = new ArrayList<>();
                AILabel aiLabel = new AILabel();
                aiLabel.setLabelName("AI_LABEL");
                aiLabelList.add(aiLabel);

                modelMap.put("originalEmail", emailDTO.getBodyHtml());
                modelMap.put("id", emailDTO.getId());
                modelMap.put("threadId", emailDTO.getThreadId());
                modelMap.put("labels", listLabel);
                modelMap.put("labelsAI", aiLabelList);
            }else {
                return "Mail empty";
            }

//            String bodyText;
//            boolean isThread ;
//            if (currentEmail.getId().equals(currentEmail.getThreadId())) {
//                bodyText = emailDTOOptional.get().getBodyPlainText();
//                if (bodyText == null || bodyText.isEmpty()) {
//                    bodyText = emailDTOOptional.get().getBodyHtml();
//                }
//                isThread = false;
//            } else {
//                bodyText = gmailService.getGmailThreadDetail(accessToken, currentEmail.getThreadId());
//                isThread = true;
//            }

//            String finalBodyText = bodyText;
//            CompletableFuture<ClaudeMailResDTO> summaryFuture = CompletableFuture.supplyAsync(() -> aiService.summaryAndSuggestEmail(emailPortletConfigs, finalBodyText, true, isThread));
//            CompletableFuture<ClaudeMailResDTO> suggestionFuture = CompletableFuture.supplyAsync(() -> aiService.summaryAndSuggestEmail(emailPortletConfigs, finalBodyText, false, isThread));
//
//            CompletableFuture.allOf(summaryFuture, suggestionFuture).join();
//
//            ClaudeMailResDTO summaryResponse = summaryFuture.get();
//            ClaudeMailResDTO suggestionResponse = suggestionFuture.get();
//            if (summaryResponse == null || suggestionResponse == null) {
//                LOGGER.info("[RENDER SERVICE] - error!");
//                LOGGER.error("One of the responses is null: summaryResponse={}, suggestionResponse={}", summaryResponse, suggestionResponse);
//                return "error";
//            }
//            modelMap.put("summary", summaryResponse.getContent());
//            modelMap.put("suggestion", suggestionResponse.getContent());


        }
        modelMap.put("listMails", emailDTOList);
        LOGGER.info("[RENDER SERVICE] - done!");
        return "mails";
    }
}
