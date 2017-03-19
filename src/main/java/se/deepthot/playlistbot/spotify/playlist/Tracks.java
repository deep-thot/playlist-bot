package se.deepthot.playlistbot.spotify.playlist;

import java.util.List;

/**
 * Created by Eruenion on 2017-03-10.
 */
public class Tracks {

    List<TrackData> getItems() {
        return items;
    }

    public void setItems(List<TrackData> items) {
        this.items = items;
    }

    private List<TrackData> items;

    static class TrackData {
        Track getTrack() {
            return track;
        }

        public void setTrack(Track track) {
            this.track = track;
        }

        private Track track;
    }

    static class Track {
        public String getName() {
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
    }
}
