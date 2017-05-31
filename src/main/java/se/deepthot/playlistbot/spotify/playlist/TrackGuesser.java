package se.deepthot.playlistbot.spotify.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import se.deepthot.playlistbot.spotify.AuthenticationService;
import se.deepthot.playlistbot.spotify.TrackId;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class TrackGuesser {
    private AuthenticationService authenticationService;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TrackGuesser.class);


    @Inject
    public TrackGuesser(AuthenticationService authenticationService, RestTemplate restTemplate) {
        this.authenticationService = authenticationService;
        this.restTemplate = restTemplate;
    }

    public Optional<TrackId> guessTrack(String title){
        logger.info("Guessing trackId for {}", title);
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", authenticationService.getAuthHeader());
        ResponseEntity<TrackSearchResponse> result = restTemplate.exchange("https://api.spotify.com/v1/search?type=track&q={title}", HttpMethod.GET, new HttpEntity<>(headers), TrackSearchResponse.class, title);

        TrackSearchResponse response = result.getBody();
        TrackSearchResponse.Tracks tracks = response.getTracks();
        logger.info("Found {} potential matches: {} ({})", tracks.getItems().size(), tracks.getItems(), tracks.getHref());

        return tracks
                .getItems()
                .stream()
                .filter(matchesArtist(title).or(matchesTrackName(title)))
                .map(t -> TrackId.of(t.getId()))
                .findFirst();
    }

    private Predicate<TrackSearchResponse.Track> matchesTrackName(String title) {
        return t -> title.toLowerCase().contains(t.getName().toLowerCase());
    }

    private Predicate<TrackSearchResponse.Track> matchesArtist(String title) {
        return t -> t.getArtists()
                .stream()
                .anyMatch(a -> title.toLowerCase().contains(a.getName().toLowerCase()));
    }



}
