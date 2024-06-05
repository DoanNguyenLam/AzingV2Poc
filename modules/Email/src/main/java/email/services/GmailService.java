package email.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.EmailDTO;
import email.dto.GmailDTO.GmailDetail;
import email.dto.GmailDTO.GmailMessageIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class GmailService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    public List<EmailDTO> getListMail(String accessToken) {
        LOGGER.info("Get gmail list ID by access token");

        String userId = this.getUserId(accessToken);
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + userId + "/messages";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GmailMessageIds> response = restTemplate.exchange(url, HttpMethod.GET, entity, GmailMessageIds.class);

        if (response.getBody() == null || response.getStatusCode() != HttpStatus.valueOf(200)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<GmailMessageIds.GmailMessage> listMailIds = response.getBody().getGmailMessageList().subList(0, 9);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Use CompletableFuture to call getListMailDetail concurrently
        List<CompletableFuture<EmailDTO>> futures = listMailIds.stream()
                .map(gmailMessage -> CompletableFuture.supplyAsync(() -> getListMailDetail(accessToken, userId, gmailMessage), executorService))
                .collect(Collectors.toList());

        // Wait for all futures to complete and collect the results

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }


    public EmailDTO getListMailDetail(String accessToken, String userId, GmailMessageIds.GmailMessage gmailIdsInfo) {
        LOGGER.info("Get mail details of mailId {}", gmailIdsInfo.getId());
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + userId + "/messages/" + gmailIdsInfo.getId();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GmailDetail> response = restTemplate.exchange(url, HttpMethod.GET, entity, GmailDetail.class);
        if (response.getBody() == null || response.getStatusCode() != HttpStatus.valueOf(200)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        GmailDetail gmailDetail = response.getBody();
        return proccessAzingMailFromGMail(gmailDetail);
    }

    public EmailDTO proccessAzingMailFromGMail(GmailDetail gmailDetail) {
        EmailDTO emailDTO = new EmailDTO();
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

//        Optional<String> bodyPlainText = gmailDetail.getPayload().getParts().stream()
//                .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/plain"))
//                .map(GmailDetail.Parts::getBody)
//                .map(GmailDetail.Body::getData)
//                .findFirst();

//        if (bodyPlainText.isPresent()) {
//            String decodedBodyPlainText = this.decodeBase64(bodyPlainText.toString());
//            emailDTO.setBodyPlainText(decodedBodyPlainText);
//        }


//        Optional<String> bodyHtml = gmailDetail.getPayload().getParts().stream()
//                .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/html"))
//                .map(GmailDetail.Parts::getBody)
//                .map(GmailDetail.Body::getData)
//                .findFirst();

//        if (bodyHtml.isPresent()) {
//            String decodedBodyHtml = this.decodeBase64(bodyHtml.toString());
//            emailDTO.setBodyPlainText(decodedBodyHtml);
//        }

        return emailDTO;
    }

    public String getUserId(String accessToken) {
        LOGGER.info("Get Gmail userId");

        String url = "https://www.googleapis.com/oauth2/v1/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Parse the JSON response to extract the ID
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("id").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decodeBase64(String base64EncodedString) {
        try {
            if (base64EncodedString == null || base64EncodedString.isEmpty()) {
                return "Empty body";
            }
            // Remove whitespace characters using streams and lambdas
            String cleanedString = base64EncodedString.chars()
                    .filter(c -> !Character.isWhitespace(c))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // Corrected line
                    .toString();

            // Allowed characters in Base64
            Set<Character> allowedChars = new HashSet<>();
            for (char c = 'A'; c <= 'Z'; c++) {
                allowedChars.add(c);
            }
            for (char c = 'a'; c <= 'z'; c++) {
                allowedChars.add(c);
            }
            for (char c = '0'; c <= '9'; c++) {
                allowedChars.add(c);
            }
            allowedChars.add('+');
            allowedChars.add('/');
            allowedChars.add('=');

            // Remove invalid characters using streams and lambdas
            cleanedString = cleanedString.chars()
                    .filter(c -> allowedChars.contains((char) c))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // Corrected line
                    .toString();

            // Check if the length is a multiple of 4
            int mod = cleanedString.length() % 4;

            // Pad the string with '=' if necessary
            StringBuilder padding = new StringBuilder();
            for (int i = 0; i < 4 - (mod % 4); i++) {
                padding.append("=");
            }
            cleanedString += padding.toString();

            // Try decoding with standard Base64 (might need adjustment)
            byte[] decodedBytes = Base64.getDecoder().decode(cleanedString);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            if (decodedString.isEmpty()) {
                // Try decoding with Base64URL (if standard Base64 fails)
                decodedBytes = Base64.getUrlDecoder().decode(cleanedString);
                decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            }

            return decodedString;
        } catch (Exception e) {
            LOGGER.info("Exception: " + e);
            return "invalid base64 cannot be repair";
        }
    }
}
