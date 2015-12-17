package at.ac.tuwien.sepm.musicplayer.presentation;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Statistic;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.presentation.StatisticControllerService;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.StatisticService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by Stefan with love on 27.05.2015.
 */
@UI
public class StatisticController implements Initializable {
    private static Logger logger = Logger.getLogger(StatisticController.class);

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private StatisticControllerService statisticControllerService;

    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private SongService songService;

    @FXML
    private TabPane tabPane_Statistic;
    @FXML
    private Tab tab_Histroy;
    @FXML
    private TableView<Statistic> tableView_History;
    @FXML
    private DatePicker datePicker_FromDate;
    @FXML
    private DatePicker datePicker_ToDate;
    @FXML
    private TableColumn<Statistic, Integer> tc_statisticId;
    @FXML
    private TableColumn<Statistic, String> tc_statisticArtist;
    @FXML
    private TableColumn<Statistic, String> tc_statisticTitle;
    @FXML
    private TableColumn<Statistic, String> tc_statisticDate;
    @FXML
    private BarChart<String, Number> barChart_NumberOfSongs;
    @FXML
    private BarChart<String, Number> barChart_TopChart;
    @FXML
    private TextField tf_topCount;

    private Date fromDate;
    private Date toDate;
    private List<Statistic> statisticList;
    private PlayerMainController mainApp;

    private List<Song> topSongList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        topSongList = new ArrayList<>();

        datePicker_FromDate.setValue(LocalDate.now().minusDays(30));
        datePicker_ToDate.setValue(LocalDate.now());

        datePicker_FromDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshTab();
        });

        datePicker_ToDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            refreshTab();
        });

        //refreshTab();
    }

    public void refreshTab() {
        Instant instantFromDate = datePicker_FromDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        fromDate = Date.from(instantFromDate);
        Instant instantToDate = datePicker_ToDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        toDate = Date.from(instantToDate.plusMillis((24 * 60 * 60 * 1000) - 1));

        try {

            statisticList = statisticService.readByDate(fromDate, toDate);
            statisticList.sort(Comparator.comparing(statistic -> statistic.getDatePlayed()));

        } catch (ServiceException serviceException) {
            logger.error(serviceException.getMessage());
        } catch (ValidationException validationException) {
            logger.error(validationException.getMessage());
        }


        initializeHistoryTable();
        statisticControllerService.initializeTabStatistics(statisticList, barChart_NumberOfSongs, fromDate, toDate);
        statisticControllerService.initializeTabTopList(this, tf_topCount.getText(), fromDate, toDate);

    }

    private void initializeHistoryTable() {

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        tableView_History.setItems(FXCollections.observableArrayList(statisticList));

        //tc_statisticId.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()));
        tc_statisticArtist.setCellValueFactory(param -> new SimpleStringProperty((param.getValue().getSongPlayed().get(Type.ARTIST) != null) ?
                param.getValue().getSongPlayed().get(Type.ARTIST).getName() : "Unknown"));
        tc_statisticTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongPlayed().getName()));
        tc_statisticDate.setCellValueFactory(param -> new SimpleStringProperty(dateFormat.format(param.getValue().getDatePlayed())));


        tableView_History.setRowFactory(tableView -> {
            final TableRow<Statistic> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event -> statisticDeleteAction(row.getItem()));

            contextMenu.getItems().addAll(deleteMenuItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    @FXML
    private void statisticDeleteAction(Statistic statistic) {
        tableView_History.getItems().remove(statistic);

        try {
            statisticService.remove(statistic.getId());
        }
        catch(ValidationException validationException) {
            logger.error(validationException.getMessage());
        }
        catch(ServiceException serviceException) {
            logger.error(serviceException.getMessage());
        }
    }

    @FXML
    private void refreshTopCountAction() {
        statisticControllerService.initializeTabTopList(this, tf_topCount.getText(), fromDate, toDate);
    }

    @FXML
    private void addToNewPlaylistAction() {

        Playlist newPlaylist = null;

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            PlaylistDTO newPlaylistDto = new PlaylistDTO("Top " + tf_topCount.getText() +
                " from " + dateFormat.format(fromDate) + " to " +dateFormat.format(toDate));

            playlistService.persist(newPlaylistDto);
            List<SongDTO> listDTO = new ArrayList<>();

            for(Song song: topSongList) {
                listDTO.add(songService.read(song.getId()));
            }
            playlistService.insertSongs(newPlaylistDto.getId(), listDTO);

            newPlaylist = new Playlist(newPlaylistDto);

        } catch (ValidationException e) {
            logger.error(e.getMessage());
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        if(newPlaylist != null) {
            mainApp.getListv_playlists().getItems().add(newPlaylist);
        }
    }

    public void setMainApp(PlayerMainController mainApp) {
        this.mainApp = mainApp;
    }

    public PlayerMainController getMainApp() {
        return mainApp;
    }

    public BarChart<String, Number> getTopBarChart() {
        return barChart_TopChart;
    }

    public List<Song> getTopSongList() {
        return topSongList;
    }

}
