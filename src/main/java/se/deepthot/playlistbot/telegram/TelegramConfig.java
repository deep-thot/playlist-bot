package se.deepthot.playlistbot.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Jonatan on 2017-05-30.
 */
@ConfigurationProperties("telegram")
@Component
public class TelegramConfig {
    private String botToken;
    private String mainChatlId;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getMainChatlId() {
        return mainChatlId;
    }

    public void setMainChatlId(String mainChatlId) {
        this.mainChatlId = mainChatlId;
    }
}
