package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.lastfm.LastFmService;
import se.deepthot.playlistbot.lostfm.LostFmService;
import se.deepthot.playlistbot.persistence.LostFmSyncConfig;
import se.deepthot.playlistbot.persistence.LostFmSyncRepository;
import se.deepthot.playlistbot.spotify.AuthenticationProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LostFmSetup {

    private final SetupSessions setupSessions;
    private final LostFmSyncRepository syncRepository;
    private final LastFmService lastFmService;
    private final AuthenticationProperties authenticationProperties;
    private final TelegramService telegramService;
    private final LostFmService lostFmService;

    public LostFmSetup(SetupSessions setupSessions, LostFmSyncRepository syncRepository, LastFmService lastFmService, AuthenticationProperties authenticationProperties, TelegramService telegramService, LostFmService lostFmService) {
        this.setupSessions = setupSessions;
        this.syncRepository = syncRepository;
        this.lastFmService = lastFmService;
        this.authenticationProperties = authenticationProperties;
        this.telegramService = telegramService;
        this.lostFmService = lostFmService;
    }

    public void handleLostFmSetup(List<Update> updates){
        updates.stream()
                .filter(u -> u.message() != null)
                .map(Update::message)
                .filter(m -> m.chat().type() == Chat.Type.Private)
                .forEach(m -> {
                    if(isStartCommand(m)){
                        setupSessions.addSession(m.chat().id(), m.from().id());
                        syncRepository.save(new LostFmSyncConfig(m.chat().id()));
                        sendReply(m, "Hi, please enter your last fm username");
                    } else if(expectingLastFmUserName(m)){
                        if(!lastFmService.verifyUsername(m.text())){
                            sendReply(m, String.format("I'm sorry, but %s does not appear to be an existing last fm user, please try again", m.text()));
                            return;
                        }
                        syncRepository.getByTelegramId(m.chat().id()).ifPresent(s -> {
                            s.setLastFmUsername(m.text());
                            syncRepository.save(s);
                            sendReply(m, String.format("Please visit %s to login to spotify and begin generating your playlist", authenticationProperties.authUrl(m.chat().id())));
                        });

                    } else if(isRefreshCommand(m)){
                        Optional<LostFmSyncConfig> syncConfig = syncRepository.getByTelegramId(m.chat().id());
                        if(!syncConfig.map(LostFmSyncConfig::getSpotifyRefreshToken).isPresent()) {
                            sendReply(m, "I don't think I know who you are. Please use /start to setup your playlist");
                        } else {
                            sendReply(m, "Refreshing your lost fm playlist... Please wait");
                            lostFmService.generateLostFmPlaylist(syncConfig.get(), r -> sendReply(m,"Playlist is refreshed"), e -> sendReply(m, "I failed to refresh your playlist. Sorry :(. I might be defective."));
                        }
                    }
                });
    }

    private boolean expectingLastFmUserName(Message m) {
        return setupSessions.findUserSession(m.chat().id()).isPresent() &&
                !syncRepository.getByTelegramId(m.chat().id()).map(LostFmSyncConfig::getLastFmUsername).isPresent();
    }

    private void sendReply(Message m, String text) {
        telegramService.sendMessage(m.chat().id(), text);
    }

    private boolean isStartCommand(Message m) {

        return isBotCommand(m) && (m.text().contains("/lost_fm") || m.text().contains("/start"));
    }

    private boolean isBotCommand(Message m) {
        return m.entities() != null && Arrays.stream(m.entities()).anyMatch(e -> e.type() == MessageEntity.Type.bot_command);
    }

    private boolean isRefreshCommand(Message m) {
        return isBotCommand(m) && m.text().contains("refresh");
    }
}
