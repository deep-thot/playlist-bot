package se.deepthot.playlistbot.spotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import se.deepthot.playlistbot.spotify.auth.*;

import javax.inject.Inject;
import java.net.URI;
import java.util.Base64;

import static org.springframework.http.RequestEntity.post;

/**
 * Created by Eruenion on 2017-03-07.
 */
@Service
public class AuthenticationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationProperties authenticationProperties;
    private final RestTemplate restTemplate;

    private CurrentAuthTokens currentAuthTokens;

    @Inject
    public AuthenticationService(AuthenticationProperties authenticationProperties, RestTemplate restTemplate) {
        this.authenticationProperties = authenticationProperties;
        this.restTemplate = restTemplate;
        currentAuthTokens = CurrentAuthTokens.empty();
    }

    public String getAuthHeader() {
        return "Bearer " + getCurrentAuthToken();
    }

    private String getCurrentAuthToken() {
        if (!currentAuthTokens.isExpired()) {
            return currentAuthTokens.getAccessToken();
        }
        currentAuthTokens = refreshAuthToken();
        return currentAuthTokens.getAccessToken();
    }

    private CurrentAuthTokens refreshAuthToken() {
        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", authenticationProperties.getRefreshToken());
        ResponseEntity<RefreshTokenResponse> response = restTemplate.exchange(createTokenRequest().body(form), RefreshTokenResponse.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Couldn't refresh auth token: " + response.getStatusCode().getReasonPhrase());
        }
        return currentAuthTokens.newAccessToken(response.getBody().getAccess_token(), response.getBody().getExpires_in());
    }

    private RequestEntity.BodyBuilder createTokenRequest() {
        return post(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", getEncodedAuthHeader())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private String getEncodedAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((authenticationProperties.getClientId() + ":" + authenticationProperties.getClientSecret()).getBytes());
    }
}
