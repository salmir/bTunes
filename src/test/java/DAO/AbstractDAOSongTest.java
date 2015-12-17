package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.ArgumentNullException;
import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexandra on 06.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
@TransactionConfiguration(defaultRollback=true)
public abstract class AbstractDAOSongTest extends JdbcBaseDao {

    @Autowired
    protected JdbcSongDao songDAO;

    protected SongDTO validSong;
    protected SongDTO invalidSong;


    /**
     * Creates the song with null.
     * @throws at.ac.tuwien.sepm.musicplayer.exceptions.ArgumentNullException
     */
    @Test(expected = PersistenceException.class)
    public void createSongWithNull_throwsArgumentNullException() throws PersistenceException {
        songDAO.persist(null);
    }



    /**
     * Creates the song with too long title name
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void createSongWithTooLongTitleParameter_throwValidationException() throws PersistenceException{

        SongDTO song = new SongDTO("tesaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat", "C://....mp3");
        songDAO.persist(song);
    }

    /**
     * Creates the song with valid parameters
     */
    @Test
    public void createSongWithValidParameter_returnTestSong() throws PersistenceException{
        assertEquals(songDAO.readAll().contains(validSong), false);
        songDAO.persist(validSong);
        assertEquals(validSong.getId(), songDAO.readAll().get(songDAO.readAll().size() - 1).getId());
    }

