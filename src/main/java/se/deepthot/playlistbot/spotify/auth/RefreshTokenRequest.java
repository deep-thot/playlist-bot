package se.deepthot.playlistbot.spotify.auth;

/**
 * Created by Eruenion on 2017-03-07.
 */
public class RefreshTokenRequest {
    private final String grant_type;
    private final String refresh_token;

    public RefreshTokenRequest(String grant_type, String refresh_token) {
        this.grant_type = grant_type;
        this.refresh_token = refresh_token;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
