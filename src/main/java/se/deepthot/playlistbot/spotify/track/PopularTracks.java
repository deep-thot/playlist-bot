package se.deepthot.playlistbot.spotify.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.AuthenticationProperties;
import se.deepthot.playlistbot.spotify.auth.AuthSession;
import se.deepthot.playlistbot.spotify.auth.AuthSessions;
import se.deepthot.playlistbot.spotify.domain.Album;
import se.deepthot.playlistbot.spotify.domain.AlbumType;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;
import se.deepthot.playlistbot.spotify.playlist.TrackData;
import se.deepthot.playlistbot.spotify.search.SearchPlaylist;
import se.deepthot.playlistbot.spotify.search.SpotifySearch;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
public class PopularTracks {

    private static final Logger logger = LoggerFactory.getLogger(PopularTracks.class);

    private final SpotifySearch spotifySearch;
    private final PlaylistHandler playlistHandler;
    private final Tracks tracks;
    private final AuthenticationProperties authenticationProperties;

    public PopularTracks(SpotifySearch spotifySearch, PlaylistHandler playlistHandler, Tracks tracks, AuthenticationProperties authenticationProperties) {
        this.spotifySearch = spotifySearch;
        this.playlistHandler = playlistHandler;
        this.tracks = tracks;
        this.authenticationProperties = authenticationProperties;
    }

    public List<Track> findPopularTracks(Integer currentYear, int limit){
        AtomicInteger count = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        List<SearchPlaylist> playlists = spotifySearch.searchPlaylist(currentYear + "", limit, getAuthSession());
        List<Track> result = playlists.parallelStream()
                .flatMap(this::loadPlaylistTracks)
                .map(TrackData::getTrack)
                .distinct()
                .filter(t -> t.getPopularity() > 60)
                .filter(this::hasProperAlbum)
                .filter(this::notCompilationAlbum)
                .filter(t -> isFromYear(currentYear, t))
                .peek(t -> count.incrementAndGet())
                .sorted(comparing(Track::getPopularity).reversed())
                .limit(5)
                .peek(t -> logger.info("track {}", t))
                .collect(toList());
        logger.info("Went through {} tracks in {} ms", count.get(), System.currentTimeMillis()-start);
        return result;
    }

    private AuthSession getAuthSession() {
        return AuthSessions.get(authenticationProperties.getRefreshToken());
    }

    private boolean hasProperAlbum(Track t) {
        return t.getAlbum().getId() != null;
    }

    private boolean notCompilationAlbum(Track t) {
        return t.getAlbum().getAlbum_type() != AlbumType.compilation;
    }

    private boolean isFromYear(Integer currentYear, Track t) {
        if (t.getAlbum().getHref() == null) {
            logger.warn("Track {} had no album!", t);
            return false;
        }
        Album album = tracks.loadFullAlbum(t.getAlbum(), getAuthSession());
        return album.getYear() == currentYear;
    }

    private Stream<TrackData> loadPlaylistTracks(SearchPlaylist p) {
        return playlistHandler.loadPlaylist(p.getTracksUrl(), getAuthSession()).getItems().stream().filter(td -> {
            if(td.getTrack() == null){
                logger.warn("Track was null for playlist {}", p.getTracksUrl());
                return false;
            }
            return true;
        });
    }
}
