package se.deepthot.playlistbot.spotify.track;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static se.deepthot.playlistbot.spotify.track.TrackClassifier.classify;
import static se.deepthot.playlistbot.spotify.track.TrackType.*;

public class TrackClassifierTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void soilwork_is_metal() throws IOException {
        AudioFeatures features = readFeatures("soilwork");

        TrackType result = classify(features);
        assertThat(result, is(METAL));
    }

    @Test
    public void miho_arai_is_calm() throws IOException {
        AudioFeatures features = readFeatures("miho_arai");

        assertThat(classify(features), is(CALM));
    }

    @Test
    public void immortal_technique_is_rap() throws IOException {
        AudioFeatures features = readFeatures("immortal_technique");

        assertThat(classify(features), is(RAP));
    }

    @Test
    public void sarek_is_party() throws IOException {
        AudioFeatures features = readFeatures("sarek");

        assertThat(classify(features), is(PARTY));
    }

    @Test
    public void prodigy_is_party() throws IOException {
        AudioFeatures features = readFeatures("prodigy");

        assertThat(classify(features), is(PARTY));
    }

    @Test
    public void eminem_is_rap() throws IOException {
        assertThat(classify(readFeatures("eminem")), is(RAP));
    }

    @Test
    public void slagsmalsklubben_is_party() throws IOException {
        assertThat(classify(readFeatures("slagsmålsklubben")), is(PARTY));
    }

    @Test
    public void ken_ring_is_rap() throws IOException {
        assertThat(classify(readFeatures("ken_ring")), is(RAP));
    }

    @Test
    public void hellsongs_is_calm() throws IOException {
        assertThat(classify(readFeatures("hellsongs")), is(CALM));
    }



    private AudioFeatures readFeatures(final String filename) throws IOException {
        return objectMapper.readValue(new ClassPathResource(filename + ".json").getURL(), AudioFeatures.class);
    }

}