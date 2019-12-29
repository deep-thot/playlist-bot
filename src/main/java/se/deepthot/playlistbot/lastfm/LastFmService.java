package se.deepthot.playlistbot.lastfm;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.umass.lastfm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import se.deepthot.playlistbot.lastfm.domain.Track;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@ConfigurationProperties("lastfm")
@Service
public class LastFmService {

    private static final Logger logger = LoggerFactory.getLogger(LastFmService.class);

    private final LoadingCache<UserPage, PaginatedResult<de.umass.lastfm.Track>> recentTracksCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(this::getRecentTracks);

    private String apiToken;
    private static final Supplier<LocalDateTime> searchUntil = () -> LocalDateTime.now().minusYears(1);


    public Stream<Track> getTopUnplayedTracks(String user) {
        return getTopTracks(user)
                .map(t -> new Track(t.getArtist(), t.getAlbum(), t.getName(), getLastPlayed(t, user).orElse(null)))
                .filter(t -> !t.getLastPlayed().isPresent());
    }

    public boolean verifyUsername(String user) {
        return User.getInfo(user, apiToken) != null;
    }

    private de.umass.lastfm.Track getInfo(String user, de.umass.lastfm.Track t) {
        de.umass.lastfm.Track track = de.umass.lastfm.Track.getInfo(t.getArtist(), t.getName(), Locale.getDefault(), user, apiToken);
        if(track == null) {
            return t;
        }
        return track;
    }

    private Optional<LocalDateTime> getLastPlayed(de.umass.lastfm.Track track, String user){
        int currentPage = 1;
        int totalPages = Integer.MAX_VALUE;
        LocalDateTime lastSeenDate = LocalDateTime.now();
        while(totalPages>=currentPage && lastSeenDate.isAfter(searchUntil.get())){
            PaginatedResult<de.umass.lastfm.Track> recentTracks = Objects.requireNonNull(recentTracksCache.get(new UserPage(currentPage, user)));
            if(recentTracks.isEmpty()){
                logger.info("Got to the end!");
                return Optional.empty();
            }
            for(de.umass.lastfm.Track t: recentTracks.getPageResults()){
                if(t.getUrl().equals(track.getUrl())){
                    return Optional.of(convert(t.getPlayedWhen()));
                }
                lastSeenDate = convert(t.getPlayedWhen());
            }
            totalPages = recentTracks.getTotalPages();
            currentPage++;

        }
        return Optional.empty();
    }

    private PaginatedResult<de.umass.lastfm.Track> getRecentTracks(UserPage userPage) {
        return User.getRecentTracks(userPage.getUser(), userPage.getPage(), 200, apiToken);
    }

    private LocalDateTime convert(Date playedWhen) {
        return playedWhen.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    private Collection<de.umass.lastfm.Track> getTopTracks(String user, int page){
        Result result = Caller.getInstance().call("user.getTopTracks", this.apiToken, "user", user, "period", Period.OVERALL.getString(), "page", page + "");
        return ResponseBuilder.buildCollection(result, de.umass.lastfm.Track.class);
    }

    private Stream<de.umass.lastfm.Track> getTopTracks(String user){
        AtomicBoolean hasMore = new AtomicBoolean(true);
        return IntStream.range(1, 10000)
                .boxed()
                .filter(i -> hasMore.get())
                .map(page -> getTopTracks(user, page))
                .peek(result -> hasMore.set(!result.isEmpty()))
                .flatMap(Collection::stream);
    }

    private static class UserPage {
        private final int page;
        private final String user;

        private UserPage(int page, String user) {
            this.page = page;
            this.user = user;
        }

        public int getPage() {
            return page;
        }

        public String getUser() {
            return user;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPage userPage = (UserPage) o;
            return page == userPage.page &&
                    user.equals(userPage.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(page, user);
        }
    }
}
