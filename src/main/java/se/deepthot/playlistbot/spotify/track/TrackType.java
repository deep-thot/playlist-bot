package se.deepthot.playlistbot.spotify.track;

public enum TrackType {
    RAP("Hiphop"), METAL("Metal"), CALM("Lugna bitar"), PARTY("Partaj"), NOT_SURE("Misc");

    TrackType(String playlistName) {
        this.playlistName = playlistName;
    }

    private String playlistName;

    public String getPlaylistName() {
        return playlistName;
    }
}
