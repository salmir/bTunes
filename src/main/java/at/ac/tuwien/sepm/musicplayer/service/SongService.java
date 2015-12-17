package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;

import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface SongService extends Service<SongDTO> {
    /**
     * persists a rating to a song
     *
     * @param id of song which gets updated
     * @param rating of song
     * @throws ValidationException if parameter(s) invalid
     * @throws ServiceException if persistence not available/fails
     */
    public void persistRating(int id, int rating) throws ValidationException, ServiceException;

    /**
     * persists lyrics to a song
     *
     * @param id of song which gets updated
     * @param lyrics of song
     * @throws ValidationException if parameter(s) invalid
     * @throws ServiceException if persistence not available/fails
     */
    public void persistLyrics(int id, String lyrics) throws ValidationException, ServiceException;

    /**
     * finds songs which have part of given name as a name
     *
     * @param name name which the songs must contain
     * @return list of songs which contain substring
     * @throws ValidationException if parameter invalid
     * @throws ServiceException if persistence not available/fails
     */
    List<SongDTO> findByName(String name) throws ValidationException, ServiceException;
}
