package se.deepthot.playlistbot.youtube;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Eruenion on 2017-03-19.
 */
public class TitleFilterTest {

    private TitleFilter target;

    @Before
    public void setUp() throws Exception {
        target = new TitleFilter();
    }

    @Test
    public void removes_parenthesis() throws Exception{
        String result = target.filter("Hurra Torpedo - Total Eclipse Of The Heart (live)");
        assertThat(result, is("hurra torpedo total eclipse of the heart"));
    }

    @Test
    public void removes_brackets() throws Exception {
        String result = target.filter("Trevor Something - Fade Away [Explicit]");
        assertThat(result, is("trevor something fade away"));
    }

    @Test
    public void strips_everything_after_last_pipe(){
        String result = target.filter("atoma - rainmen | napalm records");
        assertThat(result, is("atoma rainmen"));
    }

    @Test
    public void retains_pipe_if_no_dash(){
        String result = target.filter("atoma | rainmen");
        assertThat(result, is("atoma | rainmen"));
    }

    @Test
    public void removesStuffbetweenAsterisks(){
        String result = target.filter("Frank Zappa  Bobby Brown *Official Video*");
        assertThat(result, is("frank zappa bobby brown"));
    }

    @Test
    public void replacesDashesWithSpaces(){
        String result = target.filter("bathory-one rode to asa bay");
        assertThat(result, is("bathory one rode to asa bay"));
    }

    @Test
    public void removesKeywords(){
        String result = target.filter("hey barberiba - full song");
        assertThat(result, is("hey barberiba"));
    }

}