package email.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.*;
import email.dto.GmailDTO.GmailAccessTokenDTO;
import email.dto.GmailDTO.GmailDetail;
import email.dto.GmailDTO.GmailMessageIds;
import email.dto.GmailDTO.GmailThreadDetail;
import email.utils.Constant;
import email.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public String gmailOauthUrl(){
        return Constant.OAUTH_URL + "?" + "response_type=code" + "&client_id=" + Constant.CLIENT_ID + "&scope=" + Constant.SCOPES + "&redirect_uri=" + Constant.REDIRECT_URI;
    }

    private static final String REFRESH_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";

    public String getNewAccessToken(String clientId, String clientSecret, String refreshToken){
        LOGGER.info("Get new access token");
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("refresh_token", refreshToken);
            requestBody.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Make the HTTP POST request
            ResponseEntity<GmailAccessTokenDTO> responseEntity = restTemplate.postForEntity(REFRESH_TOKEN_URL, request, GmailAccessTokenDTO.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            return responseEntity.getBody().getAccessToken();
        }catch (Exception e){
            LOGGER.info("Get new access token exception : ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public String getAccessToken(String authorizationCode) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("client_id", Constant.CLIENT_ID);
        requestBody.put("client_secret", Constant.CLIENT_SECRET);
        requestBody.put("redirect_uri", Constant.REDIRECT_URI);
        requestBody.put("code", authorizationCode);
        requestBody.put("grant_type", "authorization_code");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                Constant.TOKEN_URL,
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

    public String getUserId(String accessToken) {
        LOGGER.info("[GET USER] - Get Gmail userId");

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

    public List<EmailDTO> getListMail(String accessToken) {
        LOGGER.info("[GET LIST MAIL] - Get gmail list ID by access token");

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
        List<LabelDTO> labelDTOS = getListLabels(accessToken);
        LOGGER.info("[GET LIST MAIL] - get list labels {}", labelDTOS.stream().map(LabelDTO::getName).collect(Collectors.toList()));

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Use CompletableFuture to call getListMailDetail concurrently
        List<CompletableFuture<EmailDTO>> futures = listMailIds.stream()
                .map(gmailMessage ->
                        CompletableFuture.supplyAsync(() ->
                                getListMailDetail(
                                        accessToken,
                                        userId,
                                        gmailMessage,
                                        labelDTOS),
                                executorService))
                .collect(Collectors.toList());

        // Wait for all futures to complete and collect the results

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public EmailDTO getListMailDetail(String accessToken, String userId, GmailMessageIds.GmailMessage gmailIdsInfo, List<LabelDTO> listLabel) {
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
        EmailDTO emailDTO = Utils.processEmailDTO(gmailDetail, listLabel);
        return emailDTO;
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
            String body = gmailDetail.getPayload().getBody().getData();
            if( body != null && !body.isEmpty()) {
                return Utils.decodeGmailBase64(body);
            }
            return "";
        }

        Optional<String> bodyPlainText = gmailDetail.getPayload().getParts().stream()
                .filter(partItem -> Objects.equals(partItem.getMimeType(), "text/plain"))
                .map(GmailDetail.Parts::getBody)
                .map(GmailDetail.Body::getData)
                .map(item -> Utils.decodeGmailBase64(item))
                .findFirst();

        return bodyPlainText.orElse("");
    }

    public List<LabelDTO> getListLabels(String accessToken) {
        LOGGER.info("[GET LABEL] - get list labels");

        String userId = this.getUserId(accessToken);
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }

        String url = "https://gmail.googleapis.com/gmail/v1/users/"+ userId + "/labels";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        LabelResponseDTO response = Utils.callApi(url, HttpMethod.GET, entity, LabelResponseDTO.class);

        if (response.getLabels() == null || response.getLabels().isEmpty()) {
            LOGGER.warn("[GET LABEL] - list label not founds");
            return new ArrayList<>();
        }

        return response.getLabels();
    }

    public void processGetNewLabels(String accessToken, List<String> currentLabels) {

        List<LabelDTO> oldLabelDTOs = this.getListLabels(accessToken);
        if (oldLabelDTOs.isEmpty()){
            return;
        }

        List<String> oldLabelName = oldLabelDTOs.stream().map(LabelDTO::getName).collect(Collectors.toList());
        if (oldLabelName.isEmpty()){
            return;
        }

        List<String> newLabels = currentLabels.stream().filter(label -> !oldLabelName.contains(label)).collect(Collectors.toList());
        if (newLabels.isEmpty()){
            return;
        }

//        this.createNewLabel(accessToken, newLabels);
    }

    public LabelDTO createNewLabel(String accessToken, String label){
        LOGGER.info("[CREATE LABEL] - new labels [{}]", label);

        String userId = this.getUserId(accessToken);
        if (userId == null || userId.isEmpty()) {
            return null;
        }

        String url = "https://gmail.googleapis.com/gmail/v1/users/"+ userId + "/labels";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");

        String jsonBody = "{ \"name\": \"" + label + "\" }";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        LabelDTO response = Utils.callApi(url, HttpMethod.POST, entity, LabelDTO.class);
        LOGGER.info("[CREATE LABEL] - success");

        return response;
    }

    public void updateLabelEmail(String accessToken, String messageId, UpdateLabelReqDTO req) {
        LOGGER.info("[UPDATE LABEL EMAIL] - Update Label Email Request: {}", req);

        String userId = this.getUserId(accessToken);
        if (userId == null || userId.isEmpty()) {
            return;
        }

        String url = "https://gmail.googleapis.com/gmail/v1/users/"+ userId + "/messages/" + messageId + "/modify";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Application");

        ObjectMapper mapper = new ObjectMapper();

        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(req);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        UpdateLabelResDTO response = Utils.callApi(url, HttpMethod.POST, entity, UpdateLabelResDTO.class);
        LOGGER.info("[UPDATE LABEL EMAIL] - response: {}", response.getLabelIds());
    }
}
