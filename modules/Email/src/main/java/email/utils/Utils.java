package email.utils;

import email.dto.EmailDTO;
import email.dto.GmailDTO.GmailDetail;
import email.dto.LabelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class Utils {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final Logger _logger = LoggerFactory.getLogger(Utils.class);

    public static <T> T callApi(String url, HttpMethod httpMethod, HttpEntity<String> entity, Class<T> returnType) {
        _logger.info("[CALL API] - url: {}", url);
        _logger.info("[CALL API] - method: {}", httpMethod);
        _logger.info("[CALL API] - entity: {}", entity);
        _logger.info("[CALL API] - returnType: {}", returnType.getName());

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, returnType);
            if (response.getStatusCode() != HttpStatus.OK) {
                _logger.warn("[CALL API] - Unexpected response status: {}", response.getStatusCode());
                return null;
            }

            _logger.info("[CALL API] - success");
            _logger.info("[CALL API] - respone status: {}", response.getStatusCode());
            _logger.info("[CALL API] - respone header: {}", response.getHeaders());
            _logger.info("[CALL API] - respone body: {}", response.getBody());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            _logger.error("[CALL API] - Client error when calling API: {}, Status code: {}, Response body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            _logger.error("[CALL API] - Server error when calling API: {}, Status code: {}, Response body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            _logger.error("[CALL API] - Error occurred when calling API: {}", url, e);
        } catch (Exception e) {
            _logger.error("[CALL API] - Unexpected error occurred when calling API: {}", url, e);
        }
        return null;
    }

    public static String decodeGmailBase64(String encodedString){
        try {
            if (encodedString == null) return "";
            // special handle for base64 from gmail body
            encodedString = encodedString.replace('-', '+').replace('_', '/');
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            return new String(decodedBytes);
        }catch (Exception e){
            _logger.info("decode exception: " + e);
            return "";
        }
    }

    public static List<LabelDTO> getLabelNames(List<LabelDTO> labelDTOs, String[] labelIds) {
        List<LabelDTO> filteredList = new ArrayList<>();
        for (LabelDTO label : labelDTOs) {
            for (String id : labelIds) {
                if (label.getId().equals(id)) {
                    filteredList.add(label);
                    break; // No need to check further for this label
                }
            }
        }
        return filteredList;
    }

    public static EmailDTO processEmailDTO(GmailDetail gmailDetail, List<LabelDTO> listLabels) {
        try {
            EmailDTO emailDTO = new EmailDTO();
            if (gmailDetail == null) return emailDTO;

            emailDTO.setId(gmailDetail.getId());
            emailDTO.setThreadId(gmailDetail.getThreadId());
            emailDTO.setSnippet(gmailDetail.getSnippet());

            Optional<String> subject = gmailDetail.getPayload().getHeaders().stream().filter(headers -> Objects.equals(headers.getName(), "Subject")).map(GmailDetail.Headers::getValue).findFirst();
            subject.ifPresent(emailDTO::setSubject);

            Optional<String> sender = gmailDetail.getPayload().getHeaders().stream().filter(headers -> Objects.equals(headers.getName(), "From")).map(GmailDetail.Headers::getValue).findFirst();
            sender.ifPresent(emailDTO::setSendFrom);

            Optional<String> receiver = gmailDetail.getPayload().getHeaders().stream().filter(headers -> Objects.equals(headers.getName(), "To")).map(GmailDetail.Headers::getValue).findFirst();
            receiver.ifPresent(emailDTO::setSendTo);

            Optional<String> date = gmailDetail.getPayload().getHeaders().stream().filter(headers -> Objects.equals(headers.getName(), "Date")).map(GmailDetail.Headers::getValue).findFirst();
            date.ifPresent(emailDTO::setDate);

            emailDTO.setLabels(getLabelNames(listLabels, gmailDetail.getLabelIds()));

            if (gmailDetail.getPayload().getParts() == null || gmailDetail.getPayload().getParts().isEmpty()) {
                String body = gmailDetail.getPayload().getBody().getData();
                if( body != null && !body.isEmpty()) {
                    String bodyHtml = Utils.decodeGmailBase64(body);
                    emailDTO.setBodyHtml(bodyHtml);
                }
                return emailDTO;
            }

            Optional<String> bodyPlainText = gmailDetail.getPayload().getParts().stream()
                    .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/plain"))
                    .map(GmailDetail.Parts::getBody)
                    .map(GmailDetail.Body::getData)
                    .map(item -> Utils.decodeGmailBase64(item))
                    .findFirst();

            bodyPlainText.ifPresent(emailDTO::setBodyPlainText);

            Optional<String> bodyHtml = gmailDetail.getPayload().getParts().stream()
                    .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/html"))
                    .map(GmailDetail.Parts::getBody)
                    .map(GmailDetail.Body::getData)
                    .map(item -> Utils.decodeGmailBase64(item))
                    .findFirst();

            bodyHtml.ifPresent(emailDTO::setBodyHtml);

            return emailDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            return new EmailDTO();
        }
    }
}
