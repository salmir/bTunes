package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.impl.DJServiceImpl;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Salmir on 03.06.2015.
 */
@UI
public class DJMainController implements Initializable {

    private static final Logger logger = Logger.getLogger(DJMainController.class);
    private List<Song> allSongsFromPlaylist;
    private List<Song> leftPlaylist = new ArrayList<>();
    private List<Song> rightPlaylist = new ArrayList<>();

    @Autowired
    private DJServiceImpl djService;

    @FXML
    private BorderPane mainPain;
    @FXML
    private TableView dj_left_tableView;
    @FXML
    private TableView dj_right_tableView;
    @FXML
    private Button dj_left_previous_btn, dj_left_stop_btn, dj_left_next_btn, dj_right_previous_btn, dj_right_stop_btn, dj_right_next_btn;
    @FXML
    private ToggleButton dj_left_playPause_btn, dj_right_playPause_btn;
    @FXML
    private Button dj_toLeft_btn, dj_toRight_btn, dj_fade_btn;
    @FXML
    private Label dj_left_song_label, dj_right_song_label, dj_playlistName_label, lb_fadeTimeValue;
    @FXML
    private TableColumn<Song, String> tc_left_artist, tc_left_title, tc_left_duration, tc_right_artist, tc_right_title, tc_right_duration;
    @FXML
    private Slider fadeVolume;
    @FXML
    private Slider djLeftDuration;
    @FXML
    private Slider djRightDuration;
    @FXML
    private Slider masterVolume;
    @FXML
    private Slider fadeTimeSlider;
    @FXML
    private Label lb_left_currentTime, lb_left_duration;
    @FXML
    private Label lb_right_currentTime, lb_right_duration;


