package se.deepthot.playlistbot.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Eruenion on 2017-03-19.
 */
@ConfigurationProperties("youtube")
@Service
public class TrackResource {

    private String apiKey;

    private final TitleFilter titleFilter;

    @Inject
    public TrackResource(TitleFilter titleFilter) {
        this.titleFilter = titleFilter;
    }

    public String getTrack(String id){
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), httpRequest -> {
        }).build();
        try {
            YouTube.Videos.List listRequest = youTube.videos().list("snippet");
            listRequest.setId(id);
            listRequest.setKey(apiKey);
            VideoListResponse response = listRequest.execute();
            return response.getItems().stream().findFirst().map(Video::getSnippet).map(VideoSnippet::getTitle).map(titleFilter::filter).orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        TrackResource trackResource = new TrackResource(new TitleFilter());
        trackResource.setApiKey("AIzaSyCIY2HOzIpjbLs-bf2s2V136Y7bxRzyk44");
        System.out.println(trackResource.getTrack("oLdUdnk5za8"));
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
