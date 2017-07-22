package se.deepthot.playlistbot.spotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Collections.emptyList;

@Service
public class SpotifyApi {
    private final RestTemplate restTemplate;
    private final AuthenticationService authenticationService;
    private final ScheduledExecutorService retryScheduler;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyApi.class);

    private static final String BASE_URL = "https://api.spotify.com/v1/";

    @Inject
    public SpotifyApi(RestTemplate restTemplate, AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.authenticationService = authenticationService;
        retryScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public <T> ResponseEntity<T> performGet(String url, Class<T> responseType, String title) {
        return performWithRetry(() -> restTemplate.exchange(RequestEntity.get(URI.create(getUrl(url))).header("Authorization", authenticationService.getAuthHeader()).build(), responseType), title);
    }

    private String getUrl(String url) {
        return url.startsWith("https:") ? url : BASE_URL + url;
    }

    public <T,R> ResponseEntity<T> performPost(String url, R requestBody, Class<T> responseType, String title, Object... urlVariables){
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", authenticationService.getAuthHeader());
        HttpEntity<R> entity = new HttpEntity<>(requestBody, headers);
        return performWithRetry(() -> restTemplate.exchange(getUrl(url), HttpMethod.POST, entity, responseType, urlVariables), title);
    }

    private <T> ResponseEntity<T> performWithRetry(Callable<ResponseEntity<T>> exchange, String title){
        try{
            return exchange.call();
        }
        catch(HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS){
                logger.info("Too many requests when performing {}", title);
                Optional<Integer> retryInSeconds = e.getResponseHeaders().getOrDefault("Retry-After", emptyList()).stream().findFirst().map(Integer::parseInt);
                return retryInSeconds.map(retry -> {
                    logger.info("Retrying in {} s", retry);
                    try {
                        return retryScheduler.schedule(exchange, retry + 1, TimeUnit.SECONDS).get(retry*2, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                        throw new RuntimeException(e1);
                    }
                }).orElse(new ResponseEntity<>(e.getStatusCode()));
            } else {
                logger.warn("Request returned status {}: {}. Headers: {}",e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders());
                return new ResponseEntity<>(e.getStatusCode());
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
