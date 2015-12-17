package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Youtube;
import at.ac.tuwien.sepm.musicplayer.service.YoutubeService;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;

/**
 * Created by Alexandra on 06.06.2015.
 */
@Service
public class YoutubeServiceImpl implements YoutubeService {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 20;

    private static YouTube youtube;
    static Thumbnail thumbnail;

    private List<String> thumbnailFinal = null;
    private List<String> sound = null;
    private List<Youtube> results = null;
    private Youtube youtub;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result sets
     */
    public void search(String input) {

        try {
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("sepm2015").build();

            // Prompt the user to enter a query term.
            if(input != "") {
                String queryTerm = input;

                // Define the API request for retrieving search results.
                YouTube.Search.List search = youtube.search().list("id,snippet");
                search.setKey("AIzaSyCmYkf9uDC9n8aEw3kKo3WUKp_7epBDaak");
                search.setQ(queryTerm);
                search.setType("video");
                //search only for embedded and syndicated videos --> no vevo, this videos only playable on youtube directly
                search.setVideoEmbeddable("true");
                search.setVideoSyndicated("true");
               // / search.setVideoLicense("false");
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    prettyPrint(searchResultList.iterator(), queryTerm);
                }
            }
        } catch (Exception e) {

           e.printStackTrace();
        }
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
       youtub = new Youtube();
        thumbnailFinal = new ArrayList<String>();
        results = new ArrayList<Youtube>();
        sound = new ArrayList<String>();
        if (!iteratorSearchResults.hasNext()) {
            results.add(youtub);
        }else {
            int count = 1;
            while (iteratorSearchResults.hasNext()) {
                Youtube helper = new Youtube();
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Confirm that the result represents a video. Otherwise, the
                // item will not contain a video ID.
                if (rId.getKind().equals("youtube#video")) {
                    helper.setNummer(count);
                    helper.setTitel(singleVideo.getSnippet().getTitle());
                    helper.setSongpath("https://www.youtube.com/v/" + rId.getVideoId());
                    thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                    helper.setImagepath(thumbnail.getUrl());

                    results.add(helper);
                    thumbnailFinal.add(thumbnail.getUrl());
                    sound.add("http://www.youtube.com/v/" + rId.getVideoId());
                    count++;
                }
            }
        }
    }

    @Override
    public List<String> getThumbnail() {
        return thumbnailFinal;
    }

    @Override
    public List<String> getSoundURLs() {
        return sound;
    }

    @Override
    public List<Youtube> getResults(){ return results;}

    @Override
    public void setActiveSongURL(String s) {

    }
}
