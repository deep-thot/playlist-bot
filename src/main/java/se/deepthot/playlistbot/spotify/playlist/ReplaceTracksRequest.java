package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

public class ReplaceTracksRequest {

    private final List<String> uris;

    public ReplaceTracksRequest(List<String> uris) {
        this.uris = uris;
    }

    public List<String> getUris() {
        return uris;
    }
}
