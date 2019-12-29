package se.deepthot.playlistbot.spotify.auth;

import java.util.Objects;

public class SpotifyUser {
    private final String username;
    private final String refreshToken;

    public SpotifyUser(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public AuthSession getAuthSession(){
        return AuthSessions.get(refreshToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotifyUser that = (SpotifyUser) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, refreshToken);
    }
}
