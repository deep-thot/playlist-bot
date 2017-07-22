package se.deepthot.playlistbot.spotify.track;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.SpotifyApi;
import se.deepthot.playlistbot.spotify.TrackId;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Service
public class Tracks {
    private final SpotifyApi spotifyApi;

    @Inject
    public Tracks(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<Track> loadTracks(List<TrackId> trackIds){
        String trackIdParam = trackIds.stream().map(TrackId::getId).collect(joining(","));
        ResponseEntity<TracksResponse> response = spotifyApi.performGet("tracks?ids=" + trackIdParam, TracksResponse.class, "track ids " + trackIdParam);
        return response.getBody().getTracks();

    }
}
