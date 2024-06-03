package email.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class Utils {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static <T> T callApi(String url, HttpMethod httpMethod,HttpEntity<String> entity, Class<T> returnType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, returnType);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
