package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.util.Arrays.stream;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class UpdateClassifier {

    private static final Logger logger = LoggerFactory.getLogger(UpdateClassifier.class);

    static final String YOUTUBE_TRACK_PATTERN = "[\\s\\S]*https://((www|m)\\.youtube\\.com/watch\\?([\\w=&.]+)?v=|youtu\\.be/)([\\w\\d\\-]{11})[\\s\\S]*";

    IncomingMessage classify(Update update){
        if(containsSpotifyTrack(update)){
            return IncomingMessage.spotify(update.message().text());
        }
        if(isYoutubeTrack(update)){
            return IncomingMessage.youtube(update.message().text());
        }
        if(isBotCommand(update)){
            return IncomingMessage.playlistCommand(update.message().chat().id() + "");
        }
        logger.info("Ignoring message {}", update.message());
        return IncomingMessage.unknown();
    }

    private boolean containsSpotifyTrack(Update u) {
        return isUrl(u) &&
                u.message().text().contains("https://open.spotify.com/track");
    }

    private boolean isUrl(Update u) {
        return u.message().entities() != null &&
                stream(u.message().entities())
                        .anyMatch(m -> m.type() == MessageEntity.Type.url);
    }

    private boolean isBotCommand(Update u){
        return u.message().entities() != null && stream(u.message().entities()).anyMatch(m -> m.type() == MessageEntity.Type.bot_command);
    }

    private boolean isYoutubeTrack(Update u){
        return isUrl(u) && u.message().text().matches(YOUTUBE_TRACK_PATTERN);
    }
}
