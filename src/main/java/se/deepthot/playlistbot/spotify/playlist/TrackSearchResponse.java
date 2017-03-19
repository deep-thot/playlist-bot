package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

/**
 * Created by Eruenion on 2017-03-19.
 */
public class TrackSearchResponse {
    private Tracks tracks;

    Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    static class Tracks{
        private List<Track> items;

        List<Track> getItems() {
            return items;
        }

        public void setItems(List<Track> items) {
            this.items = items;
        }

        private int total;

        private String href;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    static class Track {

        String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String id;

        private List<Artist> artists;

        List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
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

    static class Artist {
        private String name;

        String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
