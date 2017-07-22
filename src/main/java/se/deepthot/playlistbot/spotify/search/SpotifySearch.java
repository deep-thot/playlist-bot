package se.deepthot.playlistbot.spotify.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.SpotifyApi;

import javax.inject.Inject;
import java.util.List;

@Service
public class SpotifySearch {

    private final SpotifyApi spotifyApi;

    private static final Logger logger = LoggerFactory.getLogger(SpotifySearch.class);

    @Inject
    public SpotifySearch(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<SearchTrack> searchTracks(String query){
        ResponseEntity<TrackSearchResponse> result = spotifyApi.performGet("search?type=track&q=" + query, TrackSearchResponse.class, "Search " + query);
        TrackSearchResponse response = result.getBody();
        TrackSearchResponse.Tracks tracks = response.getTracks();
        logger.info("Found {} potential matches: {} ({})", tracks.getItems().size(), tracks.getItems(), tracks.getHref());
        return tracks.getItems();
    }


}
