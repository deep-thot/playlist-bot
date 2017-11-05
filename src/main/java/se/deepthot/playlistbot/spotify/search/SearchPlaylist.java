package se.deepthot.playlistbot.spotify.search;

public class SearchPlaylist {
    private TrackData tracks;
    private String name;
    private String id;

    public void setTracks(TrackData tracks) {
        this.tracks = tracks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TrackData getTracks() {
        return tracks;
    }

    public String getName() {
        return name;
    }

    public String getTracksUrl(){
        return tracks.getHref();
    }

    public String getId() {
        return id;
    }

    private static class TrackData {
        private String href;
        private long total;

        public void setHref(String href) {
            this.href = href;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        String getHref() {
            return href;
        }

        public long getTotal() {
            return total;
        }
    }
}
