package se.deepthot.playlistbot.spotify.playlist;

/**
 * Created by Eruenion on 2017-03-08.
 */
public class RenamePlaylistRequest {
    private final String name;


    RenamePlaylistRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
