package se.deepthot.playlistbot.spotify.playlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.deepthot.playlistbot.spotify.auth.UserResponse;
import se.deepthot.playlistbot.spotify.domain.ExternalUrls;

/**
 * Created by Eruenion on 2017-03-08.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayListResponse {
    private String name;
    private String id;
    private Tracks tracks;
    private ExternalUrls external_urls;
    private UserResponse owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayListResponse that = (PlayListResponse) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public ExternalUrls getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(ExternalUrls external_urls) {
        this.external_urls = external_urls;
    }

    public UserResponse getOwner() {
        return owner;
    }

    public void setOwner(UserResponse owner) {
        this.owner = owner;
    }
}
