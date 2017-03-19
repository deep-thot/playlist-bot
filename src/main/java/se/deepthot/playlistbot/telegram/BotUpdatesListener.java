package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.spotify.TrackId;
import se.deepthot.playlistbot.spotify.playlist.PlaylistHandler;
import se.deepthot.playlistbot.spotify.playlist.TrackGuesser;
import se.deepthot.playlistbot.youtube.TrackResource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
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
                        trackGuesser.guessTrack(title).ifPresent(id -> playlistHandler.addTracksToPlaylist(playlistId, singletonList(id)));
                        break;
                    }
                    case PLAYLIST_COMMAND: {
                        telegramBot.execute(new SendMessage(m.getText(), "https://open.spotify.com/user/eruenion/playlist/" + playlistId));
                    }
                }
            });

        } catch (Throwable e) {
            logger.error("", e);
        }
        return CONFIRMED_UPDATES_ALL;
    }

    private void handleSpotifyLink(IncomingMessage m) {
        Matcher matcher = trackPattern.matcher(m.getText());
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        String trackId = matcher.group(1);
        logger.info("Found track {}", trackId);

        playlistHandler.addTracksToPlaylist(playlistId, singletonList(TrackId.of(trackId)));
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
