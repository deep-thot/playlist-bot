package se.deepthot.playlistbot.spotify.playlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Eruenion on 2017-03-08.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayListResponse {
    private String name;
    private String id;
    private Tracks tracks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
