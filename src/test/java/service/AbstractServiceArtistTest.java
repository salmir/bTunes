package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import at.ac.tuwien.sepm.musicplayer.service.impl.AlbumServiceImpl;
import at.ac.tuwien.sepm.musicplayer.service.impl.ArtistServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Alexandra on 26.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractServiceArtistTest  extends JdbcBaseDao {
    @InjectMocks
    protected ArtistServiceImpl artistService;

    protected ArtistDTO invalidArtist2;
    protected ArtistDTO invalidArtist;
    protected ArtistDTO validArtist;

    @Mock
    ArtistDAO artistDAO;




    public AbstractServiceArtistTest() throws PersistenceException {
        MockitoAnnotations.initMocks(this); //This is a key
    }
    /**
     * Create album with invalid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void persistTestWithInvalidAlbum_ShouldThrowException() throws ValidationException, ServiceException {
        artistService.persist(invalidArtist);
    }

    /**
     * Create artist with invalid parameters, should throw Persistence
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void persistTestWithInValidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.persist(invalidArtist2);
        verify(artistDAO).persist(invalidArtist2);
    }


    /**
     * Create artist with valid parameters, persist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void persistTestWithValidParameters() throws ValidationException, ServiceException, PersistenceException {
        artistService.persist(validArtist);
        verify(artistDAO).persist(validArtist);
        verify(artistDAO, times(1)).persist(validArtist);
    }


    /**
     * update Image with
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void updateValidSongCoverShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        artistService.persistImage(1, new File("test"));
        verify(artistDAO, times(1)).persistImage(1, new File("test"));
    }

    /**
     * update an validartist with an invalid Image (null)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidartistCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.persistImage(1, null);
        verify(artistDAO, times(1)).persistImage(1, null);

    }

    /**
     * update an invalid artist (-1 id)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidIdartistCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.persistImage(-1, new File("test"));
        verify(artistDAO, times(1)).persistImage(-1, new File("test"));

    }

    /**
     * update an validartist id with an invalid Image
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void updateWithInvalidartistCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.persistImage(1, new File("fail"));
        verify(artistDAO, times(1)).persistImage(1, new File("fail"));

    }

    /**
     * read an valid artist
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test
    public void readValidartistShouldPersist() throws ValidationException, ServiceException {

        ArtistDTO readartist = artistService.read(3);
        assertEquals("DaoArtistTest", readartist.getName());
        assertEquals("-1", readartist.getId().toString()); //not set really, id-> -1
    }


    /**
     * read an artist with invalid id
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void readInvalidartistShouldThrowException() throws ValidationException, ServiceException {
        try{
            artistService.read(-1);
        }catch(ServiceException s){
            Assert.fail();
        }
    }

    /**
     * read all artists
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void readAllShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        List<ArtistDTO> list = artistService.readAll();
        assertEquals(list.size(), 1);
        verify(artistDAO, times(1)).readAll();

    }

    /**
     * remove an valid artist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void removeValidartistShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        artistService.remove(2);
        verify(artistDAO, times(1)).remove(2);
    }

    /**
     * remove an not existing artist id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ValidationException.class)
    public void removeInvalidartistShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.remove(-1);
        verify(artistDAO, times(1)).remove(-1);

    }

    /**
     * remove an not existing artist id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ServiceException.class)
    public void removeInvalidNotExistingartistShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        artistService.remove(99999);
        verify(artistDAO, times(1)).remove(99999);

    }

    /**
     * find artist by Name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void findInValidartistNameShouldPersist() throws ValidationException, ServiceException {

        List<ArtistDTO> readNames = artistService.findByName("nicht vorhanden");

    }


    /**
     * find artist by invalid Name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void findInvalidartistNameShouldThrowException() throws ValidationException, ServiceException {
        artistService.findByName(null);
    }
}
