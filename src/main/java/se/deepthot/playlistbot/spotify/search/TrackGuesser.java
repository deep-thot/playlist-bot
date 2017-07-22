package se.deepthot.playlistbot.spotify.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.TrackId;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class TrackGuesser {
    private final SpotifySearch spotifySearch;

    private static final Logger logger = LoggerFactory.getLogger(TrackGuesser.class);


    @Inject
    public TrackGuesser(SpotifySearch spotifySearch) {
        this.spotifySearch = spotifySearch;
    }

    public Optional<TrackId> guessTrack(String title){
        logger.info("Guessing trackId for {}", title);
        return spotifySearch.searchTracks(title)
                .stream()
                .filter(matchesArtist(title).or(matchesTrackName(title)))
                .map(t -> TrackId.of(t.getId()))
                .findFirst();
    }

    private Predicate<SearchTrack> matchesTrackName(String title) {
        return t -> title.toLowerCase().contains(t.getName().toLowerCase());
    }

    private Predicate<SearchTrack> matchesArtist(String title) {
        return t -> t.getArtists()
                .stream()
                .anyMatch(a -> title.toLowerCase().contains(a.getName().toLowerCase()));
    }



}
