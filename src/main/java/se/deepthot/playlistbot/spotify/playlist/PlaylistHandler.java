package se.deepthot.playlistbot.spotify.playlist;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.SpotifyApi;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.auth.AuthSession;
import se.deepthot.playlistbot.spotify.auth.SpotifyUser;
import se.deepthot.playlistbot.spotify.track.Track;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Eruenion on 2017-03-08.
 */
@Service
public class PlaylistHandler {


    private static final Logger logger = LoggerFactory.getLogger(PlaylistHandler.class);
    private final LoadingCache<SpotifyUser, List<PlayListResponse>> playlistCache;
    private final LoadingCache<PlaylistAndUser, Set<String>> trackIdCache;
    private final SpotifyApi spotifyApi;


    @Inject
    public PlaylistHandler(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
        this.playlistCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(this::listPlayLists);
        trackIdCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(this::getAllTracks);
    }


    @SuppressWarnings("ConstantConditions")
    public void addTrackToPlaylist(String playlistId, String trackId, SpotifyUser user){
        PlaylistAndUser playlistAndUser = new PlaylistAndUser(playlistId, user);
        if(trackIdCache.get(playlistAndUser).contains(trackId)){
            logger.info("No new tracks to add. ({} already exist in playlist)", trackId);
            return;
        }
        spotifyApi.performPost("users/{user}/playlists/{playlistId}/tracks", new AddTracksRequest(singletonList(TrackId.of(trackId).getSpotifyUrl())), AddTracksResponse.class, "Add track " + trackId + " to playlist " + playlistId, user.getAuthSession(), user.getUsername(), playlistId);
        logger.info("Added track {}", trackId);
        trackIdCache.get(playlistAndUser).add(trackId);

    }

    private PlayListResponse createPlaylist(String name, SpotifyUser user){
        ResponseEntity<PlayListResponse> result = spotifyApi.performPost("users/{user}/playlists", new CreatePlaylistRequest(name, true), PlayListResponse.class, "Create playlist " + name, user.getAuthSession(), user.getUsername());
        PlayListResponse body = verifyResult(result).getBody();
        logger.info("Created new playlist \"{}\" ({})", body.getName(), body.getId());
        playlistCache.invalidateAll();
        return body;
    }


    private Set<String> getTrackIds(List<TrackData> tracks){
        return tracks.stream().map(TrackData::getTrack).map(Track::getId).collect(toSet());
    }

    private Set<String> getAllTracks(PlaylistAndUser playlistAndUser){
        List<TrackData> result = getTrackData(playlistAndUser.getPlaylistId(), playlistAndUser.getAuthSession());
        return getTrackIds(result);
    }

    private List<TrackData> getTrackData(String playlistId, SpotifyUser user) {
        PlayListResponse playlist = getPlaylist(playlistId, user);
        String next = playlist.getTracks().getNext();
        List<TrackData> result = playlist.getTracks().getItems();
        while(next != null){
            Tracks response = spotifyApi.performGet(next, Tracks.class, "Loading tracks for " + playlistId, user.getAuthSession()).getBody();
            result.addAll(response.getItems());
            next = response.getNext();
        }
        return result;
    }


    private PlayListResponse getPlaylist(String playlistId, SpotifyUser user){
        ResponseEntity<PlayListResponse> result = spotifyApi.performGet("users/{user}/playlists/" + playlistId, PlayListResponse.class, "loading playlist " + playlistId, user.getAuthSession(), user.getUsername());

        return verifyResult(result).getBody();
    }

    public Tracks loadPlaylist(String url, AuthSession authSession){
        ResponseEntity<Tracks> result = spotifyApi.performGet(url, Tracks.class, "loading playlist from " + url, authSession);
        if(result.getStatusCode() == HttpStatus.NOT_FOUND){
            logger.error("Playlist at {} not found", url);
            return Tracks.empty();
        }
        return verifyResult(result).getBody();
    }

