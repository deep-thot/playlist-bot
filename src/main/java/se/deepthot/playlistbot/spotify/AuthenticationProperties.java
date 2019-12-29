package se.deepthot.playlistbot.spotify;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.auth.SpotifyUser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Eruenion on 2017-03-07.
 */
@Service
@ConfigurationProperties("spotify.auth")
public class AuthenticationProperties {

    private String authSecret;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String refreshToken;

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String authUrl(Long telegramId){
        try {
            return String.format("https://https://accounts.spotify.com/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=playlist-modify-private+playlist-modify-public+playlist-read-private&state=%s", this.clientId, URLEncoder.encode(this.redirectUri, "UTF-8"), telegramId);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public SpotifyUser user(){
        return new SpotifyUser("esplaylistbot", refreshToken);
    }
}
