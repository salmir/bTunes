package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcArtistDao;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcSongDao;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexandra on 06.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
@TransactionConfiguration(defaultRollback=true)

public abstract class AbstractDAOArtistTest extends JdbcBaseDao {


    @Autowired
    protected JdbcArtistDao artistDAO;

    protected ArtistDTO validArtist;
    protected ArtistDTO invalidArtist;

    /**
     * Creates artist with null.
     * @throws at.ac.tuwien.sepm.musicplayer.exceptions.ArgumentNullException
     */
    @Test (expected = PersistenceException.class)
    public void createArtistWithNull_throwsPersistenceException() throws PersistenceException {
        artistDAO.persist(null);
    }

    /**
     * Creates the artist with to long name
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void createArtistWithTooLongName_throwValidationException() throws PersistenceException {
        ArtistDTO artist = new ArtistDTO("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeestnameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        artist.setBiography("testBiography");
        artist.setImage("testImagePath!");
        artistDAO.persist(artist);
    }


    /**
     * Creates an artist with all valid parameters
     */
    @Test
    public void createArtistWithValidParameters_returnsValidArtist() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        int artistCount = artistDAO.readAll().size();
        artistDAO.persist(validArtist);
        // check if id,  name biography and image were set
        assertTrue(validArtist.getId() != -1 && validArtist.getName() != null && validArtist.getBiography() != null && validArtist.getImage() != null);
        // check if database size changed by 1 inserted row
        assertTrue(artistDAO.readAll().size() == artistCount + 1);
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());

    }

    /**
     * Creates an artist with only a valid name (other parameter are null)
     */
    @Test
    public void createArtistWithOneValidParameter_returnsValidArtist() throws PersistenceException {
        assertEquals(artistDAO.read("DaoArtistTest"), null);

        ArtistDTO artist = new ArtistDTO("DaoArtistTest");
        //don't need Image and Biography informations while persisting.

        artistDAO.persist(artist);

        assertEquals(artist.getId(), artistDAO.read(artist.getId()).getId());

    }

    /*
     * create an artist with invalid parameters
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createInvalidArtist_shouldThrowPersistenceException() throws PersistenceException {
        artistDAO.persist(invalidArtist);
    }


    /**
     * Reads an existing artist and compares with it's expected name
     */
    @Test
    public void readArtistByName_returnsValidArtist() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);
        ArtistDTO artist = null;
        artistDAO.persist(validArtist);
        artist = artistDAO.read("DaoArtistTest");

        assertEquals("DaoArtistTest", artist.getName());
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());
    }


    /**
     * Tries to read a not jet existing Artist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readNotExistingArtist_throwsPersistenceException() throws PersistenceException {
        if(artistDAO.readAll().size()<9999998){
            artistDAO.read(9999999);
        }else {
            artistDAO.read(artistDAO.readAll().size() + 2);
        }
    }

    /**
     * read artist with non existing id
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void readInvalidSong_shouldThrowPersistenceException() throws PersistenceException {
        artistDAO.read(-1);
    }


    /**
     * read artist with non existing name
     * @throws PersistenceException
     */
    @Test
    public void readInvalidSongByNonExistingName_shouldReturnNull() throws PersistenceException {

        assertEquals(artistDAO.readAll().contains(new ArtistDTO("abcdef")), false);
        ArtistDTO neu = artistDAO.read("abcdef");
        assertEquals(neu, null);
    }


    /**
     * Tries to remove an valid Artist
     */
    @Test
    public void removeAnValidArtist_returnsSizeMinusOne() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);
        int db_size = artistDAO.readAll().size();
        artistDAO.persist(validArtist);
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());
        assertEquals(artistDAO.readAll().size(), db_size + 1);
        artistDAO.remove(validArtist.getId());
        assertEquals(artistDAO.readAll().size(), db_size);
        assertEquals(artistDAO.readAll().contains(validArtist), false);


    }


    /**
     * Remove a not jet existing Artist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotJetExistingArtist_returnsPersistenceException() throws PersistenceException{
        if(artistDAO.readAll().size()<9999998){
            artistDAO.remove(9999999);
        }else {
            artistDAO.remove(artistDAO.readAll().size() + 2);
        }
    }

    /**
     * Remove a invalid id existing Artist
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotExistingId_returnsPersistenceException() throws PersistenceException{
        artistDAO.remove(-1);
    }



    /**
     * insert one artist and read all Artists
     * return size + 1
     * @throws PersistenceException
     */
    @Test
    public void readAll_shouldReturnSizePlusOne() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        int db_size = artistDAO.readAll().size();
        artistDAO.persist(validArtist);
        assertEquals(artistDAO.readAll().size(), db_size + 1);
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size()-1).getId(), validArtist.getId());

    }



    /**
     * update valid artist by its Biography
     * @throws PersistenceException
     */
    @Test
    public void updateValidArtistBiography_shouldReturnCorrectLength() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        artistDAO.persist(validArtist);
        validArtist.setBiography("Test Biography.......");
        artistDAO.update(validArtist);
        assertEquals("Test Biography.......", validArtist.getBiography());
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());

    }

    /**
     * update a valid Artist with too long Biography
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateInvalidBiographyArtist_shouldThrowPersitsenceException() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        try {
            // should persist
            artistDAO.persist(validArtist);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        artistDAO.persistBiography(validArtist.getId(),StringUtils.leftPad("", 8001, '*') );
    }

    /**
     * update a valid artist with null biography
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateValidSongBiographyNull_shouldThrowPersistenceException() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        try {
            // should persist
            artistDAO.persist(validArtist);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        // try to update with invalid lyrics, should fail
        artistDAO.persistBiography(validArtist.getId(), null);
    }


    /**
     * update a valid artist with invalid image
     * @throws PersistenceException
     */

    @Test(expected = PersistenceException.class)
    public void updateValidArtistWithInvalidImage_shouldThrowPersistenceException() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        try {
            // should persist
            artistDAO.persist(validArtist);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        // try to update with invalid lyrics, should fail
        artistDAO.persistImage(validArtist.getId(), new File(StringUtils.leftPad("", 201, '*')));
    }


    /**
     * update a valid artist with valid image
     */

   @Test
    public void updateValidArtistImage_shouldThrowPersistenceException() throws PersistenceException {
       assertEquals(artistDAO.readAll().contains(validArtist), false);
       try {
           // should persist
           artistDAO.persist(validArtist);
       } catch(PersistenceException e) {
           Assert.fail();
       }
       artistDAO.persistImage(validArtist.getId(), new File(StringUtils.leftPad("", 20, '*')));
       assertEquals(artistDAO.read(validArtist.getId()).getImage(), StringUtils.leftPad("", 20, '*'));
       assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());

   }


    /*
    * update a valid artist by its biography and image
    * @throws PersistenceException
    */
    @Test
    public void updateValidArtistBiography_shouldReturnNewBiography() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        try {
            // should persist
            artistDAO.persist(validArtist);
        } catch(PersistenceException e) {
            Assert.fail();
        }

        validArtist.setBiography("Test Biography abcd");
        validArtist.setImage("Test Image abcd");
        artistDAO.update(validArtist);

        assertEquals(artistDAO.read(validArtist.getId()).getBiography(), "Test Biography abcd");
        assertEquals(artistDAO.read(validArtist.getId()).getImage(), "Test Image abcd");
        assertEquals(artistDAO.readAll().get(artistDAO.readAll().size() - 1).getId(), validArtist.getId());

    }

    /*
    *update valid artist with an invalid Image
    * @throws Persistence Exception
     */

    @Test(expected = PersistenceException.class)
    public void updateArtistWithInvalidImage_shouldThrowException() throws PersistenceException {
        assertEquals(artistDAO.readAll().contains(validArtist), false);

        try {
            // should persist
            artistDAO.persist(validArtist);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        validArtist.setImage(StringUtils.leftPad("", 201, '*'));
        artistDAO.update(validArtist);
    }


}