    /**
     * Creates an song with minimum expected valid parameters (other parameter are null)
     */
    @Test
    public void createSongWithMinimumExpectedValidParameter_returnsValidSOng() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);

        SongDTO song = new SongDTO("DaoSongTest", "Path");
        song.setLength(8);
        song.setArtistId(3);
        song.setAlbumId(1);
        song.setRating(3);

        try {
            songDAO.persist(song);
        } catch (PersistenceException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(song.getId(), songDAO.readAll().get(songDAO.readAll().size() - 1).getId());

    }


    /**
     * Reads an existing song and compares with it's expected name
     */
    @Test
    public void readSongByName_returnsValidSong() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);

        List<SongDTO> song = null;
        songDAO.persist(validSong);
        song = songDAO.findByName("DaoSongTest");

        assertEquals("DaoSongTest", song.get(song.size() - 1).getName());
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }

    /**
     * Read Song by its names and checks if all are correct
     * @throws PersistenceException
     */
    @Test
    public void readSongByName_returnsValidSongs() throws PersistenceException {
        int count = songDAO.findByName(validSong.getName()).size();
        songDAO.persist(validSong);
        List<SongDTO> readNames = songDAO.findByName(validSong.getName());
        boolean contains = false;
        for(SongDTO songDTO : readNames) {
            if(songDTO.getIdentifier().equals(validSong.getIdentifier())) {
                contains = true;
                break;
            }else{
                contains = false;
            }
        }
        assertTrue(contains);
        assertEquals(readNames.size(), count + 1);
    }



    /**
     * Tries to read a non existing song
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readNotExistingSong_throwsPersistenceException() throws PersistenceException {
        if(songDAO.readAll().size()<9999998){
            songDAO.read(9999999);
        }else {
            songDAO.read(songDAO.readAll().size() + 2);
        }
    }

    /**
     * Remove a non existing Song
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void removeAnNotExistingSong_returnsPersistenceException() throws PersistenceException{
        if(songDAO.readAll().size()<9999998){
            songDAO.remove(9999999);
        }else {
            songDAO.remove(songDAO.readAll().size() + 2);
        }
    }

    /**
     * create a valid song and afterwards remove it
     * @throws PersistenceException
     */
    @Test
    public void removeValidSong_shouldReturnSizeMinusOne() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);

        int db_size = songDAO.readAll().size();
        songDAO.persist(validSong);
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());
        assertEquals(songDAO.readAll().size(), db_size + 1);
        songDAO.remove(validSong.getId());
        assertEquals(songDAO.readAll().size(), db_size);
        assertEquals(songDAO.readAll().contains(validSong), false);

    }

    /*
     * create a song with valid parameters
     * @throws PersistenceException
     */
    @Test
    public void createValidSong_shouldThrowPersistenceException() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        int songCount = songDAO.readAll().size();
        songDAO.persist(validSong);
        // check if id, artist name/id and album name/id were set
        assertTrue(validSong.getId() != -1 && validSong.getArtistName() != null && validSong.getAlbum() != null && validSong.getArtistId() != -1 && validSong.getAlbumId() != -1);
        // check if database size changed by 1 inserted row
        assertTrue(songDAO.readAll().size() == songCount + 1);
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }

    /*
     * create a song with invalid parameters
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void createInvalidSong_shouldThrowPersistenceException() throws PersistenceException {
        songDAO.persist(invalidSong);
    }

    /*
    * update a valid song by its rating
    * @throws PersistenceException
    */
    @Test
    public void updateValidSongRating_shouldReturnNewRating() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);

        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        int ratingUpdate = 2;
        songDAO.persistRating(validSong.getId(), ratingUpdate);
        assertEquals(songDAO.read(validSong.getId()).getRating(), ratingUpdate);
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }

    /*
    *update valid song with an invalid Rating
    * @throws Persistence Exception
     */

    @Test(expected = PersistenceException.class)
    public void updateInvalidSongRatingShouldThrowException() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        // try to update with invalid , should fail
        songDAO.persistRating(validSong.getId(), -1);

    }

    /**
     * update a valid song with new lyrics
     */

    @Test
    public void updateValidSongLyrics_shouldReturnNewLyrics() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        String lyricsUpdate = "Lyrics update";
        songDAO.persistLyrics(validSong.getId(), lyricsUpdate);
        assertEquals(songDAO.read(validSong.getId()).getLyrics(), lyricsUpdate);
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }


    /**
     * update a valid song with null lyrics
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateInvalidSongLyrics_shouldThrowPersistenceException() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        // try to update with invalid lyrics, should fail
        songDAO.persistLyrics(validSong.getId(), null);

    }


    /**
     * update a valid song with invalid lyrics
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateInvalidSongLyrics_sShouldThrowPersistenceException() throws PersistenceException {
        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
           Assert.fail();
        }
        // try to update with invalid lyrics, should fail
        songDAO.persistLyrics(validSong.getId(), StringUtils.leftPad("", 8001, '*'));
    }

    /**
     * read song with certain id
     * @throws PersistenceException
     */
    @Test
    public void readValidSong_shouldReturnValidSong() throws PersistenceException {

        assertEquals(songDAO.readAll().contains(validSong), false);
        songDAO.persist(validSong);
        SongDTO readSong = songDAO.read(validSong.getId());
        assertEquals(validSong.getPath(), readSong.getPath());
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }

    /**
     * read song with non existing id
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void readInvalidSong_shouldThrowPersistenceException() throws PersistenceException {
        songDAO.read(-1);
    }


    /**
     * insert one song and read all Songs
     * return size + 1
     * @throws PersistenceException
     */
    @Test
    public void readAll_shouldReturnSizePlusOne() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        int db_size = songDAO.readAll().size();
        songDAO.persist(validSong);
        assertEquals(songDAO.readAll().size(), db_size + 1);
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());


    }



    /**
     * update valid song by its length
     * @throws PersistenceException
     */
    @Test
    public void updateValidSong_shouldReturnCorrectLength() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        songDAO.persist(validSong);
        validSong.setLength(4000);
        songDAO.update(validSong);
        assertEquals(4000, validSong.getLength());
        assertEquals(songDAO.readAll().get(songDAO.readAll().size() - 1).getId(), validSong.getId());

    }

    /**
     * update a validSong with invalid Genre
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void updateInvalidGenreSong_shouldThrowPersitsenceException() throws PersistenceException {
        assertEquals(songDAO.readAll().contains(validSong), false);
        try {
            // should persist
            songDAO.persist(validSong);
        } catch(PersistenceException e) {
            Assert.fail();
        }
        validSong.setGenre(StringUtils.leftPad("", 101, '*'));
        songDAO.update(validSong);
    }

}
