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
    private Long mainChatId;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public Long getMainChatId() {
        return mainChatId;
    }

    public void setMainChatId(Long mainChatId) {
        this.mainChatId = mainChatId;
    }
}
