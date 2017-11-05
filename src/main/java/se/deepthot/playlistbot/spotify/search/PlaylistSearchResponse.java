package se.deepthot.playlistbot.spotify.search;

import java.util.List;

public class PlaylistSearchResponse {
    private Playlists playlists;



    Playlists getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    static class Playlists {
        private String next;
        private long total;
        private List<SearchPlaylist> items;

        public void setNext(String next) {
            this.next = next;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public void setItems(List<SearchPlaylist> items) {
            this.items = items;
        }

        public String getNext() {
            return next;
        }

        public long getTotal() {
            return total;
        }


        public List<SearchPlaylist> getItems() {
            return items;
        }
    }


}
