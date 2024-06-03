package email.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class Utils {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static <T> T callApi(String url, String body, Class<T> returnType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, returnType);

        return response.getBody();
    }
}
