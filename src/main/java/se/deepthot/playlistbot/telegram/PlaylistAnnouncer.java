package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;
import se.deepthot.playlistbot.spotify.playlist.TrackGuesser;
import se.deepthot.playlistbot.theme.YearTheme;

import java.util.Optional;

/**
 * Created by eruenion on 2017-06-17.
 */

@Service
public class PlaylistAnnouncer {

    private final TelegramBot telegramBot;
    private final PlaylistHandler playlistHandler;
    private final TrackGuesser trackGuesser;
    private final TelegramConfig telegramConfig;

    private static final Logger logger = LoggerFactory.getLogger(PlaylistAnnouncer.class);

    public PlaylistAnnouncer(TelegramBot telegramBot, PlaylistHandler playlistHandler, TrackGuesser trackGuesser, TelegramConfig telegramConfig) {
        this.telegramBot = telegramBot;
        this.playlistHandler = playlistHandler;
        this.trackGuesser = trackGuesser;
        this.telegramConfig = telegramConfig;
    }

    @Scheduled(cron = "0 58 23 * * SAT", zone = "Europe/Stockholm")
    public void newPlaylist(){
        Optional<Integer> year = YearTheme.getCurrentYear();
        year.ifPresent(currentYear -> {
            String playlistId = playlistHandler.getOrCreatePlaylist("Musiksnack - #" + currentYear);
            Optional<TrackId> trackId = trackGuesser.guessTrack("bot year:" + currentYear);

            trackId.ifPresent(t -> playlistHandler.addTrackToPlaylist(playlistId, t.getId()));

            logger.info("sending message to chat {}", telegramConfig.getMainChatId());
            SendResponse response = postMessageToChannel("Ny vecka, nytt år. Nu kör vi #" + currentYear + " \n https://open.spotify.com/user/esplaylistbot/playlist/" + playlistId);
            logger.info("Got response {}", response);
            logger.info("Error code {}, Description {}, parameters {}", response.errorCode(), response.description(), response.parameters());
        });
        year.orElseGet(() -> postMessageToChannel("Nej, nu gör vi något annat va? Annars måste någon säga åt mig att lägga in ett år till.").errorCode());
    }

    private SendResponse postMessageToChannel(String text) {
        return telegramBot.execute(new SendMessage(telegramConfig.getMainChatId(), text));
    }


}
