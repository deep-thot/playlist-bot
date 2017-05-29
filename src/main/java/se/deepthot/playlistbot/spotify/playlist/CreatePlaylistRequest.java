package se.deepthot.playlistbot.spotify.playlist;

/**
 * Created by Jonatan on 2017-05-29.
 */
public class CreatePlaylistRequest {
    private final String name;
    private final Boolean _public;


    public CreatePlaylistRequest(String name, Boolean aPublic) {
        this.name = name;
        _public = aPublic;
    }

    public String getName() {
        return name;
    }

    public Boolean isPublic() {
        return _public;
    }
}