    public List<TrackId> getTracksOfPlaylist(String playlistId, SpotifyUser user){
        return getTrackData(playlistId, user).stream().map(td -> td.getTrack().getId()).map(TrackId::of).collect(Collectors.toList());
    }

    public PlayListResponse getPlaylistByName(String name, SpotifyUser user){
        return getPlaylists(user).get(name);
    }

    public boolean hasPlaylistByName(String name, SpotifyUser user){
        return getPlaylistByName(name, user) != null;
    }

    public void addTrackToPlaylists(String trackId, List<String> playlistNames, SpotifyUser user){
        logger.info("Adding to playlists {}", playlistNames);
        getOrCreatePlaylists(playlistNames, user).forEach(id -> addTrackToPlaylist(id, trackId, user));
    }

    private Stream<String> getOrCreatePlaylists(List<String> playlistNames, SpotifyUser user) {
        return playlistNames.stream().map(name -> getOrCreatePlaylist(name, user).getId());
    }

    public PlayListResponse getOrCreatePlaylist(String name, SpotifyUser user) {
        Map<String, PlayListResponse> playlistMap = getPlaylists(user);
        return playlistMap.computeIfAbsent(name,  n-> createPlaylist(n, user));
    }

    private Map<String, PlayListResponse> getPlaylists(SpotifyUser user) {
        //noinspection ConstantConditions
        return playlistCache.get(user).stream().filter(playlist -> playlist.getOwner().getId().equals(user.getUsername())).distinct().collect(toMap(PlayListResponse::getName, Function.identity(), (r1, r2) -> {
            logger.info("Found duplicate playlists named {}: ({} and {}), using {}", r1.getName(), r1.getId(), r2.getId(), r1.getId());
            return r1;
        }));
    }

    private List<PlayListResponse> listPlayLists(SpotifyUser user){
        PlaylistListResponse list = spotifyApi.performGet("users/{user}/playlists", PlaylistListResponse.class, "Initial get all playlists", user.getAuthSession(), user.getUsername()).getBody();
        List<PlayListResponse> result = list.getItems();
        String next = list.getNext();
        while(next != null){
            PlaylistListResponse playlistList = spotifyApi.performGet(next, PlaylistListResponse.class, "Getting all playlists (contd.)", user.getAuthSession()).getBody();
            result.addAll(playlistList.getItems());
            next = playlistList.getNext();
        }
        return result;
    }

    public void renamePlaylist(String playlistId, String newName, AuthSession authSession){
        spotifyApi.performPut("playlists/" + playlistId, new EditPlaylistRequest(newName), ExpectedEmptyResponse.class, "Editing playlist", authSession);
        playlistCache.invalidateAll();
    }

    public void replaceTracks(String playlistId, List<String> trackUris, SpotifyUser user){
        ResponseEntity<ExpectedEmptyResponse> response = spotifyApi.performPut("playlists/{playlistId}/tracks", new ReplaceTracksRequest(trackUris), ExpectedEmptyResponse.class, "Replacing tracks of playlist", user.getAuthSession(), playlistId);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Failed operation: " + response.getStatusCode());
        }
        trackIdCache.put(new PlaylistAndUser(playlistId, user), new HashSet<>(trackUris));
    }



    private <T> ResponseEntity<T> verifyResult(ResponseEntity<T> result) {
        if(!result.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Unable to get playlist: " + result.getStatusCode().getReasonPhrase());
        }
        return result;
    }

    private static class PlaylistAndUser {
        private final String playlistId;
        private final SpotifyUser user;

        private PlaylistAndUser(String playlistId, SpotifyUser user) {
            this.playlistId = playlistId;
            this.user = user;
        }

        public String getPlaylistId() {
            return playlistId;
        }

        public SpotifyUser getAuthSession() {
            return user;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlaylistAndUser that = (PlaylistAndUser) o;
            return Objects.equals(playlistId, that.playlistId) &&
                    Objects.equals(user, that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playlistId, user);
        }
    }


}
