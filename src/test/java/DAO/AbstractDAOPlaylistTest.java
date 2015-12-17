package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.persistance.PlaylistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcPlaylistDao;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcSongDao;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractDAOPlaylistTest extends JdbcBaseDao{


    @Autowired
    protected JdbcPlaylistDao playlistDAO;

    @Autowired
    protected JdbcSongDao songDAO;

    protected PlaylistDTO validPlaylist;
    protected PlaylistDTO invalidPlaylist;

    /**
     * Creates playlist with null.
     * @throws at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createPlaylistWithNull_throwsPersistenceException() throws PersistenceException {
        playlistDAO.persist(null);
    }


    /**
     * Creates new Playlist with too long name
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createPlaylistWithTooLongName_throwsPersistenceException() throws PersistenceException {
        PlaylistDTO playlist = new PlaylistDTO("tesaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat");
        playlistDAO.persist(playlist);
    }


    /**
     * Creates the playlist with valid parameters
     */
    @Test
    public void createPlaylistWithValidParameter_returnTestPlaylist() throws PersistenceException{
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);

        playlistDAO.persist(validPlaylist);
        assertEquals(validPlaylist.getId(), playlistDAO.readAll().get(playlistDAO.readAll().size() - 1).getId());
    }


    /**
     * create a playlist with invalid parameters
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createInvalidPlaylist_shouldThrowPersistenceException() throws PersistenceException {
        playlistDAO.persist(invalidPlaylist);
    }


    /**
     * Reads an existing playlist and compares with it's expected name
     */
    @Test
    public void readPlaylistByName_returnsValidPlaylist() throws PersistenceException {
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);
        List<PlaylistDTO> playlist;
        playlistDAO.persist(validPlaylist);
        playlist = playlistDAO.readByName("DaoPlaylistTest");

        assertEquals("DaoPlaylistTest", playlist.get(playlist.size() - 1).getName());
        assertEquals(playlistDAO.readAll().get(playlistDAO.readAll().size() - 1).getId(), validPlaylist.getId());
    }


    /**
     * Reads an existing playlist and compares with it's expected name
     */
    @Test
    public void readPlaylistByNotExistingName_shouldReturnEmptyList() throws PersistenceException {
        assertEquals(playlistDAO.readAll().contains(invalidPlaylist), false);
        List<PlaylistDTO> playlist;
        playlist = playlistDAO.readByName(StringUtils.leftPad("", 201, '*'));

        assertEquals(playlist, Collections.emptyList());
    }


    /**
     * Tries to read a non existing playlist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readNotExistingPlaylist_throwsPersistenceException() throws PersistenceException {
        if(playlistDAO.readAll().size()<9999998){
            playlistDAO.read(9999999);
        }else {
            playlistDAO.read(songDAO.readAll().size() + 2);
        }
    }


    /**
     * insert one playlist and read all playLists
     * return size + 1
     * @throws PersistenceException
     */
    @Test
    public void readAll_shouldReturnSizePlusOne() throws PersistenceException {
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);
        int db_size = playlistDAO.readAll().size();
        playlistDAO.persist(validPlaylist);
        assertEquals(playlistDAO.readAll().size(), db_size + 1);
        assertEquals(playlistDAO.readAll().get(playlistDAO.readAll().size() - 1).getId(), validPlaylist.getId());

    }


    /**
     * Tries to remove an valid playlist
     */
    @Test
    public void removeAnValidPlaylist_returnsSizeMinusOne() throws PersistenceException {
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);

        int db_size = playlistDAO.readAll().size();
        playlistDAO.persist(validPlaylist);
        assertEquals(playlistDAO.readAll().get(playlistDAO.readAll().size() - 1).getId(), validPlaylist.getId());

        assertEquals(playlistDAO.readAll().size(), db_size + 1);
        playlistDAO.remove(validPlaylist.getId());
        assertEquals(playlistDAO.readAll().size(), db_size);
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);
    }

    /**
     * Remove a not jet existing Playlist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotYetExistingPlaylist_returnsPersistenceException() throws PersistenceException{
        if(playlistDAO.readAll().size()<9999998){
            playlistDAO.remove(9999999);
        }else {
            playlistDAO.remove(songDAO.readAll().size() + 2);
        }
    }


    /**
     * Remove a non existing Playlist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotExistingPlaylist_returnsPersistenceException() throws PersistenceException{
        playlistDAO.remove(-1);
    }


    /**
     * Insert Songs to Playlist
     */
    @Test
    public void insertAllSongsToPlaylist_returnsListWithAllElements() throws PersistenceException{
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);

        List<SongDTO> list = new ArrayList<>();
        list.addAll(songDAO.readAll());

        playlistDAO.persist(validPlaylist);
        playlistDAO.insertSongs(validPlaylist.getId(), list);
        assertEquals(list.size(), playlistDAO.getSongs(validPlaylist.getId()).size());
    }


    /**
     * fetch all songs from valid playlist an controll their size
     * @throws PersistenceException
     */
    @Test
    public void getSongsFromValidPlaylist_shouldReturnAllSongsFromThisPlaylist() throws PersistenceException{
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);

        List<SongDTO> list = new ArrayList<>();
        list.addAll(songDAO.readAll());

        playlistDAO.persist(validPlaylist);
        playlistDAO.insertSongs(validPlaylist.getId(), list);

        assertEquals(playlistDAO.getSongs(validPlaylist.getId()).size(), list.size());
    }

    /**
     * fetch Songs from an not existing playlist id
     * @throws PersistenceException
     */
    @Test
    public void getSongsFromNotExistingPlaylist_shouldReturnEmptyList() throws PersistenceException{
        assertEquals(playlistDAO.getSongs(-1), Collections.emptyList());
    }


    /**
     * Update an valid Playlist by its name
     */
    @Test
    public void updateNameOfValidPlaylist_returnsUpdatedPlaylist() throws PersistenceException{
        assertEquals(playlistDAO.readAll().contains(validPlaylist), false);

        playlistDAO.persist(validPlaylist);
        validPlaylist.setName("UpdatedPlaylist");
        playlistDAO.update(validPlaylist);

        assertEquals(validPlaylist.getName(), "UpdatedPlaylist");
        assertEquals(playlistDAO.readAll().get(playlistDAO.readAll().size() - 1).getId(), validPlaylist.getId());
    }
}
