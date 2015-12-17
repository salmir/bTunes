package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;

import java.io.File;
import java.util.List;

/**
 * Additional interface to autocomplete the basic CRUD-methods for the entity Artist.
 *
 * Created by Lena Lenz.
 */
public interface ArtistDAO extends DAO<ArtistDTO> {

    /**
     * verifies the image file and try to add it to the artist-entity with the given ID.
     * The image-file must not be null or longer then 200!
     * todo: update JavaDoc after implementing method.
     *
     * @param id artist-ID of the entity which should get updated with a image
     * @param file image-file which should be added to the artist-entity (max length = 200 characters)
     * @throws PersistenceException if the file is invalid, the entity does not exists or persistence is not available
     */
    void persistImage(int id, File file) throws PersistenceException;

    /**
     * verifies the biography (text only) and try to add it to the artist-entity with the given ID
     *
     * @param id artist-ID of the entity which should get updated with the biography
     * @param biography as String which should be added to the artist-entity (max length = 8000 characters)
     * @throws PersistenceException if the biography is empty, to long or the persistence is not available
     */
    void persistBiography(int id, String biography) throws PersistenceException;

    /**
     * try to read an artist-entity by his name from the persistence
     * todo: check to see whether a artist-name is unique or not -> change the validation in the method.
     *
     * @param name of artist which should be read from the persistence
     * @return an entity-object of artist
     * @throws PersistenceException if there is no artist with this name or the persistence is not available
     */
    ArtistDTO read(String name) throws PersistenceException;

    /**
     * finds entity which has part of given name as a name
     *
     * @param name name which the entity must contain
     * @return list of artists which contain substring
     * @throws PersistenceException
     */
    List<ArtistDTO> findByName(String name) throws PersistenceException;
}
