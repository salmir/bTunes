package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import org.apache.log4j.Logger;

/**
 * Created by Lena Lenz.
 */
public class ArtistBioLastFMRetriever implements ArtistBioRetriever {

    private static Logger logger = Logger.getLogger(ArtistBioLastFMRetriever.class);

    // bTunes API key
    //private final String key = getLastFMKey(); //todo: retrieve key from user


    @Override
    public void retrieveInfo(Artist artist, ArtistService service) throws ServiceException {
        try {
            String biography = retrieveBiography(artist.getName());
            service.persistBiography(artist.getId(), biography);
            artist.setBiography(biography);
        } catch (ServiceException e) {
            logger.error("failed to load / persist artist biography");
            throw new ServiceException("failed to load / persist artist biography");
        } catch (ValidationException e) {
            logger.error("invalid artist biography");
            throw new ServiceException("invalid artist biography");
        }
    }

    private String retrieveBiography(String artistName) {
        de.umass.lastfm.Artist artist = de.umass.lastfm.Artist.getInfo(artistName, getLastFMKey());
        if(artist == null) {
            return "";
        }
        String biography = artist.getWikiText();
        String parsedBiography = HtmlParser.parseHtmlToText(biography);
        return parsedBiography;
    }


}
