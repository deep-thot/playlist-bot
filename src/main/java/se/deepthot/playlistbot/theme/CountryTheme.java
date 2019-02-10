package se.deepthot.playlistbot.theme;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;

import static java.util.Arrays.asList;

public class CountryTheme {
    private static final List<String> countries = asList(
            "albanien",
            "algeriet",
            "argentina",
            "australien",
            "belgien",
            "brasilien",
            "bulgarien",
            "chile",
            "danmark",
            "estland",
            "finland",
            "frankrike",
            "grekland",
            "indien",
            "irland",
            "island",
            "israel",
            "italien",
            "jamaica",
            "japan",
            "kanada",
            "kina",
            "kroatien",
            "lettland",
            "litauen",
            "marocko",
            "mexiko",
            "nederländerna",
            "norge",
            "nyazeeland",
            "polen",
            "portugal",
            "rumänien",
            "ryssland",
            "schweiz",
            "serbien",
            "slovakien",
            "slovenien",
            "spanien",
            "storbritannien",
            "sverige",
            "sydafrika",
            "sydkorea",
            "thailand",
            "tjeckien",
            "tunisien",
            "turkiet",
            "tyskland",
            "ukraina",
            "ungern",
            "usa",
            "österrike");


    public static String getRandomCountry(){
        return countries.get(RandomUtils.nextInt(0, countries.size()));
    }

    public static boolean isCountryHashTag(String hashTag){
        return countries.contains(hashTag.toLowerCase().replaceFirst("#", ""));
    }

}
