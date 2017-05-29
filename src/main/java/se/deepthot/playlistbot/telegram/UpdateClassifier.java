package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.MessageEntity.Type.hashtag;
import static com.pengrad.telegrambot.model.MessageEntity.Type.url;
import static java.util.Arrays.stream;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class UpdateClassifier {

    private static final Logger logger = LoggerFactory.getLogger(UpdateClassifier.class);

    static final String YOUTUBE_TRACK_PATTERN = "[\\s\\S]*https://((www|m)\\.youtube\\.com/watch\\?([\\w=&.]+)?v=|youtu\\.be/)([\\w\\d\\-]{11})[\\s\\S]*";

    IncomingMessage classify(Update update){
        if(update.message() == null || update.message().entities() == null){
            logger.info("Ignoring update {}", update);
            return IncomingMessage.unknown(getUsername(update));
        }
        if(containsSpotifyTrack(update)){
            return IncomingMessage.spotify(update.message().text(), getUsername(update));
        }
        if(isYoutubeTrack(update)){
            return IncomingMessage.youtube(update.message().text(), getUsername(update));
        }
        if(isBotCommand(update)){
            return IncomingMessage.playlistCommand(update.message().chat().id() + "", getUsername(update));
        }
        logger.info("Ignoring message {}", update.message());
        return IncomingMessage.unknown(getUsername(update));
    }

    private String getUsername(Update update) {
        return update.message().from().username();
    }

    private boolean containsSpotifyTrack(Update u) {
        return isUrl(u) &&
                u.message().text().contains("https://open.spotify.com/track");
    }

    private boolean isUrl(Update u) {
        return hasEntity(u, url);
    }

    private boolean hasEntity(Update u, MessageEntity.Type type) {
        return stream(u.message().entities())
                        .anyMatch(m -> m.type() == type);
    }


    private boolean isBotCommand(Update u){
        return hasEntity(u, bot_command) && u.message().text().contains("/playlist");
    }

    private boolean isYoutubeTrack(Update u){
        return isUrl(u) && u.message().text().matches(YOUTUBE_TRACK_PATTERN);
    }
}
