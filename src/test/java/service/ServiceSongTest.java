package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Lena Lenz.
 */
public class ServiceSongTest extends AbstractServiceSongTest {

    public ServiceSongTest() throws PersistenceException {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException, PersistenceException {
        // valid song
        validSong = new SongDTO();
        validSong.setName("ServiceSongTest");
        validSong.setPath("C://");
        validSong.setReleaseYear(2000);
        validSong.setLength(350);
        validSong.setArtistName("ArtistTest");
        validSong.setAlbumName("AlbumTest");
        validSong.setGenre("Pop");
        validSong.setRating(5);
        validSong.setLyrics("lalalala..");

        // invalid song
        invalidSong = mock(SongDTO.class, "null");

        invalidSong2 = new SongDTO();
        invalidSong2.setName("ServiceSongTest");
        invalidSong2.setPath("C://");
        invalidSong2.setReleaseYear(2000);
        invalidSong2.setLength(350);
        invalidSong2.setArtistName("ArtistTest");
        invalidSong2.setAlbumName("AlbumTest");
        invalidSong2.setGenre("Pop");
        invalidSong2.setRating(5);
        invalidSong2.setLyrics("lalalala..");
        List<SongDTO> list = new ArrayList<SongDTO>();
        list.add(validSong);

        doThrow(new PersistenceException()).when(songDAO).persist(invalidSong2);
        doNothing().when(songDAO).persist(validSong);
        doThrow(new PersistenceException()).when(songDAO).persistLyrics(1, "fail");
        doNothing().when(songDAO).persistLyrics(1, "new lyrics");
        doThrow(new PersistenceException()).when(songDAO).persistRating(1, 6);
        doNothing().when(songDAO).persistRating(1, 4);
        doReturn(validSong).when(songDAO).read(anyInt());
        doReturn(list).when(songDAO).readAll();
        doNothing().when(songDAO).remove(anyInt());
        doThrow(new PersistenceException()).when(songDAO).remove(99999);
        doReturn(list).when(songDAO).findByName("test name");
        doThrow(new PersistenceException()).when(songDAO).findByName("nicht vorhanden");
    }



    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Song WHERE name=?", "ServiceSongTest");
    }

}
