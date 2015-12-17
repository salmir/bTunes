package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;

import java.io.File;
import java.util.List;

/**
 * Additional interface to autocomplete the basic CRUD-methodes for the entity Album.
 *
 * Created by Lena Lenz.
 */
public interface AlbumDAO extends DAO<AlbumDTO>{

    /**
     * verifies the image file and try to add it to the album-entity with the given ID
     * todo: update JavaDoc after implementing method.
     *
     * @param id album-ID of the entity which should get updated with a image
     * @param file image-file which should be added to the album-entity
     * @throws PersistenceException  if the file is invalid, the entity does not exists or persistence is not available
     */
    void persistImage(int id, File file) throws PersistenceException;

    /**
     * try to read an album-entity by his name and an artist which is define with the artistID from the persistence
     *
     * @param name of album which should be read from the persistence
     * @param artistId of artist who recorded the album
     * @throws PersistenceException if persistence not available
     */
    AlbumDTO read(String name, int artistId) throws PersistenceException;

    /**
     * finds entity which has part of given name as a name
     *
     * @param name name which the entity must contain
     * @return list of objects A which contain substring
     * @throws PersistenceException
     */
    List<AlbumDTO> findByName(String name) throws PersistenceException;
}