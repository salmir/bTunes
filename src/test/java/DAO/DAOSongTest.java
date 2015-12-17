package DAO;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcSongDao;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Alexandra on 06.05.2015.
 */
public class DAOSongTest extends AbstractDAOSongTest {



    @BeforeClass
    public static void setUpClass() throws SQLException{
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException {
        // valid song
        validSong = new SongDTO();
        validSong.setName("DaoSongTest");
        validSong.setPath(StringUtils.leftPad("", 300, '*'));
        validSong.setReleaseYear(2000);
        validSong.setLength(350);
        validSong.setArtistName("ArtistTest");
        validSong.setAlbumName("AlbumTest");
        validSong.setGenre(StringUtils.leftPad("", 100, '*'));
        validSong.setRating(5);
        validSong.setLyrics(StringUtils.leftPad("", 8000, '*'));

        // invalid song
        invalidSong = new SongDTO();
        invalidSong.setName(StringUtils.leftPad("", 201, '*'));
        invalidSong.setPath(StringUtils.leftPad("", 301, '*'));
        invalidSong.setReleaseYear(2000);
        invalidSong.setLength(-100);
        invalidSong.setArtistName(null);
        invalidSong.setAlbumName(null);
        invalidSong.setGenre(StringUtils.leftPad("", 101, '*'));
        invalidSong.setRating(-100);
        invalidSong.setLyrics(StringUtils.leftPad("", 8001, '*'));
    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Song WHERE name=?", "DaoSongTest");
    }

}
