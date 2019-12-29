package se.deepthot.playlistbot.telegram;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SetupSessions {
    private final Map<Long, Integer> sessionMap = new HashMap<>();

    Optional<Integer> findUserSession(Long chatId){
        return Optional.ofNullable(sessionMap.get(chatId));
    }

    void addSession(Long chatId, Integer userId){
        sessionMap.put(chatId, userId);
    }
}
