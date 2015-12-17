package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;

import java.io.File;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface AlbumService extends Service<AlbumDTO> {

    /**
     * persists an image to a song
     *
     * @param id of album which gets updated
     * @param file of image
     * @throws ValidationException if parameter(s) invalid
     * @throws ServiceException if persistence not available/fails
     */
    public void persistImage(int id, File file) throws ValidationException, ServiceException;

    /**
     * reads an album
     * @param name of album
     * @param artistId id of artist of album
     * @throws ValidationException if parameter(s) invalid
     * @throws ServiceException if persistence not available/fails
     */
    public AlbumDTO read(String name, int artistId) throws ValidationException, ServiceException;

    /**
     * finds albums which have part of given name as a name
     *
     * @param name name which the albums must contain
     * @return list of albums which contain substring
     * @throws ValidationException
     */
    List<AlbumDTO> findByName(String name) throws ValidationException, ServiceException;

}
