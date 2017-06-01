package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

/**
 * Created by Jonatan on 2017-05-29.
 */
public class PlaylistListResponse {

    private List<PlayListResponse> items;
    private String next;

    public List<PlayListResponse> getItems() {
        return items;
    }

    public void setItems(List<PlayListResponse> items) {
        this.items = items;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
