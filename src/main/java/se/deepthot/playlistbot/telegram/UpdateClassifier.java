package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
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
        Message message = Optional.ofNullable(update.message()).orElse(update.editedMessage());

        if(message == null || message.entities() == null){
            logger.info("Ignoring update {}", update);
            return IncomingMessage.unknown();
        }
        String usernameOrForward = getUsernameOrForwardFrom(message);
        if(containsSpotifyTrack(message)){
            return IncomingMessage.spotify(message.text(), usernameOrForward);
        }
        if(isYoutubeTrack(message)){
            return IncomingMessage.youtube(message.text(), usernameOrForward);
        }
        if(isBotCommand(message)){
            return IncomingMessage.playlistCommand(message.chat().id() + "", getUsername(message));
        }
        logger.info("Ignoring message {}", message);
        return IncomingMessage.unknown();
    }

    private String getUsernameOrForwardFrom(Message message) {
        if(message.forwardFrom() != null){
            return message.forwardFrom().username();
        }
        return getUsername(message);
    }

    private String getUsername(Message message) {
        return message.from().username();
    }

    private boolean containsSpotifyTrack(Message message) {
        return isUrl(message) &&
                message.text().contains("https://open.spotify.com/track");
    }

    private boolean isUrl(Message message) {
        return hasEntity(url, message);
    }

    private boolean hasEntity(MessageEntity.Type type, Message message) {
        return stream(message.entities())
                        .anyMatch(m -> m.type() == type);
    }


    private boolean isBotCommand(Message message){
        return hasEntity(bot_command, message) && message.text().contains("/playlist");
    }

    private boolean isYoutubeTrack(Message message){
        return isUrl(message) && message.text().matches(YOUTUBE_TRACK_PATTERN);
    }
}
