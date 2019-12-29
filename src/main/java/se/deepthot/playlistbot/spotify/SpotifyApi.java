package se.deepthot.playlistbot.spotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import se.deepthot.playlistbot.spotify.auth.AuthSession;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Collections.emptyList;

@Service
public class SpotifyApi {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyApi.class);
    private static final String BASE_URL = "https://api.spotify.com/v1/";
    private final RestTemplate restTemplate;
    private final AuthenticationService authenticationService;
    private final ScheduledExecutorService retryScheduler;

    @Inject
    public SpotifyApi(RestTemplate restTemplate, AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.authenticationService = authenticationService;
        retryScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public <T> ResponseEntity<T> performGet(String url, Class<T> responseType, String title, AuthSession session,  Object... urlParams) {
        return performWithRetry(() -> restTemplate.exchange(RequestEntity.get(new UriTemplate(getUrl(url)).expand(urlParams)).header("Authorization", authenticationService.getAuthHeader(session)).build(), responseType), title);
    }



    private String getUrl(String url) {
        return url.startsWith("https:") ? url : BASE_URL + url;
    }

    public <T,R> ResponseEntity<T> performPost(String url, R requestBody, Class<T> responseType, String title, AuthSession session, Object... urlVariables){
        HttpMethod method = HttpMethod.POST;
        return perform(url, requestBody, responseType, title, method, session, urlVariables);
    }

    public <T,R> ResponseEntity<T> performPut(String url, R requestBody, Class<T> responseType, String title, AuthSession session, Object... urlVariables){
        return perform(url, requestBody, responseType, title, HttpMethod.PUT, session, urlVariables);
    }

    private <T, R> ResponseEntity<T> perform(String url, R requestBody, Class<T> responseType, String title, HttpMethod method, AuthSession session, Object... urlVariables) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", authenticationService.getAuthHeader(session));
        HttpEntity<R> entity = new HttpEntity<>(requestBody, headers);
        return performWithRetry(() -> restTemplate.exchange(getUrl(url), method, entity, responseType, urlVariables), title);
    }


    private <T> ResponseEntity<T> performWithRetry(Callable<ResponseEntity<T>> exchange, String title){
        try{
            return exchange.call();
        }
        catch(HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS){
                logger.info("Too many requests when performing {}", title);
                Optional<Integer> retryInSeconds = e.getResponseHeaders().getOrDefault("Retry-After", emptyList()).stream().findFirst().map(Integer::parseInt);
                return retryInSeconds.map(retry -> retry(exchange, retry)).orElse(new ResponseEntity<>(e.getStatusCode()));
            } else {
                logger.warn("Request {} returned status {}: {}. Headers: {}", title, e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders());
                return new ResponseEntity<>(e.getStatusCode());
            }
        } catch(HttpServerErrorException e) {
            logger.info("Request {} encountered server error {}", title, e.getStatusCode());
            return retry(exchange, 1);
        }
        catch(Exception e){
            logger.warn("Error performing {}", title);
            throw new RuntimeException(e);
        }
    }

    private <T> ResponseEntity<T> retry(Callable<ResponseEntity<T>> exchange, Integer retry) {
        logger.info("Retrying in {} s", retry);
        try {
            return retryScheduler.schedule(exchange, retry + 1, TimeUnit.SECONDS).get(retry*2 + 1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e1) {
            throw new RuntimeException(e1);
        }
    }
}
