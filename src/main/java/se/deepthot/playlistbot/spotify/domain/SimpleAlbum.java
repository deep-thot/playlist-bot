package se.deepthot.playlistbot.spotify.domain;

public class SimpleAlbum {
    private String id;
    private String href;
    private String name;
    private AlbumType album_type;

    public void setId(String id) {
        this.id = id;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

    public AlbumType getAlbum_type() {
        return album_type;
    }

    public void setAlbumType(AlbumType albumType) {
        this.album_type = albumType;
    }
}
