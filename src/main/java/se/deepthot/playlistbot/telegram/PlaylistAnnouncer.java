package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
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

    public PlaylistAnnouncer(TelegramBot telegramBot, PlaylistHandler playlistHandler, TrackGuesser trackGuesser, TelegramConfig telegramConfig) {
        this.telegramBot = telegramBot;
        this.playlistHandler = playlistHandler;
        this.trackGuesser = trackGuesser;
        this.telegramConfig = telegramConfig;
    }

    @Scheduled(cron = "0 29 23 * * SAT", zone = "Europe/Stockholm")
    public void newPlaylist(){
        int currentYear = YearTheme.getCurrentYear();
        String playlistId = playlistHandler.getOrCreatePlaylist("Musiksnack - #" + currentYear);
        Optional<TrackId> trackId = trackGuesser.guessTrack("bot year:2016");

        trackId.ifPresent(t -> playlistHandler.addTrackToPlaylist(playlistId, t.getId()));

        telegramBot.execute(new SendMessage(telegramConfig.getMainChatlId(), "Ny vecka, nytt år. Nu kör vi #" + currentYear + " \n https://open.spotify.com/user/esplaylistbot/playlist/" + playlistId ));
    }

}
