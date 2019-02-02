package se.deepthot.playlistbot.theme;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;

public class WeeklyPlaylist {

    public static String getNextWeeksPlaylist() {
        return getTitle(getCurrentWeek() + 1);
    }

    public static String getIntraWeekPlaylist() {
        return "current week";
    }

    public static String getLastWeeksPlaylist(){
        return getTitle(getCurrentWeek() - 1);
    }

    public static String getCurrentWeeksPlaylist(){
        return getTitle(getCurrentWeek());
    }

    private static String getTitle(int weekNumber) {
        return "v." + weekNumber;
    }

    private static int getCurrentWeek() {
        LocalDate now = LocalDate.now(ZoneId.of("Europe/Stockholm"));
        return now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
}
