package se.deepthot.playlistbot.spotify.search;

import java.util.List;

public class SearchTrack {

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    private List<TrackSearchResponse.Artist> artists;

    List<TrackSearchResponse.Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<TrackSearchResponse.Artist> artists) {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "TrackData{" +
                "track=" + getName() +
                ", artists=" + artists +
                ", id=" + getId() +
                '}';
    }
}
