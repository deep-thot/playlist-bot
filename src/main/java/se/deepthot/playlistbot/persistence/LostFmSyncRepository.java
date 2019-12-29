package se.deepthot.playlistbot.persistence;


import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;

public interface LostFmSyncRepository extends Repository<LostFmSyncConfig, String> {

    Optional<LostFmSyncConfig> getByTelegramId(Long telegramId);

    Iterable<LostFmSyncConfig> findAll();

    Collection<LostFmSyncConfig> findAllBySpotifyRefreshTokenNotNull();

    LostFmSyncConfig save(LostFmSyncConfig config);
}
