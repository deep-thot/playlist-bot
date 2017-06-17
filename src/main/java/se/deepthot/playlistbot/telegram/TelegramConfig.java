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
    private Long mainChatlId;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public Long getMainChatlId() {
        return mainChatlId;
    }

    public void setMainChatlId(Long mainChatlId) {
        this.mainChatlId = mainChatlId;
    }
}
