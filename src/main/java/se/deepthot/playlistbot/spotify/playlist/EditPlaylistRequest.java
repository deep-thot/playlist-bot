package se.deepthot.playlistbot.spotify.playlist;

public class EditPlaylistRequest {
    private final String name;

    public EditPlaylistRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
