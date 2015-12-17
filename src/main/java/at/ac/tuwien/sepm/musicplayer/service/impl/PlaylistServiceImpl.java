package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.PlaylistDAO;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Lena Lenz.
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = Logger.getLogger(PlaylistServiceImpl.class);

    @Autowired
    private PlaylistDAO playlistDAO;

    @Override
    public List<PlaylistDTO> readByName(String name) throws ValidationException, ServiceException {
        if(name == null) {
            throw new ValidationException("name is null");
        }
        try {
            return playlistDAO.readByName(name);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<SongDTO> getSongs(int id) throws ValidationException, ServiceException {
        validatePlaylistId(id);
        try {
            return playlistDAO.getSongs(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<SongDTO> getAllSongs() throws ServiceException {
        try {
            return playlistDAO.getAllPlaylistSongs();
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void insertSongs(int playlistId, List<SongDTO> toInsert) throws ValidationException, ServiceException {
        validatePlaylistId(playlistId);
        try {
            playlistDAO.insertSongs(playlistId, toInsert);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteSongFromPlaylist(int songId) throws ServiceException {
        try {
            playlistDAO.deleteSongInPlaylistSong(songId);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persist(PlaylistDTO toPersist) throws ValidationException, ServiceException {
        validatePlaylist(toPersist);
        try {
            playlistDAO.persist(toPersist);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public PlaylistDTO read(int id) throws ValidationException, ServiceException {
        validatePlaylistId(id);
        try {
            return playlistDAO.read(id);
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<PlaylistDTO> readAll() throws ServiceException {
        try {
            return playlistDAO.readAll();
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void remove(int id) throws ValidationException, ServiceException {
        validatePlaylistId(id);
        try {
            playlistDAO.remove(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void update(PlaylistDTO toUpdate) throws ValidationException, ServiceException {
        validatePlaylistId(toUpdate.getId());
        validatePlaylist(toUpdate);
        try {
            playlistDAO.update(toUpdate);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void updateSongsInPlaylist(int playlistId, List<SongDTO> songs) throws ValidationException, ServiceException {
        validatePlaylistId(playlistId);
        try {
            playlistDAO.updateSongsInPlaylist(playlistId, songs);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    // *** validates id of playlist ***
    private void validatePlaylistId(int id) throws ValidationException {
        if(id < 0) {
            logger.error("invalid playlist id! ("+ id +")");
            throw new ValidationException("invalid playlist id! ("+ id +")");
        }
    }

    // *** validates playlist ***
    private void validatePlaylist(PlaylistDTO playlist) throws ValidationException {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Invalid Playlist parameters:\n[\n");
        boolean valid = true;

        if(playlist == null) {
            throw new ValidationException("Playlist is null!");
        }
        if(playlist.getId() != -1 && playlist.getId() < 0) {
            errorMsg.append("id ("+ playlist.getId() +")\n");
            valid = false;
        }
        if(playlist.getName() == null || playlist.getName().trim().equals("")) {
            errorMsg.append("name\n");
            valid = false;
        }
        errorMsg.append("\n]");

        // check if error
        if(!valid) {
            String error = errorMsg.toString();
            logger.error(error);
            throw new ValidationException(error);
        }
    }
}
