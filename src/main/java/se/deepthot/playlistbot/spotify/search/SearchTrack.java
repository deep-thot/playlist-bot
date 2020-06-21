package se.deepthot.playlistbot.spotify.search;

import se.deepthot.playlistbot.spotify.TrackId;

import java.util.List;

public class SearchTrack {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public TrackId getId() {
        return id;
    }

    public void setId(TrackId id) {
        this.id = id;
    }

    private TrackId id;

    private List<TrackSearchResponse.Artist> artists;

    public List<TrackSearchResponse.Artist> getArtists() {
        return artists;
    }

    public boolean hasAstist(String artist) {
        return artists.stream().anyMatch(a -> a.getName().equalsIgnoreCase(artist));
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
