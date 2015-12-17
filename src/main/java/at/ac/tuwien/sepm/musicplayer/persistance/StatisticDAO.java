package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import javafx.util.Pair;

import java.util.Date;
import java.util.List;

/**
 * Additional interface to autocomplete the basic CRUD-methods for the entity Statistic.
 *
 * Created by Alexandra on 10.05.2015.
 */
public interface StatisticDAO extends DAO<StatisticDTO> {

    /**
     * reads all statistic entries of one particular song
     *
     * @param songID the ID of the Song that was played at the moment
     * @return all entries in statistic of one song
     * @throws PersistenceException if the persistence is not available
     */
    public List<StatisticDTO> readAllBySong(int songID) throws PersistenceException;

    /**
     * reads all statistic entries between the given date range
     *
     * @param fromDate the Date to search from
     * @param toDate the Date to search to
     * @return all entries between the given date range (included)
     * @throws PersistenceException if the persistence is not available
     */
    public List<StatisticDTO> readByDate(Date fromDate, Date toDate) throws PersistenceException;

    /**
     * Counts how often a song was played during a timeperiod
     *
     * @param limit the limit of how many most played songs should be considered
     * @param fromDate the from timerange
     * @param toDate the to timerange
     * @return a list of integer, integer Pairs where the first integer is the song_id and
     *          the second is how often the song was played
     */
    public List<Pair<Integer, Integer>> countGroupBySong(int limit, Date fromDate, Date toDate) throws PersistenceException;
}
