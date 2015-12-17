package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Alexandra on 26.05.2015.
 */
public class ServiceStatisticTest extends AbstractServiceStatisticTest{

    public ServiceStatisticTest() throws PersistenceException {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException, PersistenceException, ServiceException, ValidationException {
        // valid statistic
        validStatistic = new StatisticDTO();
        validStatistic.setDateSongPlayed(new Date());
        validStatistic.setSongPlayedId(1);


        // invalid statistic
        invalidStatistic = mock(StatisticDTO.class, "null");

        invalidStatistic2 = new StatisticDTO();
        invalidStatistic2.setDateSongPlayed(new Date());
        invalidStatistic2.setSongPlayedId(1);
        List<StatisticDTO> list = new ArrayList<StatisticDTO>();
        list.add(validStatistic);

        doThrow(new PersistenceException()).when(statisticDAO).persist(invalidStatistic2);
        doNothing().when(statisticDAO).persist(validStatistic);
        doReturn(validStatistic).when(statisticDAO).read(anyInt());
        doReturn(list).when(statisticDAO).readAll();
        doNothing().when(statisticDAO).remove(anyInt());
        doThrow(new PersistenceException()).when(statisticDAO).remove(99999);
        doThrow(new PersistenceException()).when(statisticDAO).countGroupBySong(4, null, new Date());
    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Statistic WHERE date =? ", new Date());
    }
}