    private Image playImage = new Image("/images/play.png");
    private Image pauseImage = new Image("/images/pause.png");
    private Image stopImage = new Image("/images/stop.png");
    private Image nextImage = new Image("/images/next.png");
    private Image previousImage = new Image("/images/previous.png");
    private Image toRightImage = new Image("/images/toRight.png");
    private Image toLeftImage = new Image("/images/toLeft.png");
    private Image fadeImage = new Image("/images/fade.png");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fadeVolume.setMax(Math.PI / 2);
        fadeVolume.setMajorTickUnit(Math.PI/2);
        djService.setLeftSongList(leftPlaylist);
        djService.setRightSongList(rightPlaylist);
        djService.setDjController(this);
        fadeTimeSlider.setValue(djService.getTimeForNextSong());
        lb_fadeTimeValue.setText("0");
        setButtonImages();
        setListener();
    }

    public void stopAll() {
        djService.stop(true);
        djService.stop(false);
    }

    private void initLeftTable() {
        dj_left_tableView.setItems(FXCollections.observableArrayList(leftPlaylist));
        tc_left_artist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ARTIST).getName()));
        tc_left_artist.prefWidthProperty().bind(dj_left_tableView.widthProperty().multiply(0.35));
        tc_left_title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        tc_left_title.prefWidthProperty().bind(dj_left_tableView.widthProperty().multiply(0.45));
        tc_left_duration.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getLength() * 1 / (long) 60) + ":" + ((param.getValue().getLength() % 60) < 10 ? ("0" + Long.toString(param.getValue().getLength() % 60)) : (Long.toString(param.getValue().getLength() % 60)))));
        tc_left_duration.prefWidthProperty().bind(dj_left_tableView.widthProperty().multiply(0.2));
    }

    private void initRightTable() {
        dj_right_tableView.setItems(FXCollections.observableArrayList(rightPlaylist));
        tc_right_artist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ARTIST).getName()));
        tc_right_artist.prefWidthProperty().bind(dj_right_tableView.widthProperty().multiply(0.35));
        tc_right_title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        tc_right_title.prefWidthProperty().bind(dj_right_tableView.widthProperty().multiply(0.45));
        tc_right_duration.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getLength() * 1 / (long) 60) + ":" + ((param.getValue().getLength() % 60) < 10 ? ("0" + Long.toString(param.getValue().getLength() % 60)) : (Long.toString(param.getValue().getLength() % 60)))));
        tc_right_duration.prefWidthProperty().bind(dj_right_tableView.widthProperty().multiply(0.2));
    }

    public void setPlaylist(List<Song> playlistSongs) {
        this.allSongsFromPlaylist = playlistSongs;
        int i = 0;
        for(Song song : allSongsFromPlaylist) {
            if((i%2) == 1) {
                rightPlaylist.add(song);
                i++;
            } else {
                leftPlaylist.add(song);
                i++;
            }
        }
        initRightTable();
        initLeftTable();
    }

    private void setListener() {
        fadeTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            djService.setCrossfadeTime(newValue.intValue());
            lb_fadeTimeValue.setText(String.valueOf(( djService.getTimeForNextSong() / 1000)));
        });

        fadeVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            djService.setFadeVolume(newValue.doubleValue());
        });

        masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            djService.setMasterVolume(newValue.doubleValue());
        });

        djLeftDuration.valueProperty().addListener(observable -> {
            if (djLeftDuration.isValueChanging() && djService.getLeftSong() != null) {
                djService.getDJmediaPlayerONE().seek(djService.getDJmediaPlayerONE().getMedia().getDuration().multiply(djLeftDuration.getValue() / 100));
            }
        });

        djRightDuration.valueProperty().addListener(observable -> {
            if (djRightDuration.isValueChanging() && djService.getRightSong() != null) {
                djService.getDJmediaPlayerTWO().seek(djService.getDJmediaPlayerTWO().getMedia().getDuration().multiply(djRightDuration.getValue() / 100));
            }
        });
    }

    @FXML
    public void leftPlayPauseClicked(ActionEvent actionEvent) {
        if (leftPlaylist.size() != 0) {
            try {
                if (dj_left_tableView.getSelectionModel().isEmpty()) {
                    djService.setLeftSong(leftPlaylist.get(0));
                } else {
                    djService.setLeftSong(leftPlaylist.get(dj_left_tableView.getSelectionModel().getSelectedIndex()));
                }
                djService.play(true);
                djService.leftTimeDurationProperty().addListener(((observable, oldValue, newValue) -> {
                    updateTime(true);
                }));

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        else {
            dj_left_playPause_btn.setSelected(false);
        }
        dj_left_song_label.setText(djService.getLeftSong().getName());
    }

    @FXML
    public void rightPlayPauseClicked(ActionEvent actionEvent) {
        if (rightPlaylist.size()!=0) {
            try {
                if (dj_right_tableView.getSelectionModel().isEmpty()) {
                    djService.setRightSong(rightPlaylist.get(0));
                } else {
                    djService.setRightSong(rightPlaylist.get(dj_right_tableView.getSelectionModel().getSelectedIndex()));
                }
                djService.play(false);
                djService.rightTimeDurationProperty().addListener(((observable, oldValue, newValue) -> {
                    updateTime(false);
                }));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        else {
            dj_right_playPause_btn.setSelected(true);
        }
        dj_right_song_label.setText(djService.getRightSong().getName());
    }

    @FXML
    public void leftLastClicked(ActionEvent actionEvent) {
        if (validLeftCommand()) {
            try {
                djService.last(true);
                dj_left_song_label.setText(djService.getLeftSong().getName());
                dj_left_tableView.getSelectionModel().selectPrevious();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void leftNextClicked(ActionEvent actionEvent) {
        if (validLeftCommand()) {
            try {
                djService.next(true);
                dj_left_song_label.setText(djService.getLeftSong().getName());
                dj_left_tableView.getSelectionModel().selectNext();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void rightLastClicked(ActionEvent actionEvent) {
        if (validRightCommand()) {
            try {
                djService.last(false);
                dj_right_song_label.setText(djService.getRightSong().getName());
                dj_right_tableView.getSelectionModel().selectPrevious();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void rightStopClicked(ActionEvent actionEvent) {
        dj_right_playPause_btn.setSelected(false);
        djService.stop(false);
        dj_right_song_label.setText("");
    }

    @FXML
    public void leftStopClicked(ActionEvent actionEvent) {
        dj_left_playPause_btn.setSelected(false);
        djService.stop(true);
        dj_left_song_label.setText("");
    }

    @FXML
    public void rightNextClicked(ActionEvent actionEvent) {
        if (validRightCommand()) {
            try {
                djService.next(false);
                dj_right_song_label.setText(djService.getRightSong().getName());
                dj_right_tableView.getSelectionModel().selectNext();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void switchFromLeftToRight(ActionEvent actionEvent) {
        try {
            if(dj_left_tableView.getSelectionModel().isEmpty()) {
                return;
            }
            else {
                djService.switchSongFromPlaylist(leftPlaylist.get(dj_left_tableView.getSelectionModel().getSelectedIndex()));
                initRightTable();
                initLeftTable();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchFromRightToLeft(ActionEvent actionEvent) {
        try {
            if(dj_right_tableView.getSelectionModel().isEmpty()) {
                return;
            }
            else {
                djService.switchSongFromPlaylist(rightPlaylist.get(dj_right_tableView.getSelectionModel().getSelectedIndex()));
                initRightTable();
                initLeftTable();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private boolean validLeftCommand() {
        return !(DJServiceImpl.getDJmediaPlayerONE() == null || djService.getLeftSong() == null);
    }

    private boolean validRightCommand() {
        return !(DJServiceImpl.getDJmediaPlayerTWO() == null || djService.getRightSong() == null);
    }

    private void updateTime(boolean leftSide) {
        if (leftSide) {
            Platform.runLater(() -> {
                if (djService.getDJmediaPlayerONE() == null) {
                    lb_left_currentTime.setText("");
                    lb_left_duration.setText("");
                    djLeftDuration.setValue(0);
                    return;
                }
                Duration currentLeftTime = djService.getDJmediaPlayerONE().getCurrentTime();
                Duration leftDuration = djService.getDJmediaPlayerONE().getMedia().getDuration();
                lb_left_currentTime.setText(djService.currentTimeString(currentLeftTime, leftDuration));
                lb_left_duration.setText(djService.durationTimeString(leftDuration));
                if (leftDuration.greaterThan(Duration.ZERO) && !djLeftDuration.isValueChanging()) {
                    djLeftDuration.setValue(currentLeftTime.toMillis() / (leftDuration).toMillis() * 100);
                }
            });
        }
        else {
            Platform.runLater(() -> {
                if(djService.getDJmediaPlayerTWO() == null) {
                    lb_right_currentTime.setText("");
                    lb_right_duration.setText("");
                    djRightDuration.setValue(0);
                    return;
                }
                Duration currentRightTime = djService.getDJmediaPlayerTWO().getCurrentTime();
                Duration rightDuration = djService.getDJmediaPlayerTWO().getMedia().getDuration();
                lb_right_currentTime.setText(djService.currentTimeString(currentRightTime, rightDuration));
                lb_right_duration.setText(djService.durationTimeString(rightDuration));
                if (rightDuration.greaterThan(Duration.ZERO) && !djRightDuration.isValueChanging()) {
                    djRightDuration.setValue(currentRightTime.toMillis() / (rightDuration).toMillis() * 100);
                }
            });
        }
    }

    /**
     * Sets the Button-Images
     */

    private void setButtonImages() {
        setPlayPauseButtonImage();
        setStopButtonImage();
        setPreviousButtonImage();
        setNextButtonImage();
        setSwitchButtonImage();
        setFadeButtonImage();
    }

    private void setPlayPauseButtonImage() {
        ImageView toggleImageLeft = new ImageView();
        toggleImageLeft.setFitWidth(dj_left_playPause_btn.getPrefWidth());
        toggleImageLeft.setFitHeight(dj_left_playPause_btn.getPrefHeight());
        dj_left_playPause_btn.setPadding(Insets.EMPTY);
        dj_left_playPause_btn.setGraphic(toggleImageLeft);
        toggleImageLeft.imageProperty().bind(Bindings
                        .when(dj_left_playPause_btn.selectedProperty())
                        .then(pauseImage)
                        .otherwise(playImage)
        );

        ImageView toggleImageRight = new ImageView();
        toggleImageRight.setFitWidth(dj_right_playPause_btn.getPrefWidth());
        toggleImageRight.setFitHeight(dj_right_playPause_btn.getPrefHeight());
        dj_right_playPause_btn.setPadding(Insets.EMPTY);
        dj_right_playPause_btn.setGraphic(toggleImageRight);
        toggleImageRight.imageProperty().bind(Bindings
                        .when(dj_right_playPause_btn.selectedProperty())
                        .then(pauseImage)
                        .otherwise(playImage)
        );
    }

    private void setStopButtonImage() {
        ImageView imageViewLeft = new ImageView(stopImage);
        ImageView imageViewRight = new ImageView(stopImage);

        imageViewLeft.setFitWidth(dj_left_stop_btn.getPrefWidth());
        imageViewLeft.setFitHeight(dj_left_stop_btn.getPrefHeight());
        dj_left_stop_btn.setPadding(Insets.EMPTY);
        dj_left_stop_btn.setGraphic(imageViewLeft);

        imageViewRight.setFitWidth(dj_right_stop_btn.getPrefWidth());
        imageViewRight.setFitHeight(dj_right_stop_btn.getPrefHeight());
        dj_right_stop_btn.setPadding(Insets.EMPTY);
        dj_right_stop_btn.setGraphic(imageViewRight);
    }

    private void setPreviousButtonImage() {
        ImageView imageViewLeft = new ImageView(previousImage);
        ImageView imageViewRight = new ImageView(previousImage);

        imageViewLeft.setFitWidth(dj_left_previous_btn.getPrefWidth());
        imageViewLeft.setFitHeight(dj_left_previous_btn.getPrefHeight());
        dj_left_previous_btn.setPadding(Insets.EMPTY);
        dj_left_previous_btn.setGraphic(imageViewLeft);

        imageViewRight.setFitWidth(dj_right_previous_btn.getPrefWidth());
        imageViewRight.setFitHeight(dj_right_previous_btn.getPrefHeight());
        dj_right_previous_btn.setPadding(Insets.EMPTY);
        dj_right_previous_btn.setGraphic(imageViewRight);
    }

    private void setNextButtonImage() {
        ImageView imageViewLeft = new ImageView(nextImage);
        ImageView imageViewRight = new ImageView(nextImage);

        imageViewLeft.setFitWidth(dj_left_next_btn.getPrefWidth());
        imageViewLeft.setFitHeight(dj_left_next_btn.getPrefHeight());
        dj_left_next_btn.setPadding(Insets.EMPTY);
        dj_left_next_btn.setGraphic(imageViewLeft);

        imageViewRight.setFitWidth(dj_right_next_btn.getPrefWidth());
        imageViewRight.setFitHeight(dj_right_next_btn.getPrefHeight());
        dj_right_next_btn.setPadding(Insets.EMPTY);
        dj_right_next_btn.setGraphic(imageViewRight);
    }

    private void setSwitchButtonImage() {
        ImageView imageViewLeft = new ImageView(toRightImage);
        ImageView imageViewRight = new ImageView(toLeftImage);

        imageViewLeft.setFitWidth(dj_toRight_btn.getPrefWidth());
        imageViewLeft.setFitHeight(dj_toRight_btn.getPrefHeight());
        dj_toRight_btn.setPadding(Insets.EMPTY);
        dj_toRight_btn.setGraphic(imageViewLeft);

        imageViewRight.setFitWidth(dj_toLeft_btn.getPrefWidth());
        imageViewRight.setFitHeight(dj_toLeft_btn.getPrefHeight());
        dj_toLeft_btn.setPadding(Insets.EMPTY);
        dj_toLeft_btn.setGraphic(imageViewRight);
    }

    private void setFadeButtonImage() {
        ImageView fImage = new ImageView(fadeImage);

        fImage.setFitWidth(dj_fade_btn.getPrefWidth());
        fImage.setFitHeight(dj_fade_btn.getPrefHeight());
        dj_fade_btn.setPadding(Insets.EMPTY);
        dj_fade_btn.setGraphic(fImage);
    }

    @FXML
    public void fadeSong(ActionEvent actionEvent) {

        int leftSongIndex = dj_left_tableView.getSelectionModel().getSelectedIndex();
        int rightSongIndex = dj_right_tableView.getSelectionModel().getSelectedIndex();

        if(leftSongIndex < 0) {
            dj_left_tableView.getSelectionModel().select(0);
            leftSongIndex = 0;
        }
        if(rightSongIndex < 0) {
            dj_right_tableView.getSelectionModel().select(0);
            rightSongIndex = 0;
        }
        try {
            if(djService.getDJmediaPlayerONE() != null &&
                    djService.getDJmediaPlayerTWO() == null) {
                djService.fade(leftPlaylist.get(leftSongIndex), rightPlaylist.get(rightSongIndex));
            }
            else if(djService.getDJmediaPlayerTWO() != null &&
                    djService.getDJmediaPlayerONE() == null) {
                djService.fade(leftPlaylist.get(leftSongIndex), rightPlaylist.get(rightSongIndex));
            }
        }
        catch(ServiceException serviceException) {
            logger.error(serviceException);
            showErrorDialog("Errorn on fading", serviceException.getMessage());
        }
    }

    private void showErrorDialog(String message, Exception e) {
        showErrorDialog(message, e.getMessage());
    }

    private void showErrorDialog(String message, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    public Slider getFadeVolume() {
        return fadeVolume;
    }

    public void setFadeVolume(Slider fadeVolume) {
        this.fadeVolume = fadeVolume;
    }


    public TableView getDj_left_tableView() {
        return dj_left_tableView;
    }

    public TableView getDj_right_tableView() {
        return dj_right_tableView;
    }

    public ToggleButton getDj_left_playPause_btn() {
        return dj_left_playPause_btn;
    }

    public ToggleButton getDj_right_playPause_btn() {
        return dj_right_playPause_btn;
    }

}