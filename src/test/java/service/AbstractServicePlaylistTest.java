package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.persistance.PlaylistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import at.ac.tuwien.sepm.musicplayer.service.impl.AlbumServiceImpl;
import at.ac.tuwien.sepm.musicplayer.service.impl.PlaylistServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by marjaneh.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractServicePlaylistTest  extends JdbcBaseDao {

    @InjectMocks
    protected PlaylistServiceImpl playlistService;

    protected PlaylistDTO validPlaylist;
    protected PlaylistDTO invalidPlaylist;
    protected PlaylistDTO invalidPlaylist2;

    @Mock
    PlaylistDAO playlistDAO;




    public AbstractServicePlaylistTest() throws PersistenceException {
        MockitoAnnotations.initMocks(this); //This is a key
    }

    /**
     * Create playlist with invalid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void persistTestWithInvalidPlaylist_ShouldThrowException() throws ValidationException, ServiceException {
        playlistService.persist(invalidPlaylist);
    }

    /**
     * Create playlist with invalid parameters, should throw Persistence
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void persistTestWithInValidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        playlistService.persist(invalidPlaylist2);
        verify(playlistDAO).persist(invalidPlaylist2);
    }


    /**
     * Create playlist with valid parameters, persist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void persistTestWithValidParameters() throws ValidationException, ServiceException, PersistenceException {
        playlistService.persist(validPlaylist);
        verify(playlistDAO).persist(validPlaylist);
        verify(playlistDAO, times(1)).persist(validPlaylist);
    }

    /**
     * read an valid playlist
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test
    public void readValidplaylistShouldPersist() throws ValidationException, ServiceException {

        PlaylistDTO readplaylist = playlistService.read(3);
        assertEquals("ServicePlaylistTest", readplaylist.getName());
        assertEquals("-1", readplaylist.getId().toString()); //not set really, id-> -1
    }


    /**
     * read an playlist with invalid id
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void readInvalidplaylistShouldThrowException() throws ValidationException, ServiceException {
        try{
            playlistService.read(-1);
        }catch(ServiceException s){
            Assert.fail();
        }
    }

    /**
     * read all playlists
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void readAllShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        List<PlaylistDTO> list = playlistService.readAll();
        assertEquals(list.size(), 1);
        verify(playlistDAO, times(1)).readAll();

    }



    /**
     * find an Playlist by an not valid name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void findPlaylistByNotValidName_shouldReturnEmptyList() throws ValidationException, ServiceException {
        List<PlaylistDTO> list =  playlistService.readByName("thisNameDoesNotYetExist");
        assertEquals(list, Collections.emptyList());
    }


    /**
     * remove an valid playlist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void removeValidplaylistShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        playlistService.remove(2);
        verify(playlistDAO, times(1)).remove(2);
    }

    /**
     * remove an not existing playlist id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ValidationException.class)
    public void removeInvalidplaylistShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        playlistService.remove(-1);
        verify(playlistDAO, times(1)).remove(-1);

    }

    /**
     * remove an not existing playlist id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ServiceException.class)
    public void removeInvalidNotExistingplaylistShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        playlistService.remove(99999);
        verify(playlistDAO, times(1)).remove(99999);

    }

    /**
     * update an valid Playlist by its name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void updateValidPlaylist_shouldpersist() throws ValidationException, ServiceException, PersistenceException {
        validPlaylist.setId(4);
        playlistService.update(validPlaylist);
        verify(playlistDAO, times(1)).update(validPlaylist);

    }

    /**
     * update an valid Playlist by invalidName
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateValidPlaylistWithInvalidParam_shouldThrowException() throws ValidationException, ServiceException, PersistenceException {

        playlistService.update(invalidPlaylist);
        verify(playlistDAO, times(1)).update(invalidPlaylist);

    }

    /**
     * get all songs from valid playlist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void getSongsFromValidPlaylist_shouldReturnEmptyList() throws ValidationException, ServiceException, PersistenceException {

        List<SongDTO> list = playlistService.getSongs(4);
        assertTrue(list.size() == 0);
        verify(playlistDAO, times(1)).getSongs(4);

    }

    /**
     * get all songs from valid playlists
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void getAllSongsFromValidPlaylist_shouldReturnAllSongs() throws ValidationException, ServiceException {

        List<SongDTO> list = playlistService.getAllSongs();
        assertTrue(list.size() > 0);
    }
}
