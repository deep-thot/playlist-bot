package se.deepthot.playlistbot.persistence;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@SpringBootTest
class LostFmSyncRepositoryTest {

    @Inject
    private LostFmSyncRepository repository;

    @Test
    void canSaveSyncConfig() {
        repository.save(new LostFmSyncConfig(1234L));
        Iterable<LostFmSyncConfig> all = repository.findAll();
        Optional<LostFmSyncConfig> result = repository.getByTelegramId(1234L);

        assertThat(IterableUtil.toCollection(all), hasSize(1));
        assertThat(result, isPresent());
    }

    @Test
    void canUpdateSyncConfig() {
        LostFmSyncConfig config = repository.save(new LostFmSyncConfig(1234L));
        config.setSpotifyRefreshToken("12sdfs23");
        config.setLastFmUsername("test");
        repository.save(config);

        Iterable<LostFmSyncConfig> all = repository.findAll();
        Optional<LostFmSyncConfig> result = repository.getByTelegramId(1234L);

        assertThat(IterableUtil.toCollection(all), hasSize(1));
        assertThat(result.map(LostFmSyncConfig::getLastFmUsername), isPresentAndIs("test"));
        assertThat(result.map(LostFmSyncConfig::getSpotifyRefreshToken), isPresentAndIs("12sdfs23"));
    }

    @Test
    void findAllWithSpotifyRefreshToken() {
        LostFmSyncConfig config = repository.save(new LostFmSyncConfig(12345L));
        LostFmSyncConfig withToken = new LostFmSyncConfig(5432211L);
        withToken.setSpotifyRefreshToken("test1234");
        repository.save(withToken);

        assertThat(repository.findAllBySpotifyRefreshTokenNotNull(), hasSize(1));
        assertThat(repository.findAllBySpotifyRefreshTokenNotNull().stream().findFirst().map(LostFmSyncConfig::getTelegramId), isPresentAndIs(5432211L));

    }
}