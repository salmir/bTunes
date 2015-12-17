package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.StatisticDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by Alexandra on 08.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
@TransactionConfiguration(defaultRollback=true)
public abstract class AbstractDAOStatisticTest extends JdbcBaseDao{

    @Autowired
    protected StatisticDAO statisticDao;

    @Autowired
    protected SongDAO songDAO;

    protected StatisticDTO validStatistic;
    protected StatisticDTO invalidStatistic;

    /**
     * Creates statistic with null.
     * @throws at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void createStatisticWithNull_throwsPersistenceException() throws PersistenceException {
        statisticDao.persist(null);
    }

    /**
     * Create statistic with invalid parameters
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void createSatisticEntryWithInvalidSongId_throwsPersistenceException() throws PersistenceException{

        statisticDao.persist(invalidStatistic);
    }

    /**
     * Creates statistic with valid parameters.
     * @throws PersistenceException
     */
    @Test
    public void createValidStatisticEntry() throws PersistenceException {
        assertEquals(statisticDao.readAll().contains(validStatistic), false);
        statisticDao.persist(validStatistic);
        assertEquals(validStatistic.getSongPlayedId(), statisticDao.readAll().get(statisticDao.readAll().size() - 1).getSongPlayedId());
        assertEquals(statisticDao.readAll().get(statisticDao.readAll().size() - 1).getId(), validStatistic.getId());

    }

    /**
     * Creates statistik with not yet used Song id
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readAllBySongWithNotYetUsedSongId_throwsPersistenceException() throws PersistenceException {
       if(statisticDao.readAll().size()<9999998){
           statisticDao.readAllBySong(9999999);
       }else {
           statisticDao.readAllBySong(statisticDao.readAll().size() + 2);
       }
    }


    /**
     * Creates statistic with invalid Song id
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readAllBySongWithInvalidSongId_throwsPersistenceException() throws PersistenceException {
        statisticDao.readAllBySong(-1);
    }

    /**
     * Read all statistics with valid song id
     * @throws PersistenceException
     */

    @Test
    public void readALLBySongWithValidSongId() throws PersistenceException {
        assertEquals(statisticDao.readAll().contains(validStatistic), false);
        statisticDao.persist(validStatistic);
        assertEquals(statisticDao.readAll().get(statisticDao.readAll().size() - 1).getId(), validStatistic.getId()); //check if statistic is persisted
        List<StatisticDTO> newStatisticList = statisticDao.readAllBySong(validStatistic.getSongPlayedId());
        assertTrue(newStatisticList.size() >= 1); //checks that at least the song played with valid statistic ist found
        assertEquals(newStatisticList.get(newStatisticList.size()-1).getId(),validStatistic.getId());

    }

    /**
     * try to read statistik within a certain Time with invalid Dateparameters
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readByDateWithInvalidDateParameter_throwsPersistenceException() throws PersistenceException {
        List<StatisticDTO> newStatisticList = statisticDao.readByDate(null, null);
    }

    /**
     * try to read statistik within a certain Time with invalid Dateparameters
     * @throws PersistenceException
     */
    @Test (expected = PersistenceException.class)
    public void readByDateWithOneInvalidDateParameter_throwsPersistenceException() throws PersistenceException {
        List<StatisticDTO> newStatisticList = statisticDao.readByDate(null, new Date());
    }

    /**
     * read statistic within a certain period
     * @throws PersistenceException
     */

    @Test
    public void readByDateWithValidDateParameters() throws PersistenceException {
        assertEquals(statisticDao.readAll().contains(validStatistic), false);
        statisticDao.persist(validStatistic);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        List<StatisticDTO> newStatisticList = statisticDao.readByDate(cal.getTime(), new Date());
        assertTrue(newStatisticList.size() > 0);
        assertEquals(newStatisticList.get(newStatisticList.size()-1).getId(),validStatistic.getId());

    }

    /**
     * remove statistic with not yet used id
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void removeStatisticWithNotYetUsedId_shouldThrowException() throws PersistenceException{
        if(statisticDao.readAll().size()<9999998){
            statisticDao.remove(9999999);
        }else {
            statisticDao.remove(statisticDao.readAll().size() + 2);
        }
    }

    /**
     * remove statistic with invalid id
     * @throws PersistenceException
     */
    @Test(expected = PersistenceException.class)
    public void removeStatisticWithInvalidId_shouldThrowException() throws PersistenceException{
        statisticDao.remove(-1);
    }

    /**
     * remove statistic with valid id
     * @throws PersistenceException
     */
    @Test
    public void removeStatisticWithValidId_shouldGiveListMinusOne() throws PersistenceException{
        assertEquals(statisticDao.readAll().contains(validStatistic), false);
        int db_size = statisticDao.readAll().size();
        statisticDao.persist(validStatistic);
        assertEquals(statisticDao.readAll().get(statisticDao.readAll().size() - 1).getId(), validStatistic.getId());
        assertEquals(statisticDao.readAll().size(), db_size + 1);
        statisticDao.remove(validStatistic.getId());
        assertEquals(statisticDao.readAll().size(), db_size);
        assertEquals(statisticDao.readAll().contains(validStatistic), false);


    }

}

