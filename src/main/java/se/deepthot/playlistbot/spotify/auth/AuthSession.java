package se.deepthot.playlistbot.spotify.auth;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class AuthSession {
    private final String accessToken;
    private final LocalDateTime expires;
    private final String refreshToken;

    public AuthSession(String accessToken, LocalDateTime expires, String refreshToken) {
        this.accessToken = accessToken;
        this.expires = expires;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expires);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public static AuthSession fromTokenResponse(AuthTokenResponse response){
        return new AuthSession(response.getAccess_token(), LocalDateTime.now().plusSeconds(response.getExpires_in()), response.getRefresh_token());
    }

    public static AuthSession fromRefreshTokenResponse(RefreshTokenResponse response, String refreshToken){
        return new AuthSession(response.getAccess_token(), LocalDateTime.now().plusSeconds(response.getExpires_in()), refreshToken);
    }

    public static AuthSession fromRefreshToken(String refreshToken) {
        return new AuthSession(null, LocalDateTime.ofEpochSecond(0,0, ZoneOffset.UTC), refreshToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthSession that = (AuthSession) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }
}
