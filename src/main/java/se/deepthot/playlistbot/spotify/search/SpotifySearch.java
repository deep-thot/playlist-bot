package se.deepthot.playlistbot.spotify.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.SpotifyApi;
import se.deepthot.playlistbot.spotify.auth.AuthSession;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class SpotifySearch {

    private final SpotifyApi spotifyApi;

    private static final Logger logger = LoggerFactory.getLogger(SpotifySearch.class);

    @Inject
    public SpotifySearch(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<SearchTrack> searchTracks(String query, AuthSession authSession){
        ResponseEntity<TrackSearchResponse> response = spotifyApi.performGet("search?type=track&q={query}", TrackSearchResponse.class, "Search " + query, authSession, query);
        TrackSearchResponse trackResponse = response.getBody();
        if(trackResponse == null){
            return Collections.emptyList();
        }
        TrackSearchResponse.Tracks tracks = trackResponse.getTracks();
        List<SearchTrack> result = getItems(tracks);
        logger.info("Found {} potential matches: {} ({})", result.size(), result, tracks.getHref());
        return result;
    }

    private List<SearchTrack> getItems(TrackSearchResponse.Tracks tracks) {
        return tracks.getItems().stream().filter(Objects::nonNull).collect(toList());
    }

    public List<SearchPlaylist> searchPlaylist(String query, int limit, AuthSession authSession){
        ResponseEntity<PlaylistSearchResponse> result = spotifyApi.performGet("search?type=playlist&q={query}&limit={limit}", PlaylistSearchResponse.class, "Search playlist" + query, authSession, query, limit);
        return result.getBody().getPlaylists().getItems();
    }


}
