package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.service.SongService;

/**
 * Created by marjaneh.
 */
public interface SongLyricsRetriever  {

    void retrieveInfo(Song song, SongService service) throws ServiceException;
}
