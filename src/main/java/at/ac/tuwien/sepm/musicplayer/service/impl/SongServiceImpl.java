package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.DaoEntity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.persistance.DAO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.retriever.InfoRetriever;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lena Lenz.
 */
@Service
public class SongServiceImpl implements SongService {

    private static final Logger logger = Logger.getLogger(SongServiceImpl.class);

    @Autowired
    private SongDAO songDAO;


    @Override
    public void persist(SongDTO toPersist) throws ValidationException, ServiceException {
        validateSong(toPersist);
        try {
            songDAO.persist(toPersist);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persistRating(int id, int rating) throws ValidationException, ServiceException {
        validateSongId(id);
        if(rating < 1 || rating > 5) {
            throw new ValidationException("invalid rating! (should be between 1 and 5 but is "+ rating +")");
        }

        try {
            songDAO.persistRating(id, rating);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persistLyrics(int id, String lyrics) throws ValidationException, ServiceException {
        validateSongId(id);
        if(lyrics == null) {
            throw new ValidationException("lyrics are null");
        }

        try {
            songDAO.persistLyrics(id, lyrics);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<SongDTO> findByName(String name) throws ValidationException, ServiceException {
        if(name == null) {
            logger.error("invalid name");
            throw new ValidationException("invalid name");
        }
        try {
            return songDAO.findByName(name);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public SongDTO read(int id) throws ValidationException, ServiceException {
        validateSongId(id);
       try {
           return songDAO.read(id);
       }  catch (PersistenceException e) {
           logger.error(e.getMessage());
           throw new ServiceException(e.getMessage());
       }
    }


    @Override
    public List<SongDTO> readAll() throws ServiceException {
        try {
            return songDAO.readAll();
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void remove(int id) throws ValidationException, ServiceException {
        validateSongId(id);
        try {
            songDAO.remove(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void update(SongDTO toUpdate) throws ValidationException, ServiceException {
        validateSongId(toUpdate.getId());
        validateSong(toUpdate);
        try {
            songDAO.update(toUpdate);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }


    // *** validates id of song ***
    private void validateSongId(int id) throws ValidationException {
        if(id < 0) {
            logger.error("invalid song id! ("+ id +")");
            throw new ValidationException("invalid song id! ("+ id +")");
        }
    }

    // *** validates song ***
    private void validateSong(SongDTO song) throws ValidationException {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Invalid Song parameters:\n[\n");
        boolean valid = true;

        if(song == null) {
            throw new ValidationException("Song is null!");
        }
        if(song.getId() != -1 && song.getId() < 0) {
            errorMsg.append("id ("+ song.getId() +")\n");
            valid = false;
        }
        if(song.getName() == null || song.getName().trim().equals("")) {
            errorMsg.append("name\n");
            valid = false;
        }
        if(song.getPath() == null || song.getPath().trim().equals("")) {
            errorMsg.append("path\n");
            valid = false;
        }
        if(song.getLength() < 0) {
            errorMsg.append("length ("+ song.getLength() +")\n");
            valid = false;
        }
        if(song.getRating() != -1 && (song.getRating() < 1 || song.getRating() > 5)) {
            errorMsg.append("rating ("+ song.getRating() +")\n");
            valid = false;
        }
        if((song.getArtistId() == -1 && song.getArtistName() == null)) {
            errorMsg.append("artist\n");
            valid = false;
        }
        if((song.getAlbumId() == -1 && song.getAlbum() == null)) {
            errorMsg.append("album\n");
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
