package se.deepthot.playlistbot.spotify;

/**
 * Created by Eruenion on 2017-03-19.
 */
public class TrackId {

    private final String id;

    private TrackId(String id) {
        this.id = id;
    }

    public static TrackId of(String id) {
        return new TrackId(id);
    }

    public String getId() {
        return id;
    }

    public String getSpotifyUrl(){
        return "spotify:track:" + getId();
    }

    @Override
    public String toString() {
        return "TrackId{" +
                "id='" + id + '\'' +
                '}';
    }
}
