package se.deepthot.playlistbot.theme;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Created by eruenion on 2017-06-17.
 */
public class YearTheme {

    private static final int initialYear = 2016;
    private static final int finalYear = 1950;
    private static final LocalDate startDate = LocalDate.of(2017, 6, 17);

    public static Optional<Integer> getCurrentYear(){

        return Optional.of(initialYear - getCurrentWeek()).filter(y -> y >= finalYear);
    }

    private static int getCurrentWeek() {
        return (int) startDate.until(LocalDate.now(), ChronoUnit.WEEKS);
    }




}
