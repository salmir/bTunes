package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;

import de.umass.lastfm.Event;
import de.umass.lastfm.PaginatedResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alexandra on 03.06.2015.
 */
public class ConcertDataLastFMRetriever implements ConcertDataRetriever {
    private static Logger logger = Logger.getLogger(ArtistImageWikiRetriever.class);
    public String ausgabe;

    @Override
    public void retrieveInfo(Artist artist, ArtistService service) throws ServiceException {
        String concert = retrieveConcert(artist.getName());
        if (concert == null || concert.equals("")|| concert.isEmpty()) {
            ausgabe = "Currently no Concerts available";

        } else {
            ausgabe = concert;
        }
    }

    private String retrieveConcert(String artistName) {

        de.umass.lastfm.PaginatedResult events = de.umass.lastfm.Artist.getEvents(artistName, false, -1, -1, getLastFMKey());
        if(events == null) {
            return null;
        }
       String result ="";
        Collection<Event> event = events.getPageResults();

        for(Event p: event){
           result += "Date: "+ p.getStartDate()+ '\n'+"Title: "+ p.getTitle()+ '\n' + "Location: "+ p.getVenue().getCity()+ '\n'+ '\n';

        }
        return result;
    }

    public String getAusgabe(){
        return ausgabe;
    }

}
