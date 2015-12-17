package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by marjaneh.
 */
public class ServicePlaylistTest extends AbstractServicePlaylistTest {

    public ServicePlaylistTest() throws PersistenceException {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Manually insert sql-statements
    }

    @Before
    public void setUp() throws SQLException, PersistenceException {
        // valid playlist
        validPlaylist = new PlaylistDTO();
        validPlaylist.setName("ServicePlaylistTest");

        // invalid playlist
        invalidPlaylist = mock(PlaylistDTO.class, "null");

        invalidPlaylist2 = new PlaylistDTO();
        invalidPlaylist2.setName("ServicePlaylistTest2");

        List<PlaylistDTO> list = new ArrayList<PlaylistDTO>();
        list.add(validPlaylist);

        doThrow(new PersistenceException()).when(playlistDAO).persist(invalidPlaylist2);
        doNothing().when(playlistDAO).persist(validPlaylist);
        doReturn(validPlaylist).when(playlistDAO).read(anyInt());
        doReturn(list).when(playlistDAO).readAll();
        doNothing().when(playlistDAO).remove(anyInt());
        doThrow(new PersistenceException()).when(playlistDAO).remove(99999);
        doNothing().when(playlistDAO).update(validPlaylist);
        doThrow(new PersistenceException()).when(playlistDAO).update(invalidPlaylist);
        doReturn(Collections.emptyList()).when(playlistDAO).getSongs(anyInt());
        doReturn(list).when(playlistDAO).getAllPlaylistSongs();


    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Playlist WHERE name = ?", "ServicePlaylistTest");
    }
}
