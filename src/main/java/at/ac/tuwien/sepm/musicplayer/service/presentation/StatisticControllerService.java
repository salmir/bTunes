package at.ac.tuwien.sepm.musicplayer.service.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Statistic;
import at.ac.tuwien.sepm.musicplayer.presentation.StatisticController;
import at.ac.tuwien.sepm.musicplayer.service.StatisticService;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Stefan with love on 04.06.2015.
 */
@Service
public class StatisticControllerService {

    private static Logger logger = Logger.getLogger(StatisticControllerService.class);

    @Autowired
    StatisticService statisticService;

    public void initializeTabStatistics(List<Statistic> statistics, BarChart<String, Number> barChartNumberOfSongs, Date fromDate, Date toDate) {
        XYChart.Series countSongsSeries = new XYChart.Series();
        Date actualDate = fromDate;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM");

        while(actualDate.compareTo(toDate) <= 0) {
            countSongsSeries.getData().add(new XYChart.Data(dateFormat.format(actualDate), statisticService.countSongsOnDay(statistics, actualDate)));
            actualDate = new Date(actualDate.getTime() + 1000*60*60*24);
        }

        barChartNumberOfSongs.getData().clear();
        barChartNumberOfSongs.getData().addAll(countSongsSeries);
    }

    public void initializeTabTopList(StatisticController statisticController, String topCount, Date fromDate, Date toDate) {
        XYChart.Series countSongsSeries = new XYChart.Series();

        int limit = Integer.parseInt(topCount);

        try {
            for (Pair<Song, Integer> songCount : statisticService.countSongByID(limit, fromDate, toDate)) {
                countSongsSeries.getData().add(new XYChart.Data(songCount.getKey().getName(), songCount.getValue()));
                statisticController.getTopSongList().add(songCount.getKey());
            }

            statisticController.getTopBarChart().getData().clear();
            statisticController.getTopBarChart().getData().addAll(countSongsSeries);
        }
        catch(ValidationException validationException) {
            logger.error(validationException.getMessage());
        }
        catch(ServiceException serviceException) {
            logger.error(serviceException);
        }
    }
}
