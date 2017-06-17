package se.deepthot.playlistbot.theme;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Created by eruenion on 2017-06-17.
 */
public class YearTheme {

    private static final int initialYear = 2016;
    private static final int finalYear = 1950;
    private static final int startWeek = 24;

    public static int getCurrentYear(){
        return initialYear - (getCurrentWeek() -  startWeek);
    }

    private static int getCurrentWeek() {
        return LocalDate.now().get(WeekFields.of(Locale.forLanguageTag("sv-SE")).weekOfWeekBasedYear());
    }




}
