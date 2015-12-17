package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Created by Alexandra on 26.05.2015.
 */
public class ServiceArtistTest extends AbstractServiceArtistTest {
    public ServiceArtistTest() throws PersistenceException {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException, PersistenceException {
        // valid artist
        validArtist = new ArtistDTO();
        validArtist.setName("DaoArtistTest");
        validArtist.setImage(StringUtils.leftPad("", 200, '*'));
        validArtist.setBiography(StringUtils.leftPad("", 8000, '*'));

        // invalid artist
        invalidArtist = mock(ArtistDTO.class, "null");

        invalidArtist2 = new ArtistDTO();
        invalidArtist2.setName("DaoAlbumTest2");
        invalidArtist2.setName("DaoArtistTest");
        invalidArtist2.setImage(StringUtils.leftPad("", 200, '*'));
        invalidArtist2.setBiography(StringUtils.leftPad("", 8000, '*'));
        List<ArtistDTO> list = new ArrayList<ArtistDTO>();
        list.add(validArtist);

        doThrow(new PersistenceException()).when(artistDAO).persist(invalidArtist2);
        doNothing().when(artistDAO).persist(validArtist);
        doThrow(new PersistenceException()).when(artistDAO).persistImage(1, new File("fail"));
        doNothing().when(artistDAO).persistImage(1, new File("test"));
        doReturn(validArtist).when(artistDAO).read(anyInt());
        doReturn(list).when(artistDAO).readAll();
        doNothing().when(artistDAO).remove(anyInt());
        doThrow(new PersistenceException()).when(artistDAO).remove(99999);
        doReturn(list).when(artistDAO).findByName("test name");
        doThrow(new PersistenceException()).when(artistDAO).findByName("nicht vorhanden");


    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Artist WHERE name=?", "DaoArtistTest");
    }
}
