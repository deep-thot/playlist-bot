package se.deepthot.playlistbot.lastfm.domain;

import java.time.LocalDateTime;
import java.util.Optional;

public class Track {
    private final String artist;
    private final String album;
    private final String name;
    private final LocalDateTime lastPlayed;

    public Track(String artist, String album, String name, LocalDateTime lastPlayed) {
        this.artist = artist;
        this.album = album;
        this.name = name;
        this.lastPlayed = lastPlayed;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public Optional<LocalDateTime> getLastPlayed() {
        return Optional.ofNullable(lastPlayed);
    }

}
