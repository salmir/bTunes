package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import de.umass.lastfm.Album;
import de.umass.lastfm.ImageSize;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Created by Alexandra Mai.
 */
public class ArtistImageWikiRetriever implements ArtistImageRetriever {
    private static Logger logger = Logger.getLogger(ArtistImageWikiRetriever.class);
    private static final String DEFAULT_COVER = "/artistImage/Artist.png";

    @Override
    public void retrieveInfo(Artist artist, ArtistService service) throws ServiceException {
        String coverURL = retrieveAlbumCover(artist.getName());
        if (coverURL == null || coverURL.equals("")) {
            artist.setImage(DEFAULT_COVER);
        } else {
            String fileName = getFileName(coverURL);
            File file = new File("src/main/resources/artistImage/" + fileName);
            try {
                FileUtils.copyURLToFile(new URL(coverURL), file);
            } catch (IOException e) {
                logger.error("failed to save artist image");
            }
            try {
                service.persistImage(artist.getId(), file);
                artist.setImage(file.getPath());
            } catch (ServiceException e) {
                logger.error("failed to load / persist artist image");
                throw new ServiceException("failed to load / persist artist image");
            } catch (ValidationException e) {
                logger.error("invalid artist image");
                throw new ServiceException("invalid artist image");
            }
        }
    }

    private String retrieveAlbumCover(String artistName) {

        de.umass.lastfm.Artist artist = de.umass.lastfm.Artist.getInfo(artistName, getLastFMKey());
        if(artist == null) {
            return "";
        }
        String image = artist.getImageURL(ImageSize.LARGE);
        return image;
    }

    private String getFileName(String coverURL) {
        String[] parts = coverURL.split("/");
        return parts[parts.length - 1];
    }
}
