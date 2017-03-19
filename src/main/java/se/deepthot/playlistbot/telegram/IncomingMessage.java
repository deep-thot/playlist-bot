package se.deepthot.playlistbot.telegram;

/**
 * Created by Eruenion on 2017-03-19.
 */
public class IncomingMessage {

    private final Type type;

    private final String text;

    private IncomingMessage(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    Type getType() {
        return type;
    }

    public static IncomingMessage spotify(String text){
        return new IncomingMessage(Type.SPOTIFY_LINK, text);
    }

    public static IncomingMessage youtube(String text){
        return new IncomingMessage(Type.YOUTUBE_LINK, text);
    }

    static IncomingMessage playlistCommand(String chatId){
        return new IncomingMessage(Type.PLAYLIST_COMMAND, chatId);
    }

    static IncomingMessage unknown(){
        return new IncomingMessage(Type.UNKNOWN, "");
    }

    boolean shouldHandle(){
        return type != Type.UNKNOWN;
    }

    String getText() {
        return text;
    }

    enum Type{
        SPOTIFY_LINK, YOUTUBE_LINK, PLAYLIST_COMMAND, UNKNOWN
    }
}
