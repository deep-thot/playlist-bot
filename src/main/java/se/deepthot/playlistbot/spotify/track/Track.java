package se.deepthot.playlistbot.spotify.track;

import se.deepthot.playlistbot.spotify.domain.SimpleAlbum;

public class Track {
    private String id;
    private String name;
    private int popularity;
    private SimpleAlbum album;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                '}';
    }

    public SimpleAlbum getAlbum() {
        return album;
    }

    public void setAlbum(SimpleAlbum album) {
        this.album = album;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        return id != null ? id.equals(track.id) : track.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
