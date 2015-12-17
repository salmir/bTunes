package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.impl.AlbumServiceImpl;
import org.apache.commons.lang.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Alexandra on 26.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractServiceAlbumTest  extends JdbcBaseDao {

   @InjectMocks
    protected AlbumServiceImpl albumService;

    protected AlbumDTO invalidAlbum2;
    protected AlbumDTO invalidAlbum;
    protected AlbumDTO validAlbum;

    @Mock
    AlbumDAO albumDAO;




    public AbstractServiceAlbumTest() throws PersistenceException {
        MockitoAnnotations.initMocks(this); //This is a key
    }

    /**
     * Create album with invalid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void persistTestWithInvalidAlbum_ShouldThrowException() throws ValidationException, ServiceException {
        albumService.persist(invalidAlbum);
    }

    /**
     * Create album with invalid parameters, should throw Persistence
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void persistTestWithInValidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        albumService.persist(invalidAlbum2);
        verify(albumDAO).persist(invalidAlbum2);
    }


    /**
     * Create album with valid parameters, persist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void persistTestWithValidParameters() throws ValidationException, ServiceException, PersistenceException {
        albumService.persist(validAlbum);
        verify(albumDAO).persist(validAlbum);
        verify(albumDAO, times(1)).persist(validAlbum);
    }


    /**
     * update Image with
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void updateValidSongCoverShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        albumService.persistImage(1, new File("test"));
        verify(albumDAO, times(1)).persistImage(1, new File("test"));
    }

    /**
     * update an validAlbum with an invalid Image (null)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidAlbumCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
       albumService.persistImage(1, null);
        verify(albumDAO, times(1)).persistImage(1, null);

    }

    /**
     * update an invalid Album (-1 id)
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void updateInvalidIdAlbumCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        albumService.persistImage(-1, new File("test"));
        verify(albumDAO, times(1)).persistImage(-1, new File("test"));

    }

    /**
     * update an validAlbum id with an invalid Image
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void updateWithInvalidAlbumCoverShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        albumService.persistImage(1, new File("fail"));
        verify(albumDAO, times(1)).persistImage(1, new File("fail"));

    }

    /**
     * read an valid album
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test
    public void readValidAlbumShouldPersist() throws ValidationException, ServiceException {

        AlbumDTO readAlbum = albumService.read(3);
        assertEquals("DaoAlbumTest", readAlbum.getName());
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
            albumService.read(-1);
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
    public void readAllShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        List<AlbumDTO> list = albumService.readAll();
        assertEquals(list.size(), 1);
        verify(albumDAO, times(1)).readAll();

    }

    /**
     * remove an valid album
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void removeValidAlbumShouldPersist() throws ValidationException, ServiceException, PersistenceException {
       albumService.remove(2);
        verify(albumDAO, times(1)).remove(2);
    }

    /**
     * remove an not existing album id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ValidationException.class)
    public void removeInvalidAlbumShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        albumService.remove(-1);
        verify(albumDAO, times(1)).remove(-1);

    }

    /**
     * remove an not existing album id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ServiceException.class)
    public void removeInvalidNotExistingAlbumShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        albumService.remove(99999);
        verify(albumDAO, times(1)).remove(99999);

    }

    /**
     * find Album by Name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void findInValidAlbumNameShouldPersist() throws ValidationException, ServiceException {

        List<AlbumDTO> readNames = albumService.findByName("nicht vorhanden");

    }


    /**
     * find Album by invalid Name
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void findInvalidAlbumNameShouldThrowException() throws ValidationException, ServiceException {
        albumService.findByName(null);
    }



}
