package se.deepthot.playlistbot.theme;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by eruenion on 2017-06-17.
 */
public class YearTheme {

    private static final int initialYear = 2016;
    private static final int finalYear = 1950;
    private static final int startWeek = 24;

    public static Optional<Integer> getCurrentYear(){
        return Optional.of(initialYear - (getCurrentWeek() - startWeek)).filter(y -> y >= finalYear);
    }

    private static int getCurrentWeek() {
        return LocalDate.now().get(WeekFields.of(Locale.forLanguageTag("sv-SE")).weekOfWeekBasedYear());
    }




}
