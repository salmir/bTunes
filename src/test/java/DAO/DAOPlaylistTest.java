package DAO;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.PlaylistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcPlaylistDao;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Alexandra on 08.05.2015.
 */
public class DAOPlaylistTest extends AbstractDAOPlaylistTest{

    @BeforeClass
    public static void setUpClass() throws SQLException{
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException {
        // valid playlist
        validPlaylist = new PlaylistDTO();
        validPlaylist.setName("DaoPlaylistTest");


        // invalid playlist
        invalidPlaylist = new PlaylistDTO();
        invalidPlaylist.setName(StringUtils.leftPad("", 201, '*'));
    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Song WHERE name=?", "DaoPlaylistTest");
    }

}
