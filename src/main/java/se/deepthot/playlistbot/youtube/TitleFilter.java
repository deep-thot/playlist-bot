package se.deepthot.playlistbot.youtube;

import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Eruenion on 2017-03-19.
 */
@Service
public class TitleFilter {

    private static List<String> keywords = asList("trailer", "hq", "lyrics", "full song");

    String filter(String title){
        String filtered = keywords.stream().reduce(title.toLowerCase(), (result, keyword) -> result.replace(keyword, ""));
        return filtered
                .replaceAll("\\(.+\\)", "")
                .replaceAll("\\[.+]", "")
                .replaceAll("\\*.+\\*", "")
                .replaceFirst("(.+-.+)\\|(.+)", "$1")
                .replaceAll("[\\-&]", " ")
                .replaceAll("[ ]{2,}", " ")
                .trim();
    }
}
