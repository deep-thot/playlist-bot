package se.deepthot.playlistbot.spotify.domain;

import java.time.Year;
import java.time.format.DateTimeFormatter;

public class Album {
    private String id;
    private String name;
    private String release_date;
    private ReleaseDatePrecision release_date_precision;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setRelease_date_precision(ReleaseDatePrecision release_date_precision) {
        this.release_date_precision = release_date_precision;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public ReleaseDatePrecision getRelease_date_precision() {
        return release_date_precision;
    }

    public int getYear(){
        return Year.parse(getRelease_date(), DateTimeFormatter.ofPattern(getRelease_date_precision().getPattern())).getValue();
    }

    private enum ReleaseDatePrecision{
        year("yyyy"), month("yyyy-MM"), day("yyyy-MM-dd");

        private String pattern;

        ReleaseDatePrecision(String pattern){
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }
}
