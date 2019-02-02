package se.deepthot.playlistbot.spotify.playlist;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Eruenion on 2017-03-08.
 */
@Component
@ConfigurationProperties("spotify.playlist")
public class PlaylistConfig {

    public static final String PLAYLIST_PREFIX = "Musiksnack - ";
    private String playlistId;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
