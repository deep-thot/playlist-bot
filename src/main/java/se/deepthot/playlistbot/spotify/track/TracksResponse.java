package se.deepthot.playlistbot.spotify.track;

import java.util.List;

public class TracksResponse {
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
