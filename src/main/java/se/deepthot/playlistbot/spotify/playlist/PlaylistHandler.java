package se.deepthot.playlistbot.spotify.playlist;

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
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

/**
 * Created by Eruenion on 2017-03-08.
 */
@Service
public class PlaylistHandler {

   private final PlaylistConfig playlistConfig;
   private final RestTemplate restTemplate;
   private final AuthenticationService authenticationService;

   private static final Logger logger = LoggerFactory.getLogger(PlaylistHandler.class);


   @Inject
    public PlaylistHandler(PlaylistConfig playlistConfig, RestTemplate restTemplate, AuthenticationService authenticationService) {
        this.playlistConfig = playlistConfig;
        this.restTemplate = restTemplate;
       this.authenticationService = authenticationService;
   }

    public void renamePlaylist(String name){
        ResponseEntity<Void> result = restTemplate.exchange(RequestEntity
                .put(URI.create("https://api.spotify.com/v1/users/eruenion/playlists/" + playlistConfig.getPlaylistId()))
                .header("Authorization", authenticationService.getAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RenamePlaylistRequest(name)), Void.class);
        logger.info("Renamed playlist");

    }

    public void addTracksToPlaylist(String playlistId, List<TrackId> trackIds){
        List<TrackId> nonDuplicates =  filterNewTracks(playlistId, trackIds);
        if(nonDuplicates.isEmpty()){
            logger.info("No new tracks to add. ({} already exist in playlist)", trackIds);
            return;
        }
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", authenticationService.getAuthHeader());
        HttpEntity<AddTracksRequest> entity = new HttpEntity<>(new AddTracksRequest(nonDuplicates.stream().map(TrackId::getSpotifyUrl).collect(toList())), headers);
        try {
            ResponseEntity<AddTracksResponse> result = restTemplate.exchange("https://api.spotify.com/v1/users/eruenion/playlists/{playlistId}/tracks", HttpMethod.POST, entity, AddTracksResponse.class, playlistId);
            verifyResult(result);
            logger.info("Added tracks {}", trackIds);
        } catch(HttpClientErrorException e){
            logger.error("Error in request, status {}, message {}", e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private void addTrackToPlaylist(String playlistId, String trackId){
        addTracksToPlaylist(playlistId, singletonList(TrackId.of(trackId)));
    }

    public PlayListResponse createPlaylist(String name){
        ResponseEntity<PlayListResponse> result = restTemplate.exchange(RequestEntity.post(URI.create("https://api.spotify.com/v1/users/eruenion/playlists")).contentType(MediaType.APPLICATION_JSON).header("Authorization", authenticationService.getAuthHeader()).body(new CreatePlaylistRequest(name, true)), PlayListResponse.class);
        verifyResult(result);
        PlayListResponse body = result.getBody();
        logger.info("Created new playlist \"{}\" ({})", body.getName(), body.getId());
        return body;
    }

    private List<TrackId> filterNewTracks(String playlistId, List<TrackId> trackIds) {
        Set<String> existingTracks = getAllTrackIds(playlistId);
        return trackIds.stream().filter(t -> !existingTracks.contains(t.getId())).collect(toList());
    }

    private Set<String> getTrackIds(Tracks tracks) {
        return tracks.getItems().stream().map(Tracks.TrackData::getTrack).map(Tracks.Track::getId).collect(toSet());
    }

    private Set<String> getAllTrackIds(String playlistId){
        PlayListResponse playlist = getPlaylist(playlistId);
        String next = playlist.getTracks().getNext();
        Set<String> result = getTrackIds(playlist.getTracks());
        while(next != null){
            Tracks response = performGet(next, Tracks.class).getBody();
            result.addAll(getTrackIds(response));
            next = response.getNext();
        }
        return result;
    }

    private <T> ResponseEntity<T> performGet(String next, Class<T> responseType) {
        try {
            return restTemplate.exchange(RequestEntity.get(URI.create(next)).header("Authorization", authenticationService.getAuthHeader()).build(), responseType);
        } catch(HttpClientErrorException e){
            logger.warn("Request returned status {}: {}. Headers: {}",e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders());
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    public PlayListResponse getPlaylist(String playlistId){
        ResponseEntity<PlayListResponse> result = restTemplate.exchange(RequestEntity.get(URI.create("https://api.spotify.com/v1/users/eruenion/playlists/" + playlistId))
                .header("Authorization", authenticationService.getAuthHeader()).build(), PlayListResponse.class);
        verifyResult(result);
        return result.getBody();
    }

    public String getPlaylistByName(String name){
        return getPlaylists().get(name);
    }

    public void addTrackToPlaylists(String trackId, List<String> playlistNames){
        logger.info("Adding to playlists {}", playlistNames);
        Map<String, String> playlistMap = getPlaylists();
        playlistNames.stream().map(name -> playlistMap.computeIfAbsent(name,  n-> createPlaylist(n).getId())).forEach(id -> addTrackToPlaylist(id, trackId));
    }

    private Map<String, String> getPlaylists() {
        return listPlayLists().stream().distinct().collect(toMap(PlayListResponse::getName, PlayListResponse::getId));
    }

    private List<PlayListResponse> listPlayLists(){
        PlaylistListResponse list = performGet("https://api.spotify.com/v1/users/eruenion/playlists", PlaylistListResponse.class).getBody();
        List<PlayListResponse> result = list.getItems();
        String next = list.getNext();
        while(next != null){
            PlaylistListResponse playlistList = performGet(next, PlaylistListResponse.class).getBody();
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
