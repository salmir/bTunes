package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger logger = Logger.getLogger(ArtistServiceImpl.class);

    @Autowired
    private ArtistDAO artistDAO;

    @Override
    public void persistImage(int id, File file) throws ValidationException, ServiceException {
        if(id < 0) {
            logger.error("invalid id!");
            throw new ValidationException("invalid id!");
        }
        if(file == null) {
            logger.error("file null!");
            throw new ValidationException("file null!");
        }
        try {
            artistDAO.persistImage(id, file);
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persistBiography(int id, String biography) throws ValidationException, ServiceException {
        if(id < 0) {
            logger.error("invalid id!");
            throw new ValidationException("invalid id!");
        }
        if(biography == null) {
            logger.error("biography null!");
            throw new ValidationException("biography null!");
        }
        try {
            artistDAO.persistBiography(id, biography);
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public ArtistDTO read(String name) throws ValidationException, ServiceException {
        validateArtistName(name);

        try {
            return artistDAO.read(name);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<ArtistDTO> findByName(String name) throws ValidationException, ServiceException {
        if(name == null) {
            logger.error("invalid name");
            throw new ValidationException("invalid name");
        }
        try {
            return artistDAO.findByName(name);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persist(ArtistDTO toPersist) throws ValidationException, ServiceException {
        validateArtist(toPersist);

        try {
            artistDAO.persist(toPersist);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public ArtistDTO read(int id) throws ValidationException, ServiceException {
        validateArtistId(id);

        try {
            return artistDAO.read(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<ArtistDTO> readAll() throws ServiceException {
        try {
            return artistDAO.readAll();
        }  catch (PersistenceException e) {
            logger.error(e.getMessage()); //todo: ohne db
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void remove(int id) throws ValidationException, ServiceException {
        validateArtistId(id);

        try {
            artistDAO.remove(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void update(ArtistDTO toUpdate) throws ValidationException, ServiceException {
        validateArtistId(toUpdate.getId());
        validateArtist(toUpdate);

        try {
            artistDAO.update(toUpdate);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    // *** validates id of artist ***
    private void validateArtistId(int id) throws ValidationException {
        if(id < 0) {
            logger.error("invalid artist id! ("+ id +")");
            throw new ValidationException("invalid artist id! ("+ id +")");
        }
    }

    // *** validates name for extra-read(String) method ***
    private void validateArtistName(String name) throws ValidationException {
        if(name == null || name.trim().equals("")) {
            logger.error("invalid artist name! ("+ name +")");
            throw new ValidationException("invalid artist name! ("+ name +")");
        }
    }

    // *** validates the artist as object ***
    private void validateArtist(ArtistDTO artist) throws ValidationException {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Invalid Artist parameters:\n[\n");
        boolean valid = true;

        if(artist == null) throw new ValidationException("Artist is null!");

        if(artist.getId() != -1 && artist.getId() < 0) {
            errorMsg.append("id ("+ artist.getId() + ")\n");
            valid = false;
        }
        if(artist.getName() == null || artist.getName().trim().equals("")) {
            errorMsg.append("name\n");
            valid = false;
        }
        errorMsg.append("\n]");

        if(!valid) {
            String error = errorMsg.toString();
            logger.error(error);
            throw new ValidationException(error);
        }
    }
}
