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
import se.deepthot.playlistbot.spotify.track.Track;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    private final LoadingCache<String, List<PlayListResponse>> playlistCache;
    private final LoadingCache<String, Set<String>> trackIdCache;
    private final SpotifyApi spotifyApi;


    @Inject
    public PlaylistHandler(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
        this.playlistCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(s -> listPlayLists());
        trackIdCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(this::getAllTracks);
    }


    public void addTrackToPlaylist(String playlistId, String trackId){
        if(trackIdCache.get(playlistId).contains(trackId)){
            logger.info("No new tracks to add. ({} already exist in playlist)", trackId);
            return;
        }
        spotifyApi.performPost("users/esplaylistbot/playlists/{playlistId}/tracks", new AddTracksRequest(singletonList(TrackId.of(trackId).getSpotifyUrl())), AddTracksResponse.class, "Add track " + trackId + " to playlist " + playlistId, playlistId);
        logger.info("Added track {}", trackId);
        trackIdCache.get(playlistId).add(trackId);

    }

    private PlayListResponse createPlaylist(String name){
        ResponseEntity<PlayListResponse> result = spotifyApi.performPost("users/esplaylistbot/playlists", new CreatePlaylistRequest(name, true), PlayListResponse.class, "Create playlist " + name);
        PlayListResponse body = verifyResult(result).getBody();
        logger.info("Created new playlist \"{}\" ({})", body.getName(), body.getId());
        playlistCache.invalidateAll();
        return body;
    }


    private Set<String> getTrackIds(List<TrackData> tracks){
        return tracks.stream().map(TrackData::getTrack).map(Track::getId).collect(toSet());
    }

    private Set<String> getAllTracks(String playlistId){
        List<TrackData> result = getTrackData(playlistId);
        return getTrackIds(result);
    }

    private List<TrackData> getTrackData(String playlistId) {
        PlayListResponse playlist = getPlaylist(playlistId);
        String next = playlist.getTracks().getNext();
        List<TrackData> result = playlist.getTracks().getItems();
        while(next != null){
            Tracks response = spotifyApi.performGet(next, Tracks.class, "Loading tracks for " + playlistId).getBody();
            result.addAll(response.getItems());
            next = response.getNext();
        }
        return result;
    }


    private PlayListResponse getPlaylist(String playlistId){
        ResponseEntity<PlayListResponse> result = spotifyApi.performGet("users/esplaylistbot/playlists/" + playlistId, PlayListResponse.class, "loading playlist " + playlistId);

        return verifyResult(result).getBody();
    }

    public Tracks loadPlaylist(String url){
        ResponseEntity<Tracks> result = spotifyApi.performGet(url, Tracks.class, "loading playlist from " + url);
        if(result.getStatusCode() == HttpStatus.NOT_FOUND){
            logger.warn("Playlist at {} not found", url);
            return Tracks.empty();
        }
        return verifyResult(result).getBody();
    }

    public List<TrackId> getTracksOfPlaylist(String playlistId){
        return getTrackData(playlistId).stream().map(td -> td.getTrack().getId()).map(TrackId::of).collect(Collectors.toList());
    }

    public String getPlaylistByName(String name){
        return getPlaylists().get(name);
    }

    public void addTrackToPlaylists(String trackId, List<String> playlistNames){
        logger.info("Adding to playlists {}", playlistNames);
        getOrCreatePlaylists(playlistNames).forEach(id -> addTrackToPlaylist(id, trackId));
    }

    private Stream<String> getOrCreatePlaylists(List<String> playlistNames) {
        return playlistNames.stream().map(this::getOrCreatePlaylist);
    }

    public String getOrCreatePlaylist(String name) {
        Map<String, String> playlistMap = getPlaylists();
        return playlistMap.computeIfAbsent(name,  n-> createPlaylist(n).getId());
    }

    private Map<String, String> getPlaylists() {
        return playlistCache.get("IneedSomekey").stream().distinct().collect(toMap(PlayListResponse::getName, PlayListResponse::getId));
    }

    private List<PlayListResponse> listPlayLists(){
        PlaylistListResponse list = spotifyApi.performGet("users/esplaylistbot/playlists", PlaylistListResponse.class, "Initial get all playlists").getBody();
        List<PlayListResponse> result = list.getItems();
        String next = list.getNext();
        while(next != null){
            PlaylistListResponse playlistList = spotifyApi.performGet(next, PlaylistListResponse.class, "Getting all playlists (contd.)").getBody();
            result.addAll(playlistList.getItems());
            next = playlistList.getNext();
        }
        return result;
    }

    public void renamePlaylist(String playlistId, String newName){
        spotifyApi.performPut("playlists/" + playlistId, new EditPlaylistRequest(newName), ExpectedEmptyResponse.class, "Editing playlist");
        playlistCache.invalidateAll();
    }

    public void replaceTracks(String playlistId, List<String> trackUris){
        spotifyApi.performPut("playlists/{playlistId}/tracks", new ReplaceTracksRequest(trackUris), ExpectedEmptyResponse.class, "Replacing tracks of playlist", playlistId);
        trackIdCache.put(playlistId, new HashSet<>(trackUris));
    }



    private <T> ResponseEntity<T> verifyResult(ResponseEntity<T> result) {
        if(!result.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Unable to get playlist: " + result.getStatusCode().getReasonPhrase());
        }
        return result;
    }


}
