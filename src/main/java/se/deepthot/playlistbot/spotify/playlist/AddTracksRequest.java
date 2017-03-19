package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

/**
 * Created by Eruenion on 2017-03-10.
 */
public class AddTracksRequest {
    private final List<String> uris;


    AddTracksRequest(List<String> uris) {
        this.uris = uris;
    }

    public List<String> getUris() {
        return uris;
    }
}
