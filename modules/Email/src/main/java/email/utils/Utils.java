package email.utils;

import email.services.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class Utils {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final Logger _logger = LoggerFactory.getLogger(Utils.class);

    public static <T> T callApi(String url, HttpMethod httpMethod, HttpEntity<String> entity, Class<T> returnType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, returnType);
            if (response.getStatusCode() != HttpStatus.OK) {
                _logger.warn("Unexpected response status: {}", response.getStatusCode());
                return null;
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            _logger.error("Client error when calling API: {}, Status code: {}, Response body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            _logger.error("Server error when calling API: {}, Status code: {}, Response body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            _logger.error("Error occurred when calling API: {}", url, e);
        } catch (Exception e) {
            _logger.error("Unexpected error occurred when calling API: {}", url, e);
        }
        return null;
    }

}
