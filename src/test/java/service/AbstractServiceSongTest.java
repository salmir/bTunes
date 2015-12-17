package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.impl.SongServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Lena Lenz.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractServiceSongTest extends JdbcBaseDao {

    @InjectMocks
    protected SongServiceImpl songService;

    protected SongDTO invalidSong2;
    protected SongDTO invalidSong;
    protected SongDTO validSong;

    @Mock
    SongDAO songDAO;




    public AbstractServiceSongTest() throws PersistenceException {
        MockitoAnnotations.initMocks(this); //This is a key
    }
   /**
     * Create album with invalid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void persistTestWithInvalidAlbum_ShouldThrowException() throws ValidationException, ServiceException {
        songService.persist(invalidSong);
    }

    /**
     * Create album with invalid parameters, should throw Persistence
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void persistTestWithInValidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.persist(invalidSong2);
        verify(songDAO).persist(invalidSong2);
    }


    /**
     * Create album with valid parameters, persist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void persistTestWithValidParameters() throws ValidationException, ServiceException, PersistenceException {
        songService.persist(validSong);
        verify(songDAO).persist(validSong);
        verify(songDAO, times(1)).persist(validSong);
    }




    /**
     * update Image with
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void updateValidSongLyricsShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        songService.persistLyrics(1, "new lyrics");
        verify(songDAO, times(1)).persistLyrics(1, "new lyrics");
    }

    /**
     * update an validAlbum with an invalid Image (null)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidSongLyricsShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
       songService.persistLyrics(1, null);
        verify(songDAO, times(1)).persistLyrics(1, null);

    }

    /**
     * update an invalid Album (-1 id)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidIdSongRatgingShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.persistRating(1, 6);
        verify(songDAO, times(1)).persistRating(1, 6);

    }

    /**
     * update Song with valid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void updateValidSongRatingShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        songService.persistRating(1, 4);
        verify(songDAO, times(1)).persistRating(1, 4);
    }


    /**
     * update song with rating (-1 id)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateRatingWithInvalidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.persistRating(-1, 4);
        verify(songDAO, times(1)).persistRating(-1, 4);

    }

    /**
     * update an validAlbum id with an invalid Image
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void updateWithInvalidAlbumCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.persistLyrics(1, "fail");
        verify(songDAO, times(1)).persistLyrics(1, "fail");

    }

     /**
     * read an valid album
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test
    public void readValidAlbumShouldPersist() throws ValidationException, ServiceException {

        SongDTO readAlbum = songService.read(3);
        assertEquals("ServiceSongTest", readAlbum.getName());
        assertEquals("-1", readAlbum.getId().toString()); //not set really, id-> -1
    }


    /**
     * read an album with invalid id
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void readInvalidAlbumShouldThrowException() throws ValidationException, ServiceException {
        try{
            songService.read(-1);
        }catch(ServiceException s){
            Assert.fail();
        }
    }

    /**
     * read all albums
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void readAll_ShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        List<SongDTO> list = songService.readAll();
        assertEquals(list.size(), 1);
        verify(songDAO, times(1)).readAll();

    }

    /**
     * remove an valid album
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void removeValidAlbumShouldPersist() throws ValidationException, ServiceException, PersistenceException {
       songService.remove(2);
        verify(songDAO, times(1)).remove(2);
    }

    /**
     * remove an not existing album id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ValidationException.class)
    public void removeInvalidAlbumShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.remove(-1);
        verify(songDAO, times(1)).remove(-1);

    }

    /**
     * remove an not existing album id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ServiceException.class)
    public void removeInvalidNotExistingAlbumShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        songService.remove(99999);
        verify(songDAO, times(1)).remove(99999);

    }


    /**
     * read song with invalid id
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void readInvalidSongShouldThrowException() throws ValidationException, ServiceException {
        try{
            songService.read(-1);
        }catch(ServiceException s){
            Assert.fail();
        }
    }

    /**
     * fing a valid Song by name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void findValidSongNameShouldPersist() throws ValidationException, ServiceException {

        List<SongDTO> readNames = songService.findByName("test name");
        assertEquals(readNames.size(), 1);
        assertEquals(readNames.get(0).getId().toString(),"-1");

    }

    /**
     * find a invalid song should throw exception
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void findInvalidSongNameShouldThrowException() throws ValidationException, ServiceException {
        songService.findByName(null);
    }


    /**
     * remove an invalid song
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void removeInvalidSongShouldThrowException() throws ValidationException, ServiceException {
        songService.remove(-1);
    }



}
