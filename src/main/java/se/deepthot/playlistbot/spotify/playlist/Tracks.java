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

        private List<Artist> artists;

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
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
    }

    static class Artist {
        private String name;

        public String getName() {
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
