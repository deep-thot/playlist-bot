package se.deepthot.playlistbot.telegram;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;

import static java.util.Arrays.stream;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class UpdateClassifier {

    IncomingMessage classify(Update update){
        if(containsSpotifyTrack(update)){
            return IncomingMessage.spotify(update.message().text());
        }
        if(isBotCommand(update)){
            return IncomingMessage.playlistCommand(update.message().chat().id() + "");
        }
        return IncomingMessage.unknown();
    }

    private boolean containsSpotifyTrack(Update u) {
        return u.message().entities() != null &&
                stream(u.message().entities())
                        .anyMatch(m -> m.type() == MessageEntity.Type.url) &&
                u.message().text().contains("https://open.spotify.com/track");
    }

    private boolean isBotCommand(Update u){
        return u.message().entities() != null && stream(u.message().entities()).anyMatch(m -> m.type() == MessageEntity.Type.bot_command);
    }
}
