package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.presentation.DJMainController;
import at.ac.tuwien.sepm.musicplayer.service.DJService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Salmir on 04.06.2015.
 */
@Service
public class DJServiceImpl implements DJService {

    private static final Logger logger = Logger.getLogger(DJServiceImpl.class);
    private static MediaPlayer DJmediaPlayerONE, DJmediaPlayerTWO;
    private Song leftSong;
    private Song leftDuration;

    private Integer timeForNextSong; // fading-time in milliseconds.
    private List<Song> leftSongList, rightSongList;
    private boolean leftPause, rightPause;
    private double fadeVolume, masterVolume;
    private DoubleProperty leftTimeDuration;
    private DoubleProperty rightTimeDuration;


    private DJMainController djController;

    public DJServiceImpl() {
        timeForNextSong = 0;
        fadeVolume = 0.0;
        masterVolume = 0.5;
        leftTimeDuration = new SimpleDoubleProperty(0);
        rightTimeDuration = new SimpleDoubleProperty(0);
    }

    @Override
    public MediaPlayer play(boolean leftSide) throws ServiceException {
        if (leftSide) {
            if (leftSong == null) {
                throw new ServiceException("please select a song from the left list!");
            }
            else if (DJmediaPlayerONE == null) {
                DJmediaPlayerONE = new MediaPlayer(new Media(leftSong.getFile().toURI().toString()));
                DJmediaPlayerONE.setVolume(Math.cos(fadeVolume));
                DJmediaPlayerONE.play();
                leftPause = false;
                DJmediaPlayerONE.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        try {
                            if(DJmediaPlayerONE != null &&
                                    DJmediaPlayerONE.getMedia().durationProperty().getValue() != null) {
                                if (newValue.compareTo(
                                        DJmediaPlayerONE.getMedia().durationProperty().getValue()
                                                .subtract(Duration.millis(timeForNextSong))) >= 0) {
                                    fade(leftSong, leftDuration);
                                }
                            }
                        } catch (ServiceException serviceExceptoi) {
                            logger.error(serviceExceptoi.getMessage());
                        }
                    }
                });
            }
            else if (leftPause) {
                DJmediaPlayerONE.play();
                leftPause = false;
            }
            else {
                pause(true);
                leftPause = true;
            }
            DJmediaPlayerONE.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
                leftTimeDuration.set((DJmediaPlayerONE == null) ? 0.0 : DJmediaPlayerONE.currentTimeProperty().get().toSeconds());
            }));
        }

        if (!leftSide) {
            if (leftDuration == null) {
                throw new ServiceException("please select a song from the right list!");
            }
            else if (DJmediaPlayerTWO == null) {
                DJmediaPlayerTWO = new MediaPlayer(new Media(leftDuration.getFile().toURI().toString()));
                DJmediaPlayerTWO.setVolume(Math.sin(fadeVolume));
                DJmediaPlayerTWO.play();
                rightPause = false;
                DJmediaPlayerTWO.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        try {
                            if(DJmediaPlayerTWO != null &&
                                    DJmediaPlayerTWO.getMedia().durationProperty().getValue() != null) {
                                if (newValue.compareTo(
                                        DJmediaPlayerTWO.getMedia().durationProperty().getValue()
                                                .subtract(Duration.millis(timeForNextSong))) >= 0) {
                                    fade(leftSong, leftDuration);
                                }
                            }
                        }
                        catch(ServiceException serviceExceptoi) {
                            logger.error(serviceExceptoi.getMessage());
                        }
                    }
                });
            }
            else if (rightPause) {
                DJmediaPlayerTWO.play();
                rightPause = false;
            }
            else {
                pause(false);
                rightPause = true;
            }
            DJmediaPlayerTWO.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
                rightTimeDuration.set((DJmediaPlayerTWO == null) ? 0.0 : DJmediaPlayerTWO.currentTimeProperty().get().toSeconds());
            }));
        }
        return leftSide ? DJmediaPlayerONE : DJmediaPlayerTWO;
    }

    @Override
    public void pause(boolean leftSide) throws ServiceException {
        if (leftSide && DJmediaPlayerONE != null) {
            DJmediaPlayerONE.pause();
        }
        else if (DJmediaPlayerTWO != null) {
            DJmediaPlayerTWO.pause();
        }
        else {
            logger.debug("No playing song found to pause!");
            throw new ServiceException("No playing song found to pause!");
        }
    }

    @Override
    public void stop(boolean leftSide) {
        if (leftSide) {
            if (leftSong != null && DJmediaPlayerONE != null) {
                DJmediaPlayerONE.stop();
                leftPause = false;
                DJmediaPlayerONE = null;
            }
        }
        if (!leftSide) {
            if (leftDuration != null && DJmediaPlayerTWO != null) {
                DJmediaPlayerTWO.stop();
                rightPause = false;
                DJmediaPlayerTWO = null;
            }
        }
    }

    @Override
    public void next(boolean leftSide) throws ServiceException {
        if (leftSide) {
            if (DJmediaPlayerONE != null) {
                if (DJmediaPlayerONE.getStatus() == MediaPlayer.Status.PLAYING) {
                    stop(true);
                }
                if (leftSongList.size() == 0 || leftSongList == null) {
                    throw new ServiceException("left songlist is empty!");
                }
                else if (leftSongList.indexOf(leftSong) >= leftSongList.size() - 1) {
                    leftSong = leftSongList.get(0);
                }
                else {
                    leftSong = leftSongList.get(leftSongList.indexOf(leftSong) + 1);
                }
                play(true);
            }
        }
        if (!leftSide) {
            if (DJmediaPlayerTWO != null) {
                if (DJmediaPlayerTWO.getStatus() == MediaPlayer.Status.PLAYING) {
                    stop(false);
                }
                if (rightSongList.size() == 0 || rightSongList == null) {
                    throw new ServiceException("right songlist is empty!");
                }
                else if (rightSongList.indexOf(leftDuration) >= rightSongList.size() - 1) {
                    leftDuration = rightSongList.get(0);
                }
                else {
                    leftDuration = rightSongList.get(rightSongList.indexOf(leftDuration) + 1);
                }
                play(false);
            }
        }
    }

    @Override
    public void last(boolean leftSide) throws ServiceException {
        if (leftSide) {
            if (DJmediaPlayerONE != null) {
                if (DJmediaPlayerONE.getStatus() == MediaPlayer.Status.PLAYING) {
                    stop(true);
                }
                if (leftSongList.size() == 0 || leftSongList == null) {
                    throw new ServiceException("left songlist is empty!");
                }
                else if (leftSongList.indexOf(leftSong) == 0) {
                    stop(true);
                }
                else {
                    leftSong = leftSongList.get(leftSongList.indexOf(leftSong) - 1);
                }
                play(true);
            }
        }
        if (!leftSide) {
            if (DJmediaPlayerTWO != null) {
                if (DJmediaPlayerTWO.getStatus() == MediaPlayer.Status.PLAYING) {
                    stop(false);
                }
                if (rightSongList.size() == 0 || rightSongList == null) {
                    throw new ServiceException("right songlist is empty!");
                } else if (rightSongList.indexOf(leftDuration) == 0) {
                    stop(false);
                } else {
                    leftDuration = rightSongList.get(rightSongList.indexOf(leftDuration) - 1);
                }
                play(false);
            }
        }
    }

    @Override
    public void switchSongFromPlaylist(Song song) throws ServiceException {
        if(leftSongList.contains(song)) {
            leftSongList.remove(song);
            rightSongList.add(song);
        }
        else if (rightSongList.contains(song)) {
            rightSongList.remove(song);
            leftSongList.add(song);
        }
        else {
            throw new ServiceException("please select a song!");
        }
    }

    @Override
    public void fade(Song songLeft, Song songRight) throws ServiceException {

        leftSong = songLeft;
        leftDuration = songRight;

        if(DJmediaPlayerONE == null && DJmediaPlayerTWO != null) {
            fadeOut(false);
            //DJmediaPlayerONE = new MediaPlayer(new Media(songLeft.getFile().toURI().toString()));
            fadeIn(true);
        }
        if(DJmediaPlayerTWO == null && DJmediaPlayerONE != null) {
            fadeOut(true);
            //DJmediaPlayerTWO = new MediaPlayer(new Media(songRight.getFile().toURI().toString()));
            fadeIn(false);
        }
    }

    @Override
    public void setVolume() {
        if (DJmediaPlayerONE != null) {
            DJmediaPlayerONE.setVolume(masterVolume * (Math.cos(fadeVolume)));
        }
        if (DJmediaPlayerTWO != null) {
            DJmediaPlayerTWO.setVolume(masterVolume * (Math.sin(fadeVolume)));
        }
    }

    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume = masterVolume;
        setVolume();
    }

    public void setLeftSongList(List<Song> list) {
        this.leftSongList = list;
    }

    public void setRightSongList(List<Song> list) {
        this.rightSongList = list;
    }

    public Song getLeftSong() {
        return leftSong;
    }

    public void setLeftSong(Song leftSong) {
        this.leftSong = leftSong;
    }

    public Song getRightSong() {
        return leftDuration;
    }

    public void setRightSong(Song rightSong) {
        this.leftDuration = rightSong;
    }

    public Integer getTimeForNextSong() {
        return timeForNextSong;
    }

    public void setTimeForNextSong(Integer timeForNextSong) {
        this.timeForNextSong = timeForNextSong;
    }

    public double getFadeVolume() {
        return fadeVolume;
    }

    public void setFadeVolume(double fadeVolume) {
        this.fadeVolume = fadeVolume;
        setVolume();
    }

    public static MediaPlayer getDJmediaPlayerTWO() {
        return DJmediaPlayerTWO;
    }

    public static void setDJmediaPlayerTWO(MediaPlayer DJmediaPlayerTWO) {
        DJServiceImpl.DJmediaPlayerTWO = DJmediaPlayerTWO;
    }

    public static MediaPlayer getDJmediaPlayerONE() {
        return DJmediaPlayerONE;
    }

    public static void setDJmediaPlayerONE(MediaPlayer DJmediaPlayerONE) {
        DJServiceImpl.DJmediaPlayerONE = DJmediaPlayerONE;
    }

    public void setCrossfadeTime(Integer time) {
        this.timeForNextSong = time;
    }

    private void fadeOut(boolean leftSide) {

        Duration duration = Duration.millis(timeForNextSong);
        if(duration.toMillis() == 0) {
            duration = duration.add(Duration.millis(10));
        }
        Timeline fadeOut = new Timeline(
                new KeyFrame(duration,
                        new KeyValue(leftSide? DJmediaPlayerONE.volumeProperty() : DJmediaPlayerTWO.volumeProperty(), 0.0)));

        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(leftSide) {
                    djController.leftStopClicked(null);
                    djController.getDj_left_tableView().getSelectionModel().selectNext();
                }
                else {
                    djController.rightStopClicked(null);
                    djController.getDj_right_tableView().getSelectionModel().selectNext();
                }
            }
        });
        fadeOut.play();
    }

    private void fadeIn(boolean leftSide) throws ServiceException{

        if(leftSide) {
            djController.leftPlayPauseClicked(null);
            djController.getDj_left_playPause_btn().setSelected(true);
        }
        else {
            djController.rightPlayPauseClicked(null);
            djController.getDj_right_playPause_btn().setSelected(true);
        }
        Duration duration = Duration.millis(timeForNextSong);
        if(duration.toMillis() == 0) {
            duration = duration.add(Duration.millis(10));
        }
        Timeline fadeIn = new Timeline(
                new KeyFrame(duration,
                        new KeyValue(djController.getFadeVolume().valueProperty(),
                                leftSide ? 0.0 : djController.getFadeVolume().getMax())));
        fadeIn.play();
    }

    public void setDjController(DJMainController djController) {
        this.djController = djController;
    }


    public double getLeftTimeDuration() {
        return leftTimeDuration.get();
    }

    public DoubleProperty leftTimeDurationProperty() {
        return leftTimeDuration;
    }

    public void setLeftTimeDuration(double leftTimeDuration) {
        this.leftTimeDuration.set(leftTimeDuration);
    }

    public double getRightTimeDuration() {
        return rightTimeDuration.get();
    }

    public DoubleProperty rightTimeDurationProperty() {
        return rightTimeDuration;
    }

    public void setRightTimeDuration(double rightTimeDuration) {
        this.rightTimeDuration.set(rightTimeDuration);
    }

    public String currentTimeString(Duration currentTime, Duration duration) {
        int intCurrentTime = (int) Math.floor(currentTime.toSeconds());
        int intDuration = (int) Math.floor(duration.toSeconds());
        int currentTimeHour, currentTimeMin, currentTimeSec;

        if (intDuration / 3600 > 0) {
            currentTimeHour = intCurrentTime / 3600;
            currentTimeMin = (intCurrentTime % 3600) / 60;
            currentTimeSec = intCurrentTime % 60;
            return String.format("%d:%02d:%02d", currentTimeHour, currentTimeMin, currentTimeSec);

        } else {
            currentTimeMin = (intCurrentTime % 3600) / 60;
            currentTimeSec = intCurrentTime % 60;
            return String.format("%02d:%02d", currentTimeMin, currentTimeSec);
        }
    }

    public String durationTimeString(Duration duration) {
        int intDuration = (int) Math.floor(duration.toSeconds());
        int durationHour, durationMin, durationSec;

        if (intDuration / 3600 > 0) {
            durationHour = intDuration / 3600;
            durationMin = (intDuration % 3600) / 60;
            durationSec = intDuration % 60;
            return String.format("%d:%02d:%02d", durationHour, durationMin, durationSec);
        }
        else {
            durationMin = (intDuration % 3600) / 60;
            durationSec = intDuration % 60;
            return String.format("%02d:%02d", durationMin, durationSec);
        }
    }
}