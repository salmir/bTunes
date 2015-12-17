package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.StatisticDAO;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Stefan Weißegger
 */
@Repository
public class JdbcStatisticDao  extends JdbcBaseDao implements StatisticDAO {

    private static final Logger logger = Logger.getLogger(JdbcStatisticDao.class);

    private static final String INSERT = "INSERT INTO Statistic(id, date, song_id) VALUES (DEFAULT, ?, ?)";
    private static final String READ = "SELECT * FROM Statistic WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM Statistic";
    private static final String GET_BY_SONG = "SELECT * FROM Statistic WHERE song_id = ?";
    private static final String GET_BY_DATERANGE = "SELECT * FROM Statistic WHERE DATE >= ? AND DATE <= ?";
    private static final String DELETE = "DELETE FROM Statistic WHERE id = ?";

    private static final String COUNT_ID = "SELECT COUNT(*) FROM Statistic WHERE song_id = ?";
    private static final String COUNT_GROUPBY_SONG = "SELECT COUNT(*) COUNT, SONG_ID FROM Statistic WHERE DATE >= ? AND DATE <= ? GROUP BY SONG_ID " +
            "ORDER BY COUNT DESC LIMIT ?";

    @Autowired
    JdbcSongDao jdbcSongDao;

    @Override
    public void persist(StatisticDTO toPersist) throws PersistenceException {
        if(toPersist == null) {
            throw new PersistenceException("StatisticsDTO == null");
        }

        int newId ;
        try {
            SongDTO songDTO = jdbcSongDao.read(toPersist.getSongPlayedId());
            if(songDTO == null) {
                logger.error("song to statistic does not exist in database");
                throw new PersistenceException("song does not exist in database");
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT,
                        new String[]{"id"});

                preparedStatement.setTimestamp(1, new Timestamp(toPersist.getDateSongPlayed().getTime()));
                preparedStatement.setInt(2, toPersist.getSongPlayedId());
                return preparedStatement;
            }, keyHolder);


            newId = keyHolder.getKey().intValue();
            toPersist.setId(newId);

        } catch(DataAccessException e) {
            logger.error("failed to persist statistic");
            throw new PersistenceException("failed to persist statistic", e);
        }
        logger.debug("new statistic with id="+ newId +" stored.");
    }

    @Override
    public StatisticDTO read(int id) throws PersistenceException {
        StatisticDTO statisticDTO = new StatisticDTO();

        try {
            statisticDTO = jdbcTemplate.queryForObject(READ, RowMappers.getStatisticRowMapper(), id);
        } catch(DataAccessException e) {
            logger.error("Failed to read statistic with id " + id);
            throw new PersistenceException("Faied to read statistic with id " + id);
        }
        return statisticDTO;
    }

    @Override
    public List<StatisticDTO> readAll() throws PersistenceException {
        List<StatisticDTO> statistics = new ArrayList<>();
        try {
            statistics = jdbcTemplate.query(GET_ALL, RowMappers.getStatisticRowMapper());

        } catch(DataAccessException e) {
            logger.error("failed to read statistic");
            throw new PersistenceException("failed to read statistics", e);
        }
        return statistics;
    }

    @Override
    public List<StatisticDTO> readAllBySong(int songID) throws PersistenceException {
        List<StatisticDTO> statistics = new ArrayList<>();
        try {
            statistics = jdbcTemplate.query(GET_BY_SONG, RowMappers.getStatisticRowMapper(), songID);
            if(statistics.size() <= 0) {
                logger.debug("no entry with this song_id stored!");
                throw new PersistenceException("no statistic for song with id = "+ songID);
            }
        } catch(DataAccessException e) {
            logger.error("failed to read statistic");
            throw new PersistenceException("failed to read statistics", e);
        }
        return statistics;
    }

    @Override
    public List<StatisticDTO> readByDate(Date fromDate, Date toDate) throws PersistenceException {
        List<StatisticDTO> statistics = new ArrayList<>();
        if(fromDate == null || toDate == null)
            throw new PersistenceException("invalid Date Parameter fromDate = " + fromDate + ", toDate = " + fromDate);
        try {
            statistics = jdbcTemplate.query(GET_BY_DATERANGE, RowMappers.getStatisticRowMapper(), fromDate, toDate);

        } catch(DataAccessException e) {
            logger.error("failed to read statistic");
            throw new PersistenceException("failed to read statistics", e);
        }
        return statistics;
    }

    @Override
    public List<Pair<Integer, Integer>> countGroupBySong(int limit, Date fromDate, Date toDate) throws PersistenceException {
        List<Pair<Integer, Integer>> countedList = new ArrayList<>();

        if (fromDate == null || toDate == null)
            throw new PersistenceException("invalid Date Parameter fromDate = " + fromDate + ", toDate = " + fromDate);
        try {
            countedList = jdbcTemplate.query(
                    COUNT_GROUPBY_SONG, RowMappers.getStatisticGroupByCountMapper(), fromDate, toDate, limit);
        } catch (DataAccessException e) {
            logger.error("failed to read statistic");
            throw new PersistenceException("failed to read statistics", e);
        }
        return countedList;
    }



    @Override
    public void remove(int id) throws PersistenceException {
        if(id < 0)
            throw new PersistenceException("entity id can't be below 0");
        try {
            int count = jdbcTemplate.update(DELETE, id);
            if(count == 0)
                throw new PersistenceException("no statistic entry found with id: " + id);
        } catch(DataAccessException e) {
            logger.error("failed to remove statistic with id="+ id);
            throw new PersistenceException("failed to remove statistic with id="+ id, e);
        }
        logger.debug("removed statistic with id=" + id + ".");
    }

    @Override
    public void update(StatisticDTO toUpdate) throws PersistenceException {
        //no need for that :)
    }
}
