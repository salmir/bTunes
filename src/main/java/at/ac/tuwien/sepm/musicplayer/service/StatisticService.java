package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Statistic;
import javafx.util.Pair;

import java.util.Date;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface StatisticService extends Service<StatisticDTO> {
    public List<Statistic> readByDate(Date fromDate, Date toDate) throws ValidationException, ServiceException;

    Integer countSongsOnDay(List<Statistic> statisticList, Date dayToCount);

    public List<Pair<Song, Integer>> countSongByID(int limit, Date fromDate, Date toDate) throws ServiceException, ValidationException;
}
