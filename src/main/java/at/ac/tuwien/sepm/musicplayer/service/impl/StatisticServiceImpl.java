package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Statistic;
import at.ac.tuwien.sepm.musicplayer.persistance.StatisticDAO;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.StatisticService;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    private static final Logger logger = Logger.getLogger(StatisticServiceImpl.class);
    private static final String DUMMY_ENTRY = "This song has already been deleted";

    @Autowired
    private StatisticDAO statisticDAO;

    @Autowired
    private SongService songService;
    @Autowired
    private Library library;

    @Override
    public void persist(StatisticDTO toPersist) throws ValidationException, ServiceException {
        if(toPersist != null && toPersist.getDateSongPlayed() != null && toPersist.getSongPlayedId() >= 0) {
            try {
                statisticDAO.persist(toPersist);
            } catch (PersistenceException persistanceException) {
                logger.error(persistanceException.getMessage());
                throw new ServiceException(persistanceException.getMessage());
            }
        }
        else
            throw new ValidationException("Statistic to persist is not valid - SongID: " + toPersist.getSongPlayedId() +
             "Song played at: " + toPersist.getDateSongPlayed());
    }

    @Override
    public StatisticDTO read(int id) throws ValidationException, ServiceException {
        if(id < 0)
            throw new ValidationException("id can't be less than zero");
        try {
            return statisticDAO.read(id);
        } catch (PersistenceException persistanceException) {
            logger.error(persistanceException.getMessage());
            throw new ServiceException(persistanceException.getMessage());
        }
    }

    @Override
    public List<StatisticDTO> readAll() throws ServiceException {
        try {
            return statisticDAO.readAll();
        } catch (PersistenceException persistanceException) {
            logger.error(persistanceException.getMessage());
            throw new ServiceException(persistanceException.getMessage());
        }
    }

    @Override
    public void remove(int id) throws ValidationException, ServiceException {
        try {
            if(id < 0)
                throw new ValidationException("Invalid Id to remove: Id cannnot be less than 0");
            statisticDAO.remove(id);
        } catch (PersistenceException persistanceException) {
            logger.error(persistanceException.getMessage());
            throw new ServiceException(persistanceException.getMessage());
        }
    }

    @Override
    public void update(StatisticDTO toUpdate) throws ValidationException, ServiceException {
        throw new ServiceException("This method is not implemented (no use for it for now)");
    }

    @Override
    public List<Statistic> readByDate(Date fromDate, Date toDate) throws ValidationException, ServiceException {
        List<StatisticDTO> statisticDTOList;
        List<Statistic> statisticList = new ArrayList<Statistic>();
        Song newSong;
        SongDTO songDto;
        try {
            statisticDTOList = statisticDAO.readByDate(fromDate, toDate);

            for(StatisticDTO statisticDto : statisticDTOList) {
                try {
                    songDto = songService.read(statisticDto.getSongPlayedId());
                    newSong = library.getSongMap().get(songDto.getIdentifier());

                    statisticList.add(new Statistic(statisticDto.getId(), newSong, statisticDto.getDateSongPlayed()));
                }
                catch(ServiceException serviceException) {
                    statisticList.add(new Statistic(statisticDto.getId(), new Song(DUMMY_ENTRY), statisticDto.getDateSongPlayed()));
                }

            }
            return statisticList;
        }
        catch (PersistenceException persistanceException) {
            logger.error(persistanceException.getMessage());
            throw new ServiceException(persistanceException.getMessage());
        }
    }

    @Override
    public Integer countSongsOnDay(List<Statistic> sortedStatisticList, Date dayToCount) {
        Integer counter = 0;

        for(Statistic statistic: sortedStatisticList) {
            if(statistic.getDatePlayed().compareTo(dayToCount) >= 0 &&
                    statistic.getDatePlayed().compareTo(new Date(dayToCount.getTime() + 1000*60*60*24)) <= 0) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public List<Pair<Song, Integer>> countSongByID(int limit, Date fromDate, Date toDate) throws ServiceException, ValidationException {

        List<Pair<Song, Integer>> counted = new ArrayList<>();
        SongDTO songDto;
        Song newSong;
        try {
            List<Pair<Integer, Integer>> countedSongs = statisticDAO.countGroupBySong(limit, fromDate, toDate);

            for(Pair<Integer, Integer> pair: countedSongs) {
                try {
                    songDto = songService.read(pair.getKey());
                    newSong = library.getSongMap().get(songDto.getIdentifier());

                    counted.add(new Pair<>(newSong, pair.getValue()));
                }
                catch(ServiceException serviceException) {
                    counted.add(new Pair<>(new Song(DUMMY_ENTRY), pair.getValue()));
                }
            }
            return counted;
        }
        catch(PersistenceException persistanceException) {
            throw new ServiceException(persistanceException.getMessage());
        }
    }
}
