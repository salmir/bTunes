package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.impl.AlbumServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Alexandra on 26.05.2015.
 */
public class ServiceAlbumTest extends AbstractServiceAlbumTest {


    public ServiceAlbumTest() throws PersistenceException {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException, PersistenceException {
        // valid abum
        validAlbum = new AlbumDTO();
        validAlbum.setName("DaoAlbumTest");
        validAlbum.setReleaseYear(2000);
        validAlbum.setArtistName("ArtistTest");
        validAlbum.setCover(StringUtils.leftPad("", 300, '*'));

        // invalid album
        invalidAlbum = mock(AlbumDTO.class, "null");

        invalidAlbum2 = new AlbumDTO();
        invalidAlbum2.setName("DaoAlbumTest2");
        invalidAlbum2.setReleaseYear(2000);
        invalidAlbum2.setArtistName("ArtistTest2");
        invalidAlbum2.setCover(StringUtils.leftPad("", 300, '*'));
        List<AlbumDTO> list = new ArrayList<AlbumDTO>();
        list.add(validAlbum);

        doThrow(new PersistenceException()).when(albumDAO).persist(invalidAlbum2);
        doNothing().when(albumDAO).persist(validAlbum);
        doThrow(new PersistenceException()).when(albumDAO).persistImage(1, new File("fail"));
        doNothing().when(albumDAO).persistImage(1, new File("test"));
        doReturn(validAlbum).when(albumDAO).read(anyInt());
        doReturn(list).when(albumDAO).readAll();
        doNothing().when(albumDAO).remove(anyInt());
        doThrow(new PersistenceException()).when(albumDAO).remove(99999);
        doReturn(list).when(albumDAO).findByName("test name");
        doThrow(new PersistenceException()).when(albumDAO).findByName("nicht vorhanden");




    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Album WHERE name=?", "DaoAlbumTest");
    }

}
