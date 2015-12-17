package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Created by marjaneh.
 */
public class AlbumCoverLastFMRetriever implements AlbumCoverRetriever {
    private static Logger logger = Logger.getLogger(AlbumCoverLastFMRetriever.class);
    private static final String DEFAULT_COVER = "/albumCover/Album.jpg";

    @Override
    public void retrieveInfo(Album album, AlbumService service) throws ServiceException {
        String coverURL = retrieveAlbumCover(album.getArtistName(), album.getName());

        if (coverURL == null || coverURL.equals("")) {
            album.setCover(DEFAULT_COVER);
        } else {
            String fileName = getFileName(coverURL);
            File file = new File("src/main/resources/albumCover/" + fileName);
            try {
                //Copies bytes from the URL source(coverURL) to a file.
                FileUtils.copyURLToFile(new URL(coverURL),file);
            } catch (IOException e) {
                logger.error("failed to save album cover");
            }
            try {
                service.persistImage(album.getId(), file);
                album.setCover(file.getPath());
            } catch (ServiceException e) {
                logger.error("failed to load / persist album cover");
                throw new ServiceException("failed to load / persist album cover");
            } catch (ValidationException e) {
                logger.error("invalid album cover");
                throw new ServiceException("invalid album cover");
            }
        }
    }

    private String retrieveAlbumCover(String artistName, String albumName) {
        //Set the log level specifying which message levels will be logged by this logger.
        // The level value Level.OFF can be used to turn off logging.
        de.umass.lastfm.Caller.getInstance().getLogger().setLevel(Level.OFF);
        de.umass.lastfm.Album album = de.umass.lastfm.Album.getInfo(artistName, albumName, getLastFMKey());
        de.umass.lastfm.Caller.getInstance().getLogger().setLevel(Level.ALL);

        if (album == null) {
            return "";
        }
        return album.getImageURL(ImageSize.LARGE);
    }

    private String getFileName(String coverURL) {
        String[] parts = coverURL.split("/");
        return parts[parts.length - 1];
    }
}
