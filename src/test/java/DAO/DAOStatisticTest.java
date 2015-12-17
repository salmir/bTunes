package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Alexandra on 08.05.2015.
 */
public class DAOStatisticTest extends AbstractDAOStatisticTest {

    @BeforeClass
    public static void setUpClass() throws SQLException{
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException, PersistenceException {
        // valid statistic
        validStatistic = new StatisticDTO();
        validStatistic.setDateSongPlayed(new Date());
        validStatistic.setSongPlayedId(songDAO.readAll().get(0).getId());


        // invalid statistic
        invalidStatistic = new StatisticDTO();
        invalidStatistic.setDateSongPlayed(null);
        invalidStatistic.setSongPlayedId(-1);
    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Statistic WHERE date =? ", new Date());
    }
}
