package se.deepthot.playlistbot.spotify.auth;

import java.time.LocalDateTime;

/**
 * Created by Eruenion on 2017-03-07.
 */
public class CurrentAuthTokens {
    private final String accessToken;
    private final LocalDateTime expires;

    private CurrentAuthTokens(String accessToken, long ttl) {
        this.accessToken = accessToken;
        expires = LocalDateTime.now().plusSeconds(ttl);
    }

    public CurrentAuthTokens newAccessToken(String accessToken, long ttl){
        return new CurrentAuthTokens(accessToken, ttl);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expires);
    }

    public boolean isEmpty(){
        return accessToken == null;
    }

    public static CurrentAuthTokens empty(){
        return new CurrentAuthTokens(null, -1);
    }
}
