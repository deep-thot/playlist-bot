package se.deepthot.playlistbot.persistence;

import se.deepthot.playlistbot.spotify.auth.SpotifyUser;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LostFmSyncConfig {


    @Id
    private Long telegramId;
    private String lastFmUsername;
    private String spotifyRefreshToken;
    private String spotifyUsername;

    public LostFmSyncConfig(){

    }


    public LostFmSyncConfig(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public String getSpotifyRefreshToken() {
        return spotifyRefreshToken;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public void setSpotifyRefreshToken(String spotifyRefreshToken) {
        this.spotifyRefreshToken = spotifyRefreshToken;
    }

    public String getSpotifyUsername() {
        return spotifyUsername;
    }

    public void setSpotifyUsername(String spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }

    public SpotifyUser spotifyUser(){
        return new SpotifyUser(spotifyUsername, spotifyRefreshToken);
    }
}
