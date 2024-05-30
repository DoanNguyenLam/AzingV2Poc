package email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.dto.ClientOauthDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/gmail")
public class GmailController {

    private final String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String CLIENT_ID = "221683923236-50rrujitiesa8c0igmcjt01v7fpe3pdu.apps.googleusercontent.com";
    private final String CLIENT_SECRET = "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";
    private final String SCOPES = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.labels";

    private final String REDIRECT_URI = "http://localhost:8080/api/gmail/access-token";

    @GetMapping("/oauth/url")
    public String gmailOauthUrl(){
        return OAUTH_URL + "?" + "response_type=code" + "&client_id=" + CLIENT_ID + "&scope=" + SCOPES + "&redirect_uri" + REDIRECT_URI;
//        return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=221683923236-50rrujitiesa8c0igmcjt01v7fpe3pdu.apps.googleusercontent.com&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.labels&redirect_uri=http://localhost:8080/api/gmail/access-token";
    }

    @GetMapping("/access-token")
    public String getAccessToken(@RequestParam(value = "code") String authorizationCode) throws Exception {
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

        ResponseEntity<String> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getBody() == null && response.getStatusCode() != HttpStatus.valueOf(200)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gmail unauthorized");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        String accessToken = objectMapper.readValue(response.getBody(), ClientOauthDTO.class).getAccessToken();
        return accessToken;
    }


}
