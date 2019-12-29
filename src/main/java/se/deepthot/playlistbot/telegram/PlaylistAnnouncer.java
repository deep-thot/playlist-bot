package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.AuthenticationProperties;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.auth.SpotifyUser;
import se.deepthot.playlistbot.spotify.playlist.PlayListResponse;
import se.deepthot.playlistbot.spotify.playlist.PlaylistConfig;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static se.deepthot.playlistbot.theme.WeeklyPlaylist.*;

/**
 * Created by eruenion on 2017-06-17.
 */

@Service
public class PlaylistAnnouncer {

    private final TelegramBot telegramBot;
    private final PlaylistHandler playlistHandler;
    private final TelegramConfig telegramConfig;
    private final AuthenticationProperties authenticationProperties;

    private static final Logger logger = LoggerFactory.getLogger(PlaylistAnnouncer.class);

    public PlaylistAnnouncer(TelegramBot telegramBot, PlaylistHandler playlistHandler, TelegramConfig telegramConfig, AuthenticationProperties authenticationProperties) {
        this.telegramBot = telegramBot;
        this.playlistHandler = playlistHandler;
        this.telegramConfig = telegramConfig;
        this.authenticationProperties = authenticationProperties;
    }

    @Scheduled(cron = "00 00 21 * * SUN", zone = "Europe/Stockholm")
    public void weekSchedule(){
        weeklyPlaylist();
    }


    private SpotifyUser getUser(){
        return authenticationProperties.user();
    }


    private void weeklyPlaylist() {
        if(playlistHandler.hasPlaylistByName(prefixed(getCurrentWeeksPlaylist()), getUser())){
            logger.info("There is already a playlist for this week. Did someone run this before?");
            return;
        }
        PlayListResponse weeklyPlaylist = playlistHandler.getOrCreatePlaylist(prefixed(getLastWeeksPlaylist()), getUser());
        playlistHandler.renamePlaylist(weeklyPlaylist.getId(), prefixed(getCurrentWeeksPlaylist()), getUser().getAuthSession());
        PlayListResponse intraWeekPlaylist = playlistHandler.getOrCreatePlaylist(prefixed(getIntraWeekPlaylist()), getUser());
        List<TrackId> tracksThisWeek = playlistHandler.getTracksOfPlaylist(intraWeekPlaylist.getId(), getUser());
        List<String> trackUris = tracksThisWeek.stream().map(TrackId::getSpotifyUrl).collect(toList());
        playlistHandler.replaceTracks(weeklyPlaylist.getId(), trackUris, getUser());
        playlistHandler.replaceTracks(intraWeekPlaylist.getId(), Collections.emptyList(), getUser());

        postMessageToChannel("Då summerar vi den gångna veckan. Totalt uppfattade jag " + trackUris.size() + " låtar. \n " + playlistUrl(weeklyPlaylist));
    }

    private String prefixed(String currentWeeksPlaylist) {
        return PlaylistConfig.PLAYLIST_PREFIX + currentWeeksPlaylist;
    }

    private static String playlistUrl(PlayListResponse playlistId){
        return playlistId.getExternal_urls().getSpotify();
    }

    @SuppressWarnings("UnusedReturnValue")
    private SendResponse postMessageToChannel(String text) {
        return telegramBot.execute(new SendMessage(telegramConfig.getMainChatId(), text));
    }







}
