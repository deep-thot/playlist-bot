package se.deepthot.playlistbot.spotify.auth;

/**
 * Created by Eruenion on 2017-03-07.
 */
public class AuthTokenRequest {

    private final String grant_type;
    private final String code;
    private final String redirect_uri;

    public AuthTokenRequest(String grant_type, String code, String redirect_uri) {
        this.grant_type = grant_type;
        this.code = code;
        this.redirect_uri = redirect_uri;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getCode() {
        return code;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }
}
