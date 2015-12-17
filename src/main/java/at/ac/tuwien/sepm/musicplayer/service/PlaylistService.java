package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;

import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface PlaylistService extends Service<PlaylistDTO> {

    /**
     * finds playlist which have part of given name as a name
     *
     * @param name name which the playlist must contain
     * @return list of playlists which contain substring
     * @throws ValidationException if parameter invalid
     * @throws ServiceException if persistence not available/fails
     */
    List<PlaylistDTO> readByName(String name) throws ValidationException, ServiceException;

    /**
     * try to get all songs from a specified playlist with the given unique ID.
     *
     * @param id of the entity which should get read from the persistence
     * @return a list of all songs from this playlist ordered by priority
     * @throws ValidationException if parameter invalid
     * @throws ServiceException if persistence not available/fails
     */
    List<SongDTO> getSongs(int id) throws ValidationException, ServiceException;

    /**
     * get all songs which are in any playlist
     *
     * @return all songs which are in any playlist
     * @throws ServiceException if persistence not available/fails
     */
    List<SongDTO> getAllSongs() throws ServiceException;

    /**
     * try to add a list of songs into an existing playlist.
     *
     * @param playlistId in which the songs are going to be added.
     * @param toInsert list of songs. (should be valid songs)
     * @throws ValidationException if parameter invalid
     * @throws ServiceException if persistence not available/fails
     */
    void insertSongs(int playlistId, List<SongDTO> toInsert) throws ValidationException, ServiceException;

    /**
     * Deletes all songs with the given ID in any playlist
     *
     * @param songId of the song which should be deleted
     * @throws ServiceException if persistence not available/fails
     */
    void deleteSongFromPlaylist(int songId) throws ServiceException;

    /**
     * Updates the position of songs in playlist with the given ID
     *
     * @param playlistId of the playlist in which position of songs should be updated
     * @param songs in playlist which should be updated
     * @throws ValidationException if parameter invalid
     * @throws ServiceException if persistence not available/fails
     */
    void updateSongsInPlaylist(int playlistId, List<SongDTO> songs) throws ValidationException, ServiceException;
}
