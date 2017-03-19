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
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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

    private List<TrackId> filterNewTracks(String playlistId, List<TrackId> trackIds) {
        PlayListResponse playlist = getPlaylist(playlistId);
        Set<String> existingTracks = playlist.getTracks().getItems().stream().map(Tracks.TrackData::getTrack).map(Tracks.Track::getId).collect(toSet());
        return trackIds.stream().filter(t -> !existingTracks.contains(t.getId())).collect(toList());
    }

    public PlayListResponse getPlaylist(String playlistId){
        ResponseEntity<PlayListResponse> result = restTemplate.exchange(RequestEntity.get(URI.create("https://api.spotify.com/v1/users/eruenion/playlists/" + playlistId))
                .header("Authorization", authenticationService.getAuthHeader()).build(), PlayListResponse.class);
        verifyResult(result);
        return result.getBody();
    }

    private void verifyResult(ResponseEntity<?> result) {
        if(!result.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Unable to get playlist: " + result.getStatusCode().getReasonPhrase());
        }
    }


}
