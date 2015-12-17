package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
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
public class AlbumServiceImpl implements AlbumService {

    private static final Logger logger = Logger.getLogger(AlbumServiceImpl.class);

    @Autowired
    private AlbumDAO albumDAO;

    @Autowired
    private ArtistService artistService;

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
            albumDAO.persistImage(id, file);
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public AlbumDTO read(String name, int artistId) throws ValidationException, ServiceException {
        if(name == null){
            throw new ValidationException("Name is null");
        }else if(artistId == -1){
            throw new ValidationException("Not registered Album");
        }

        try {
            return albumDAO.read(name, artistId);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<AlbumDTO> findByName(String name) throws ValidationException, ServiceException {
        if(name == null) {
            logger.error("invalid name");
            throw new ValidationException("invalid name");
        }
        try {

            List<AlbumDTO> albums = albumDAO.findByName(name);

            for(AlbumDTO a : albums){
                a.setArtistName(artistService.read(a.getArtistId()).getName());
            }

            return albums;
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void persist(AlbumDTO toPersist) throws ValidationException, ServiceException {
        validateAlbum(toPersist);
        try {
            albumDAO.persist(toPersist);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }


    @Override
    public AlbumDTO read(int id) throws ValidationException, ServiceException {
        validateAlbumId(id);
        try {
            return albumDAO.read(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<AlbumDTO> readAll() throws ServiceException {
        try {
            return albumDAO.readAll();
        }  catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void remove(int id) throws ValidationException, ServiceException {
        validateAlbumId(id);
        try {
            albumDAO.remove(id);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void update(AlbumDTO toUpdate) throws ValidationException, ServiceException {
        validateAlbum(toUpdate);
        try {
            albumDAO.update(toUpdate);
        } catch (PersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    private void validateAlbumId(int id) throws ValidationException {
        if(id < 0) {
            logger.error("invalid album id! ("+ id +")");
            throw new ValidationException("invalid album id! ("+ id +")");
        }
        if(id == -1){
            throw new ValidationException("Not registered Album");
        }
    }

    private void validateAlbum(AlbumDTO album) throws ValidationException{
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Invalid Album parameters:\n[\n");
        boolean valid = true;

        if(album == null){
            throw new ValidationException("Album is null!");
        }
        if(album.getId() != -1 && album.getId() < 0) {
            errorMsg.append("id ("+ album.getId() +")\n");
            valid = false;
        }
        if(album.getName() == null || album.getName().trim().equals("")) {
            errorMsg.append("name\n");
            valid = false;
        }
        if((album.getArtistId() == -1 && album.getArtistName() == null)) {
            errorMsg.append("artist\n");
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
