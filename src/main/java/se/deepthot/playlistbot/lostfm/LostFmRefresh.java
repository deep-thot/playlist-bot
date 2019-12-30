package se.deepthot.playlistbot.lostfm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.persistence.LostFmSyncRepository;

@Service
public class LostFmRefresh {

    private static final Logger logger = LoggerFactory.getLogger(LostFmRefresh.class);

    private final LostFmSyncRepository syncRepository;
    private final LostFmService lostFmService;

    public LostFmRefresh(LostFmSyncRepository syncRepository, LostFmService lostFmService) {
        this.syncRepository = syncRepository;
        this.lostFmService = lostFmService;
    }


    @Scheduled(cron = "00 00 06 * * *", zone = "Europe/Stockholm")
    public void syncAll(){
        syncRepository.findAllBySpotifyRefreshTokenNotNull().forEach(config -> lostFmService.generateLostFmPlaylist(config, r -> {}, e -> logger.warn("Error refreshing config for user {}", config.getTelegramId(), e)));
    }




}
