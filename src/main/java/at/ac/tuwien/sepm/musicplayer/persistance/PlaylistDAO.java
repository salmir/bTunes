package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;

import java.util.List;

/**
 * Additional interface to autocomplete the basic CRUD-methods for the entity Playlist.
 *
 * Created by Marjaneh.
 */
public interface PlaylistDAO extends DAO<PlaylistDTO> {

    /**
     * try to read an playlist-entity by its name from the persistence
     *
     * @param name of the playlist which should be read from the persistence
     * @return a list with DTO-objects of playlist
     * @throws PersistenceException if the persistence is not available
     */
    List<PlaylistDTO> readByName(String name) throws PersistenceException;

    /**
     * try to get all songs from a specified playlist with the given unique ID.
     *
     * @param id of the entity which should get read from the persistence
     * @return a list of all songs from this playlist ordered by priority
     * @throws PersistenceException if the persistence is not available
     */
    List<SongDTO> getSongs(int id) throws PersistenceException;

    /**
     * Deletes all songs with from playlist with the given ID
     *
     * @param id of the playlist from which all songs should be deleted
     * @throws PersistenceException if there is no playlist with this ID or the persistence is not available
     */
    void deleteSongs(int id) throws PersistenceException;

    /**
     * try to add a list of songs into an existing playlist.
     *
     * @param playlistId in which the songs are going to be added. (must not be null!)
     * @param toInsert list of songs. (should be valid songs)
     * @throws PersistenceException if the persistence is not available
     */
    void insertSongs(int playlistId, List<SongDTO> toInsert) throws PersistenceException;

    /**
     * @return all songs which are in any playlist
     */
    List<SongDTO> getAllPlaylistSongs() throws PersistenceException;

    /**
     * Deletes all songs with the given ID in any playlist
     *
     * @param id of the song which should be deleted
     * @throws PersistenceException if the persistence is not available
     */
    void deleteSongInPlaylistSong(int id) throws PersistenceException;

    /**
     * Updates the position of songs in playlist with the given ID
     *
     * @param playlistId of the playlist in which position of songs should be updated
     * @param songs in playlist which should be updated
     * @throws PersistenceException if the persistence is not available
     */
    void updateSongsInPlaylist(int playlistId, List<SongDTO> songs) throws PersistenceException;
}
