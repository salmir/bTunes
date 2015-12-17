package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.persistance.StatisticDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcBaseDao;
import at.ac.tuwien.sepm.musicplayer.service.impl.StatisticServiceImpl;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Alexandra on 26.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public abstract class AbstractServiceStatisticTest  extends JdbcBaseDao {
    @InjectMocks
    protected StatisticServiceImpl statisticService;

    protected StatisticDTO invalidStatistic2;
    protected StatisticDTO invalidStatistic;
    protected StatisticDTO validStatistic;

    @Mock
    StatisticDAO statisticDAO;




    public AbstractServiceStatisticTest() throws PersistenceException {
        MockitoAnnotations.initMocks(this); //This is a key
    }

   /**
     * Create statistic with invalid parameters
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void persistTestWithInvalidstatistic_ShouldThrowException() throws ValidationException, ServiceException {
        statisticService.persist(invalidStatistic);
    }

    /**
     * Create statistic with invalid parameters, should throw Persistence
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void persistTestWithInValidParameters_ShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        statisticService.persist(invalidStatistic2);
        verify(statisticDAO).persist(invalidStatistic2);
    }


    /**
     * Create statistic with valid parameters, persist
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void persistTestWithValidParameters() throws ValidationException, ServiceException, PersistenceException {
        statisticService.persist(validStatistic);
        verify(statisticDAO).persist(validStatistic);
        verify(statisticDAO, times(1)).persist(validStatistic);
    }

    /**
     * read an valid statistic
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test
    public void readValidstatisticShouldPersist() throws ValidationException, ServiceException, PersistenceException {

        StatisticDTO readstatistic = statisticService.read(3);
        verify(statisticDAO, times(1)).read(3);
    }


    /**
     * read an statistic with invalid id
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test(expected = ValidationException.class)
    public void readInvalidstatisticShouldThrowException() throws ValidationException, ServiceException {
        try{
            statisticService.read(-1);
        }catch(ServiceException s){
            Assert.fail();
        }
    }

    /**
     * read all statistics
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void readAllShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        List<StatisticDTO> list = statisticService.readAll();
        assertEquals(list.size(), 1);
        verify(statisticDAO, times(1)).readAll();

    }

    /**
     * remove an valid statistic
     * @throws ValidationException
     * @throws ServiceException
     */
    @Test
    public void removeValidstatisticShouldPersist() throws ValidationException, ServiceException, PersistenceException {
        statisticService.remove(2);
        verify(statisticDAO, times(1)).remove(2);
    }

    /**
     * remove an not existing statistic id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ValidationException.class)
    public void removeInvalidstatisticShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        statisticService.remove(-1);
        verify(statisticDAO, times(1)).remove(-1);

    }

    /**
     * remove an not existing statistic id
     * @throws ValidationException
     * @throws ServiceException
     */

    @Test(expected = ServiceException.class)
    public void removeInvalidNotExistingstatisticsticShouldThrowException() throws ValidationException, ServiceException, PersistenceException {
        statisticService.remove(99999);
        verify(statisticDAO, times(1)).remove(99999);

    }

    /**
     * countsongbyId
     * @throws ServiceException
     * @throws ValidationException
     */
    @Test
    public void countSongsByID_shouldreturnEmptyList() throws ServiceException, ValidationException {
        List<Pair<Song, Integer>> songs = statisticService.countSongByID(4, new Date(), new Date());
        assertEquals(songs, Collections.emptyList());
    }

    /**
     * count songs by id with invalid parameters
     * @throws ServiceException
     * @throws ValidationException
     */
    @Test(expected=ServiceException.class)
    public void countSongsByID_shouldthrowException() throws ServiceException, ValidationException {
        List<Pair<Song, Integer>> songs = statisticService.countSongByID(4, null, new Date());
        assertEquals(songs, Collections.emptyList());
    }
    
}
