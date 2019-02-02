package se.deepthot.playlistbot.spotify.playlist;

import se.deepthot.playlistbot.spotify.track.Track;

import java.time.ZonedDateTime;
import java.util.List;

public class TrackData {
    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    private Track track;

    private ZonedDateTime added_at;


    private List<Tracks.Artist> artists;

    public List<Tracks.Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Tracks.Artist> artists) {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "TrackData{" +
                "track=" + track.getName() +
                ", artists=" + artists +
                ", id=" + track.getId() +
                '}';
    }

    public ZonedDateTime getAdded_at() {
        return added_at;
    }

    public void setAdded_at(ZonedDateTime added_at) {
        this.added_at = added_at;
    }
}
