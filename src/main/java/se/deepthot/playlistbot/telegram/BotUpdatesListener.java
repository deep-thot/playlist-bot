package se.deepthot.playlistbot.telegram;

import com.google.common.collect.Sets;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.playlist.PlaylistConfig;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;
import se.deepthot.playlistbot.spotify.search.TrackGuesser;
import se.deepthot.playlistbot.theme.WeeklyPlaylist;
import se.deepthot.playlistbot.youtube.TrackResource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

/**
 * Created by Eruenion on 2017-03-10.
 */
@ConfigurationProperties("spotify.playlist")
@Service
public class BotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(BotUpdatesListener.class);
    private static final Pattern trackPattern = Pattern.compile("https://open.spotify.com/track/([\\w\\d]+)");
    private static final Pattern youtubePattern = Pattern.compile(UpdateClassifier.YOUTUBE_TRACK_PATTERN);

    private static final Set<String> playlistHashTagPatterns = Sets.newHashSet("#30daysongchallenge", "#day[\\d]{2}", "#[12][09][0-9]{2}");

    private final TelegramBot telegramBot;
    private final PlaylistHandler playlistHandler;
    private final UpdateClassifier updateClassifier;
    private final TrackResource trackResource;
    private final TrackGuesser trackGuesser;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    private String playlistId;

    @Inject
    public BotUpdatesListener(TelegramBot telegramBot, PlaylistHandler playlistHandler, UpdateClassifier updateClassifier, TrackResource trackResource, TrackGuesser trackGuesser) {
        this.telegramBot = telegramBot;
        this.playlistHandler = playlistHandler;
        this.updateClassifier = updateClassifier;
        this.trackResource = trackResource;
        this.trackGuesser = trackGuesser;
    }

    @Override
    public int process(List<Update> list) {
        try {
            List<IncomingMessage> messages = list.stream().map(updateClassifier::classify).filter(IncomingMessage::shouldHandle).collect(toList());
            messages.forEach(m -> {
                switch (m.getType()) {
                    case SPOTIFY_LINK: {
                        handleSpotifyLink(m);
                        break;
                    }
                    case YOUTUBE_LINK: {
                        String trackId = extractYoutubeId(m.getText());
                        String title = trackResource.getTrack(trackId);
                        trackGuesser.guessTrack(title).ifPresent(id -> addToPlaylists(m, id.getId()));
                        break;
                    }
                    case PLAYLIST_COMMAND: {
                        String playlist = playlistHandler.getPlaylistByName(PlaylistConfig.PLAYLIST_PREFIX + m.getUserName());
                        String text = getPersonalPLaylist(playlist);
                        telegramBot.execute(new SendMessage(m.getText(), text + "\n\n hela kanalens playlist med _allt_ hittar du på https://open.spotify.com/user/esplaylistbot/playlist/" + playlistId).parseMode(ParseMode.Markdown));
                    }
                }
            });

        } catch (Throwable e) {
            logger.error("", e);
        }
        return CONFIRMED_UPDATES_ALL;
    }

    private String getPersonalPLaylist(String playlist) {
        String text;
        if(playlist == null){
            text = "Du har ingen playlist än. Skicka några youtube eller spotify-länkar till mig först.";
        } else {
            text = "Din playlist hittar du på https://open.spotify.com/user/esplaylistbot/playlist/" + playlist;
        }
        return text;
    }

    private void handleSpotifyLink(IncomingMessage m) {
        Matcher matcher = trackPattern.matcher(m.getText());
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        String trackId = matcher.group(1);
        logger.info("Found track {}", trackId);

        addToPlaylists(m, trackId);
    }

    private void addToPlaylists(IncomingMessage m, String trackId) {
        List<String> playlistNames = Stream.of(filterHashTags(m.getHashTags()), Stream.of(WeeklyPlaylist.getIntraWeekPlaylist()), Stream.of(m.getUserName())).flatMap(identity()).map(PlaylistConfig.PLAYLIST_PREFIX::concat).collect(toList());

        playlistHandler.addTrackToPlaylists(trackId, playlistNames);


        addTrack(trackId, playlistId);
    }

    private static Stream<String> filterHashTags(List<String> hashTags) {
        return hashTags.stream().distinct().filter(tag -> playlistHashTagPatterns.stream().anyMatch(tag::matches));
    }


    private void addTrack(String trackId, String playListId) {
        playlistHandler.addTrackToPlaylist(playListId, trackId);
    }

    private String extractYoutubeId(String text){
        Matcher matcher = youtubePattern.matcher(text);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        return matcher.group(4);
    }


    @PostConstruct
    public void startListener() {
        telegramBot.setUpdatesListener(this);
        logger.info("Listening...");
    }


}
