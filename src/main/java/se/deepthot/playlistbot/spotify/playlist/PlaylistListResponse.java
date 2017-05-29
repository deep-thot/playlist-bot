package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

/**
 * Created by Jonatan on 2017-05-29.
 */
public class PlaylistListResponse {

    private List<PlayListResponse> items;

    public List<PlayListResponse> getItems() {
        return items;
    }

    public void setItems(List<PlayListResponse> items) {
        this.items = items;
    }
}
