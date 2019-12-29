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


    @Inject
    public AuthenticationService(AuthenticationProperties authenticationProperties, RestTemplate restTemplate) {
        this.authenticationProperties = authenticationProperties;
        this.restTemplate = restTemplate;
    }

    public String getAuthHeader(AuthSession authSession) {
       return "Bearer " + getAuthToken(authSession);
    }

    private String getAuthToken(AuthSession session){
        if(session.isExpired()){
            return refreshAuthToken(session).getAccessToken();
        }
        return session.getAccessToken();
    }

    private AuthSession refreshAuthToken(AuthSession session){
        ResponseEntity<RefreshTokenResponse> response = getRefreshToken(session.getRefreshToken());
        RefreshTokenResponse body = response.getBody();
        return AuthSessions.put(AuthSession.fromRefreshTokenResponse(body, session.getRefreshToken()));
    }

    private ResponseEntity<RefreshTokenResponse> getRefreshToken(String refreshToken) {
        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        return getTokenResponse(form, RefreshTokenResponse.class);
    }

    private <T> ResponseEntity<T> getTokenResponse(LinkedMultiValueMap<String, Object> form, Class<T> responseClass) {
        ResponseEntity<T> response = restTemplate.exchange(createTokenRequest().body(form), responseClass);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Couldn't refresh auth token: " + response.getStatusCode().getReasonPhrase());
        }
        return response;
    }

    public SpotifyUser getAuthAndRefreshToken(String authCode){
        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", authenticationProperties.getRedirectUri());

        ResponseEntity<AuthTokenResponse> response = getTokenResponse(form, AuthTokenResponse.class);
        AuthSession session = AuthSessions.put(AuthSession.fromTokenResponse(response.getBody()));
        return getUser(session);
    }

    public SpotifyUser getUser(AuthSession session){
        ResponseEntity<UserResponse> user = restTemplate.exchange(RequestEntity.get(URI.create("https://api.spotify.com/v1/me")).header("Authorization", getAuthHeader(session)).build(), UserResponse.class);
        return new SpotifyUser(user.getBody().getId(), session.getRefreshToken());
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
