package se.deepthot.playlistbot.spotify.playlist;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.deepthot.playlistbot.spotify.AuthenticationService;
import se.deepthot.playlistbot.spotify.TrackId;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Eruenion on 2017-03-08.
 */
@Service
public class PlaylistHandler {

    private final RestTemplate restTemplate;
   private final AuthenticationService authenticationService;



   private static final Logger logger = LoggerFactory.getLogger(PlaylistHandler.class);
    private final LoadingCache<String, List<PlayListResponse>> playlistCache;
    private final LoadingCache<String, Set<String>> tracksCache;
    private final ScheduledExecutorService retryScheduler;


    @Inject
    public PlaylistHandler(RestTemplate restTemplate, AuthenticationService authenticationService) {
        this.restTemplate = restTemplate;
        this.authenticationService = authenticationService;
        this.playlistCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(s -> listPlayLists());
        tracksCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build(this::getAllTrackIds);
        retryScheduler = Executors.newSingleThreadScheduledExecutor();
    }


    public void addTrackToPlaylist(String playlistId, String trackId){
        if(tracksCache.get(playlistId).contains(trackId)){
            logger.info("No new tracks to add. ({} already exist in playlist)", trackId);
            return;
        }
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", authenticationService.getAuthHeader());
        HttpEntity<AddTracksRequest> entity = new HttpEntity<>(new AddTracksRequest(singletonList(TrackId.of(trackId).getSpotifyUrl())), headers);
        performWithRetry(() -> restTemplate.exchange("https://api.spotify.com/v1/users/esplaylistbot/playlists/{playlistId}/tracks", HttpMethod.POST, entity, AddTracksResponse.class, playlistId), "Add track " + trackId + " to playlist " + playlistId);
        logger.info("Added track {}", trackId);
        tracksCache.get(playlistId).add(trackId);

    }

    private <T> ResponseEntity<T> performWithRetry(Callable<ResponseEntity<T>> exchange, String title){
        try{
            return exchange.call();
        }
        catch(HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS){
                logger.info("Too many requests when performing {}", title);
                Optional<Integer> retryInSeconds = e.getResponseHeaders().getOrDefault("Retry-After", emptyList()).stream().findFirst().map(Integer::parseInt);
                return retryInSeconds.map(retry -> {
                    logger.info("Retrying in {} s", retry);
                    try {
                        return retryScheduler.schedule(exchange, retry + 1, TimeUnit.SECONDS).get(retry*2, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                        throw new RuntimeException(e1);
                    }
                }).orElse(new ResponseEntity<>(e.getStatusCode()));
            } else {
                logger.warn("Request returned status {}: {}. Headers: {}",e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders());
                return new ResponseEntity<>(e.getStatusCode());
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private PlayListResponse createPlaylist(String name){
        ResponseEntity<PlayListResponse> result = performWithRetry(() -> restTemplate.exchange(RequestEntity.post(URI.create("https://api.spotify.com/v1/users/esplaylistbot/playlists")).contentType(MediaType.APPLICATION_JSON).header("Authorization", authenticationService.getAuthHeader()).body(new CreatePlaylistRequest(name, true)), PlayListResponse.class), "Create playlist " + name);
        verifyResult(result);
        PlayListResponse body = result.getBody();
        logger.info("Created new playlist \"{}\" ({})", body.getName(), body.getId());
        playlistCache.invalidateAll();
        return body;
    }

    private Set<String> getTrackIds(Tracks tracks) {
        return tracks.getItems().stream().map(Tracks.TrackData::getTrack).map(Tracks.Track::getId).collect(toSet());
    }

    private Set<String> getAllTrackIds(String playlistId){
        PlayListResponse playlist = getPlaylist(playlistId);
        String next = playlist.getTracks().getNext();
        Set<String> result = getTrackIds(playlist.getTracks());
        while(next != null){
            Tracks response = performGet(next, Tracks.class, "Loading tracks for " + playlistId).getBody();
            result.addAll(getTrackIds(response));
            next = response.getNext();
        }
        return result;
    }

    private <T> ResponseEntity<T> performGet(String url, Class<T> responseType, String title) {
        return performWithRetry(() -> restTemplate.exchange(RequestEntity.get(URI.create(url)).header("Authorization", authenticationService.getAuthHeader()).build(), responseType), title);
    }

    public PlayListResponse getPlaylist(String playlistId){
        ResponseEntity<PlayListResponse> result = performGet("https://api.spotify.com/v1/users/esplaylistbot/playlists/" + playlistId, PlayListResponse.class, "loading playlist " + playlistId);
        verifyResult(result);
        return result.getBody();
    }

    public String getPlaylistByName(String name){
        return getPlaylists().get(name);
    }

    public void addTrackToPlaylists(String trackId, List<String> playlistNames){
        logger.info("Adding to playlists {}", playlistNames);
        Map<String, String> playlistMap = getPlaylists();
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
        PlaylistListResponse list = performGet("https://api.spotify.com/v1/users/esplaylistbot/playlists", PlaylistListResponse.class, "Initial get all playlists").getBody();
        List<PlayListResponse> result = list.getItems();
        String next = list.getNext();
        while(next != null){
            PlaylistListResponse playlistList = performGet(next, PlaylistListResponse.class, "Getting all playlists (contd.)").getBody();
            result.addAll(playlistList.getItems());
            next = playlistList.getNext();
        }
        return result;
    }



    private void verifyResult(ResponseEntity<?> result) {
        if(!result.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Unable to get playlist: " + result.getStatusCode().getReasonPhrase());
        }
    }


}
