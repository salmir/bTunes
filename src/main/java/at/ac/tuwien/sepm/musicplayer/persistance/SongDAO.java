package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;

import java.util.List;

/**
 * Additional interface to autocomplete the basic CRUD-methods for the entity Song.
 *
 * Created by Lena Lenz.
 */
public interface SongDAO extends DAO<SongDTO> {

    /**
     * verifies the rating (Integer only) and try to add it to the song-entity with the given ID
     * The default-value for rating is -1.
     * todo: please check how useful it is to set -1 as default value for Song-rating.
     * todo: In other music players it is possible to rate with 1-5 "stars" or "without" (0) stars. That includes that the default rating is '0'
     * The rating should be 1 <= value <= 5.
     *
     * @param id song-ID of the entity which should get updated with a valid rating
     * @param rating an Integer for the rating of this song (valid values: 1,2,3,4,5)
     * @throws PersistenceException if the rating is invalid, the entity does not exists or the persistence is not available
     */
    void persistRating(int id, int rating) throws PersistenceException;

    /**
     * verifies the lyric (text only) and try to add it to the song-entity with the given ID
     * The lyric must not be null or longer then 8000 characters.
     *
     * @param id song-ID of the entity which should get updated with the lyric (not null)
     * @param lyrics as String which should be added to the song-entity (max length = 8000 characters)
     * @throws PersistenceException if the lyric is null, to long or the persistence is not available
     */
    void persistLyrics(int id, String lyrics) throws PersistenceException;

    /**
     * finds songs which have part of given name as a name
     *
     * @param name name which the song must contain
     * @return list of songs which contain substring
     * @throws PersistenceException
     */
    List<SongDTO> findByName(String name) throws PersistenceException;

}
