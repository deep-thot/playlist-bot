package se.deepthot.playlistbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.deepthot.playlistbot.lostfm.LostFmService;
import se.deepthot.playlistbot.persistence.LostFmSyncRepository;
import se.deepthot.playlistbot.spotify.AuthenticationService;
import se.deepthot.playlistbot.spotify.auth.SpotifyUser;
import se.deepthot.playlistbot.telegram.TelegramService;

/**
 * Created by Eruenion on 2017-03-07.
 */
@RestController
@RequestMapping("/lostfm-setup")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final LostFmSyncRepository syncRepository;
    private final TelegramService telegramService;
    private final LostFmService lostFmService;


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationService authenticationService, LostFmSyncRepository syncRepository, TelegramService telegramService, LostFmService lostFmService) {
        this.authenticationService = authenticationService;
        this.syncRepository = syncRepository;
        this.telegramService = telegramService;
        this.lostFmService = lostFmService;
    }


    @GetMapping("")
    public String getAuthDetails(@RequestParam String code, @RequestParam String state){
        SpotifyUser user = authenticationService.getAuthAndRefreshToken(code);
        long chatId = Long.parseLong(state);
        syncRepository.getByTelegramId(chatId).ifPresent(config -> {
            config.setSpotifyRefreshToken(user.getRefreshToken());
            config.setSpotifyUsername(user.getUsername());
            syncRepository.save(config);
            telegramService.sendMessage(chatId, "Authenticated successfully, generating your lost fm playlist. This may take a while...");
            lostFmService.generateLostFmPlaylist(config,
                    playlist -> telegramService.sendMessage(chatId, "Your lost fm playlist is available: " + playlist.getExternal_urls().getSpotify()),
                    e -> telegramService.sendMessage(chatId, String.format("I failed to generate your playlist :(. Something about %s", e.getMessage()))
            );
        });

        return "Thank you for completing lost fm setup. Your playlist will be available shortly";
    }



}
