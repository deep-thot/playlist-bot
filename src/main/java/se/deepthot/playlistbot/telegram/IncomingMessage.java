package se.deepthot.playlistbot.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * Created by Eruenion on 2017-03-19.
 */
public class IncomingMessage {

    private final Type type;

    private final String text;

    private final List<String> hashTags;

    private final String userName;

    private IncomingMessage(Type type, String text, List<String> hashTags, String userName) {
        this.type = type;
        this.text = text;
        this.hashTags = hashTags;
        this.userName = userName;
    }

    Type getType() {
        return type;
    }

    public static IncomingMessage spotify(String text, String userName){

        return createMessage(text, userName, Type.SPOTIFY_LINK);
    }

    private static IncomingMessage createMessage(String text, String userName, Type type) {
        List<String> hashTags = getHashTags(text);
        return new IncomingMessage(type, text, hashTags, userName);
    }

    private static List<String> getHashTags(String text) {
        Matcher matcher = Pattern.compile("(#[\\w]+)").matcher(text);
        List<String> hashTags = new ArrayList<>();
        while(matcher.find()){
            hashTags.add(matcher.group());
        }
        return hashTags;
    }

    public static IncomingMessage youtube(String text, String userName){
        return createMessage(text, userName, Type.YOUTUBE_LINK);
    }

    static IncomingMessage playlistCommand(String chatId, String userName){
        return new IncomingMessage(Type.PLAYLIST_COMMAND, chatId, emptyList(), userName);
    }

    static IncomingMessage unknown(String userName){
        return new IncomingMessage(Type.UNKNOWN, "", emptyList(), userName);
    }

    boolean shouldHandle(){
        return type != Type.UNKNOWN;
    }

    String getText() {
        return text;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public String getUserName() {
        return "@" + userName;
    }

    enum Type{
        SPOTIFY_LINK, YOUTUBE_LINK, PLAYLIST_COMMAND, UNKNOWN
    }

    public static void main(String[] args) {
        System.out.println(IncomingMessage.getHashTags("#day13 #30daysongchallenge https://youtu.be/UAKCR7kQMTQ"));
    }
}
