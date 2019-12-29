package se.deepthot.playlistbot.spotify.track;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.SpotifyApi;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.auth.AuthSession;
import se.deepthot.playlistbot.spotify.domain.Album;
import se.deepthot.playlistbot.spotify.domain.SimpleAlbum;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

@Service
public class Tracks {
    private final SpotifyApi spotifyApi;

    @Inject
    public Tracks(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<Track> loadTracks(List<TrackId> trackIds, AuthSession authSession){
        String trackIdParam = trackIds.stream().map(TrackId::getId).collect(joining(","));
        ResponseEntity<TracksResponse> response = spotifyApi.performGet("tracks?ids=" + trackIdParam, TracksResponse.class, "track ids " + trackIdParam, authSession);
        return Optional.ofNullable(response.getBody()).map(TracksResponse::getTracks).orElse(emptyList());
    }

    public Album loadFullAlbum(SimpleAlbum simpleAlbum, AuthSession authSession){
        return spotifyApi.performGet(simpleAlbum.getHref(), Album.class, "Loading album " + simpleAlbum.getName(), authSession).getBody();
    }

    public AudioFeatures getAudioFeatures(TrackId trackId, AuthSession authSession){
        return spotifyApi.performGet("audio-features/{trackId}", AudioFeatures.class, "Get track audio features", authSession, trackId.getId()).getBody();
    }
}
