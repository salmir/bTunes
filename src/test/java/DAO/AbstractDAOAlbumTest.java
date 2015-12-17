package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.ArgumentNullException;
import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcAlbumDao;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexandra on 06.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractDAOAlbumTest extends JdbcBaseDao {

    @Autowired
    protected JdbcAlbumDao albumDAO;

    protected AlbumDTO validAlbum;
    protected AlbumDTO invalidAlbum;

    /**
     * Creates the album with null.
     * @throws at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createAlbumWithNull_throwsPersistenceException() throws PersistenceException{
        albumDAO.persist(null);
    }

    /**
     * Creates the album with too long name
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void createAlbumWithTooLongName_throwValidationException() throws PersistenceException{
        AlbumDTO album = new AlbumDTO("tesaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat");
        albumDAO.persist(album);
    }


    /**
     * Creates the album with valid parameters
     */
    @Test
    public void createAlbumWithValidParameters_returnsTestAlbum() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        albumDAO.persist(validAlbum);
        assertEquals(validAlbum.getName(), albumDAO.readAll().get(albumDAO.readAll().size() - 1).getName());
        assertEquals(validAlbum.getId(), albumDAO.readAll().get(albumDAO.readAll().size() - 1).getId());

    }

    /**
     * Creates the album with invalid parameters
     */
    @Test(expected = PersistenceException.class)
    public void createAlbumWithInValidParameters_shouldThrowPersistenceException() throws PersistenceException {

        albumDAO.persist(invalidAlbum);
    }


    /**
     * Reads an existing album and compares with it's expected name
     */
    @Test
    public void readAlbumByName_returnsValidAlbum() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        AlbumDTO album = null;
        albumDAO.persist(validAlbum);
        album = albumDAO.read("DaoAlbumTest", validAlbum.getArtistId());

        assertEquals("DaoAlbumTest", album.getName());
        assertEquals(album.getId(), validAlbum.getId());
    }


    /**
     * Tries to read a not jet existing Album
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readNotJetExistingAlbum_throwsPersistenceException() throws PersistenceException {
        if(albumDAO.readAll().size()<9999998){
            albumDAO.read(9999999);
        }else {
            albumDAO.read(albumDAO.readAll().size() + 2);
        }
    }


    /**
     * read album with certain id
     * @throws PersistenceException
     */
    @Test
    public void readValidAlbum_shouldReturnValidSong() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        albumDAO.persist(validAlbum);
        AlbumDTO readSong = albumDAO.read(validAlbum.getId());
        assertEquals(validAlbum.getName(), readSong.getName());
        assertEquals(validAlbum.getId(), readSong.getId());
    }


    /**
     * read album with certain name
     * @throws PersistenceException
     */
    @Test
    public void readValidAlbumByName_shouldReturnValidSong() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        albumDAO.persist(validAlbum);
        AlbumDTO readSong = albumDAO.read("DaoAlbumTest",validAlbum.getArtistId());
        assertEquals(validAlbum.getName(), readSong.getName());
        assertEquals(validAlbum.getId(), readSong.getId());
    }

    /**
     * read invalid album with certain name
     * @throws PersistenceException
     */
    @Test
    public void readAlbumByNameWithInvalidParamters_shouldReturnNull() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        albumDAO.persist(validAlbum);
        AlbumDTO readSong = albumDAO.read(StringUtils.leftPad("", 201, '*'),validAlbum.getId());
        assertEquals(null, readSong);

    }

    /**
     * Tries to read a non existing Album
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readNoNExistingAlbum_throwsPersistenceException() throws PersistenceException {
        albumDAO.read(-1);
    }


    /**
     * Tries to remove an valid Album
     */
    @Test
    public void removeAnValidAlbum_returnsSizeMinusOne() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        int db_size = albumDAO.readAll().size();
        albumDAO.persist(validAlbum);
        assertEquals(albumDAO.readAll().get(albumDAO.readAll().size() - 1).getId(), validAlbum.getId());
        assertEquals(albumDAO.readAll().size(), db_size + 1);
        albumDAO.remove(validAlbum.getId());
        assertEquals(albumDAO.readAll().size(), db_size);
        assertFalse(albumDAO.readAll().contains(validAlbum));
    }


    /**
     * Remove a non existing Album
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotExistingAlbum_returnsPersistenceException() throws PersistenceException{
        if(albumDAO.readAll().size()<9999998){
            albumDAO.remove(9999999);
        }else {
            albumDAO.remove(albumDAO.readAll().size() + 2);
        }
    }


    /**
     * update valid album by its Cover
     * @throws PersistenceException
     */
    @Test
    public void updateValidAlbum_shouldReturnCorrectCover() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);
        albumDAO.persist(validAlbum);
        validAlbum.setCover("New Cover");
        albumDAO.update(validAlbum);
        assertEquals("New Cover", validAlbum.getCover());
        assertEquals(validAlbum.getId(), albumDAO.read(validAlbum.getName(), validAlbum.getArtistId()).getId());
    }


    /**
     * update a validSong with invalid Cover
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateWithInvalidCover_shouldThrowPersitsenceException() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);

        try {
            // should persist
            albumDAO.persist(validAlbum);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        validAlbum.setCover(StringUtils.leftPad("", 301, '*'));
        albumDAO.update(validAlbum);
    }

    /**
     * update a validSong with null
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateWithNull_shouldThrowPersitsenceException() throws PersistenceException {
        assertEquals(albumDAO.readAll().contains(validAlbum), false);

        try {
            // should persist
            albumDAO.persist(validAlbum);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        albumDAO.update(null);
    }
}
