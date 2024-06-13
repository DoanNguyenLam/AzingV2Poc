package email.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.EmailDTO;
import email.dto.GmailDTO.GmailDetail;
import email.dto.GmailDTO.GmailMessageIds;
import email.dto.GmailDTO.GmailThreadDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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

        // Get unread email
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + userId + "/messages"  + "?q=is:unread";

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

            if (gmailDetail.getPayload().getParts() == null || gmailDetail.getPayload().getParts().isEmpty()) {
                return emailDTO;
            }

            Optional<String> bodyPlainText = gmailDetail.getPayload().getParts().stream()
                    .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/plain"))
                    .map(GmailDetail.Parts::getBody)
                    .map(GmailDetail.Body::getData)
                    .map(this::decodeGmailBase64)
                    .findFirst();

            bodyPlainText.ifPresent(emailDTO::setBodyPlainText);

            Optional<String> bodyHtml = gmailDetail.getPayload().getParts().stream()
                    .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/html"))
                    .map(GmailDetail.Parts::getBody)
                    .map(GmailDetail.Body::getData)
                    .map(this::decodeGmailBase64)
                    .findFirst();

            bodyHtml.ifPresent(emailDTO::setBodyHtml);

            return emailDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            return new EmailDTO();
        }

    }

    public String getGmailThreadDetail(String accessToken, String threadId){
        String userId = this.getUserId(accessToken);
        if (userId == null || userId.isEmpty()){
            return null;
        }

        String url = "https://gmail.googleapis.com/gmail/v1/users/" + userId + "/threads/" + threadId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GmailThreadDetail> response = restTemplate.exchange(url, HttpMethod.GET, entity, GmailThreadDetail.class);
        if (response.getBody() == null || response.getStatusCode() != HttpStatus.valueOf(200)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        GmailThreadDetail threadDetails = response.getBody();
        if (threadDetails == null || threadDetails.getMessages().isEmpty()){
            return "";
        }

        List<GmailDetail> gmailDetails = threadDetails.getMessages();
        GmailDetail gmailDetail = gmailDetails.get(gmailDetails.size() -1);

        if (gmailDetail.getPayload().getParts() == null || gmailDetail.getPayload().getParts().isEmpty()) {
            return "";
        }

        Optional<String> bodyPlainText = gmailDetail.getPayload().getParts().stream()
                .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/plain"))
                .map(GmailDetail.Parts::getBody)
                .map(GmailDetail.Body::getData)
                .map(this::decodeGmailBase64)
                .findFirst();

        return bodyPlainText.orElse("");
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


    public String decodeGmailBase64(String encodedString){
        try {
            if (encodedString == null) return "";
            // special handle for base64 from gmail body
            encodedString = encodedString.replace('-', '+').replace('_', '/');
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            return new String(decodedBytes);
        }catch (Exception e){
            LOGGER.info("decode exception: " + e);
            return "";
        }
    }

    private final String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";
    private final String SCOPES = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.labels";

    private final String REDIRECT_URI = "http://localhost:8080/api/gmail/access-token";

    public String gmailOauthUrl(){
        return OAUTH_URL + "?" + "response_type=code" + "&client_id=" + CLIENT_ID + "&scope=" + SCOPES + "&redirect_uri=" + REDIRECT_URI;
    }

    public String getAccessToken(String authorizationCode) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("client_id", CLIENT_ID);
        requestBody.put("client_secret", CLIENT_SECRET);
        requestBody.put("redirect_uri", REDIRECT_URI);
        requestBody.put("code", authorizationCode);
        requestBody.put("grant_type", "authorization_code");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getBody() == null || response.getStatusCode() != HttpStatus.OK) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gmail unauthorized");
        }

        Map<String, Object> responseBody = response.getBody();

        return (String) responseBody.get("access_token");
    }

}
