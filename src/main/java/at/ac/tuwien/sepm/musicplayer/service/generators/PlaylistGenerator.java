package at.ac.tuwien.sepm.musicplayer.service.generators;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

/**
 * Created by Lena Lenz.
 */
@Service
public interface PlaylistGenerator<A extends Entity> {

    /**
     * generate new playlist out of seed
     *
     * @param entity entity of which playlist should get generated
     * @return new playlist
     */
    Playlist generatePlaylist(A entity) throws ValidationException, ServiceException, FileNotFoundException;

    /**
     * get API key of bTunes-user on LastFM
     * @return API key of LastFM
     */
    default String getLastFMKey() {
        return "6107f246b80d4d0b9605077fb8763c7a"; //todo: retrieve key from user
    }

}
