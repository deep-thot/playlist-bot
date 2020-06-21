package se.deepthot.playlistbot.lostfm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.lastfm.LastFmService;
import se.deepthot.playlistbot.persistence.LostFmSyncConfig;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.auth.SpotifyUser;
import se.deepthot.playlistbot.spotify.playlist.PlayListResponse;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;
import se.deepthot.playlistbot.spotify.search.SearchTrack;
import se.deepthot.playlistbot.spotify.search.SpotifySearch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class LostFmService {

    private static final Logger logger = LoggerFactory.getLogger(LostFmService.class);

    private final LastFmService lastFmService;
    private final SpotifySearch spotifySearch;
    private final PlaylistHandler playlistHandler;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public LostFmService(LastFmService lastFmService, SpotifySearch spotifySearch, PlaylistHandler playlistHandler) {
        this.lastFmService = lastFmService;
        this.spotifySearch = spotifySearch;
        this.playlistHandler = playlistHandler;
    }

    public void generateLostFmPlaylist(LostFmSyncConfig syncConfig, Consumer<PlayListResponse> playlistDone, Consumer<Exception> error){

        executorService.submit(() -> {
            try {
                SpotifyUser spotifyUser = syncConfig.spotifyUser();
                PlayListResponse lostFmPlaylist = playlistHandler.getOrCreatePlaylist("Lost FM", spotifyUser);
                List<String> trackIds = lastFmService.getTopUnplayedTracks(syncConfig.getLastFmUsername())
                        .map(track ->
                                spotifySearch.searchTracks(String.format("artist:%s track:%s", queryParam(track.getArtist()), queryParam(track.getName())), spotifyUser.getAuthSession())
                                        .stream()
                                        .filter(st -> st.getName().toLowerCase().equals(track.getName().toLowerCase()))
                                        .filter(st -> st.hasAstist(track.getArtist()))
                                        .findFirst()
                        ).filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(SearchTrack::getId)
                        .map(TrackId::getSpotifyUrl)
                        .limit(50)
                        .collect(Collectors.toList());

                playlistHandler.replaceTracks(lostFmPlaylist.getId(), trackIds, spotifyUser);
                playlistDone.accept(lostFmPlaylist);

            } catch (Exception e){
                logger.error("Failed to generate playlist", e);
                error.accept(e);
            }
        });
    }

    private String queryParam(String input){
        return input.toLowerCase().replaceAll("['\u030a]", "")
                .replaceAll("[åäÅÄ]","a")
                .replaceAll("öø", "o");
    }


}
