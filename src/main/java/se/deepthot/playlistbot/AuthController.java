package se.deepthot.playlistbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.deepthot.playlistbot.spotify.playlist.PlayListResponse;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;

import javax.inject.Inject;

/**
 * Created by Eruenion on 2017-03-07.
 */
@RestController
@RequestMapping("/cometopapa")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String playlistId = "29C1ia9Dc1s9wYAhXKcug7";

    private final PlaylistHandler playlistHandler;

    @Inject
    public AuthController(PlaylistHandler playlistHandler) {
        this.playlistHandler = playlistHandler;
    }

    @GetMapping("")
    public String getAuthDetails(@RequestParam String code, @RequestParam(required = false) String state){
        logger.info("Got auth code {} and state {}", code, state);
        return code;
    }




    @GetMapping("/playlist")
    public PlayListResponse getPlayList(){
        return playlistHandler.getPlaylist(playlistId);
    }


}
