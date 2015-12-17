package service;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.service.Player;
import at.ac.tuwien.sepm.musicplayer.service.impl.PlayerImpl;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Created by Alexandra on 23.06.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class ServicePlayerTest{

    private Player playerService;

    @MockitoAnnotations.Mock
    private Song mockedDependency;


    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        playerService = new PlayerImpl();
        playerService.setActiveSong(mockedDependency);
    }

    @Test
    public void getActivePlaylist (){
        List<Song> list = playerService.getActivePlaylist();
        assertNull(list);
    }


    @Test
    public void getActiveSong (){
        Song song = playerService.getActiveSong();
        assertNotNull(song);
        assertEquals(song, mockedDependency);
    }


}
