package se.deepthot.playlistbot.spotify.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthSessions {
    private static final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();

    public static AuthSession get(String refreshtoken){
        return sessions.getOrDefault(refreshtoken, AuthSession.fromRefreshToken(refreshtoken));
    }

    public static AuthSession put(AuthSession session){
        sessions.put(session.getRefreshToken(), session);
        return session;
    }
}
