package at.ac.tuwien.sepm.musicplayer.service;


import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Youtube;

import java.util.List;

/**
 * Created by Alexandra on 06.06.2015.
 */
public interface YoutubeService{

   /// gives the url of the thumbnail photo back
    List<String> getThumbnail();

    //gives the complete Youtube Url of the selected Video back
   List<String> getSoundURLs();

    void search(String input);

    List<Youtube> getResults();

    void setActiveSongURL(String s);




}
