package se.deepthot.playlistbot.spotify.search;

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
        private List<SearchTrack> items;

        List<SearchTrack> getItems() {
            return items;
        }

        public void setItems(List<SearchTrack> items) {
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
