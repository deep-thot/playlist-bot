package se.deepthot.playlistbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import se.deepthot.playlistbot.telegram.TelegramConfig;

/**
 * Created by Eruenion on 2017-03-07.
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    private String botToken;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public TelegramBot bot(TelegramConfig telegramConfig){
        return TelegramBotAdapter.build(telegramConfig.getBotToken());
    }



}
