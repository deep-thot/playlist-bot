package se.deepthot.playlistbot.spotify.playlist;

import se.deepthot.playlistbot.spotify.track.Track;

import java.util.Collections;
import java.util.List;

/**
 * Created by Eruenion on 2017-03-10.
 */
public class Tracks {

    public List<TrackData> getItems() {
        return items;
    }

    public void setItems(List<TrackData> items) {
        this.items = items;
    }

    private List<TrackData> items;

    private String next;

    String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public static Tracks empty(){
        Tracks tracks = new Tracks();
        tracks.setItems(Collections.emptyList());
        tracks.setNext("");
        return tracks;
    }

    public static class TrackData {
        public Track getTrack() {
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
