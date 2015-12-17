package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;

import java.io.File;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface ArtistService extends Service<ArtistDTO> {

    /**
     *
     * @param id
     * @param file
     * @throws
     */
    void persistImage(int id, File file) throws ValidationException, ServiceException;

    /**
     *
     *
     * @param id
     * @param biography
     * @throws
     */
    void persistBiography(int id, String biography) throws ValidationException, ServiceException;

    /**todo
     * reads artist by name
     *
     * @param name of artist
     * @return
     * @throws
     */
    ArtistDTO read(String name) throws ValidationException, ServiceException;

    /**
     * finds artists which have part of given name as a name
     *
     * @param name name which the artist must contain
     * @return list of albums which contain substring
     * @throws ValidationException
     */
    List<ArtistDTO> findByName(String name) throws ValidationException, ServiceException;

}
