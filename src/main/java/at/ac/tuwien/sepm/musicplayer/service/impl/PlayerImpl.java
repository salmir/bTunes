package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.service.Player;
import at.ac.tuwien.sepm.musicplayer.service.StatisticService;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by marjaneh.
 */
@Service
@Configurable
public class PlayerImpl implements Player {

    private static Logger logger = Logger.getLogger(PlayerImpl.class);

    private MediaPlayer mediaPlayer;
    private Song activeSong;
    private Integer activeSongPosition;
    private boolean pause;
    private boolean repeat;
    private boolean repeatAll;
    private boolean normal;
    private boolean shuffle;
    private List<Song> activePlaylist;
    private List<Integer> shuffleList;
    private DoubleProperty currentTimeDuration;
    private BooleanProperty stopRequested;
    private StringProperty mediaChanged;
    private DoubleProperty power;
    private int activePlaylistId;
    private double volume;

    @Autowired
    StatisticService statisticService;

    @PostConstruct
    public void initialize() {
        activeSong = null;
        activeSongPosition = null;
        pause = false;
        repeat = false;
        shuffle = false;
        power = null;
        volume = 0.5;
    }

    public PlayerImpl() {
        this.activeSong = null;
        this.activeSongPosition = null;
        this.pause = false;
        this.repeat = false;
        this.repeatAll = false;
        this.normal = true;
        this.shuffle = false;
        this.currentTimeDuration = new SimpleDoubleProperty(0);
        this.stopRequested = new SimpleBooleanProperty(false);
        this.mediaChanged = new SimpleStringProperty("");
        this.power = new SimpleDoubleProperty(0.5);
    }

    @Override
    public void play(Song song) {
        if (mediaPlayer == null || (!pause && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING)) {
            mediaPlayer = new MediaPlayer(new Media(song.getFile().toURI().toString()));
            stopRequested.setValue(false);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();
            logger.info("media palyer set to play");

            mediaPlayer.statusProperty().addListener(new ChangeListener<MediaPlayer.Status>() {
                @Override
                public void changed(ObservableValue<? extends MediaPlayer.Status> observable, MediaPlayer.Status oldValue, MediaPlayer.Status newValue) {
                    if (newValue == MediaPlayer.Status.PLAYING) {
                        mediaChanged.setValue(activeSong.getName());
                        try {
                            statisticService.persist(new StatisticDTO(song.getId(), new Date()));
                        } catch (ServiceException | ValidationException serviceException) {
                            logger.error(serviceException.getMessage());
                        }
                    }
                }
            });

        }  else if (pause) {
            pause = false;
            mediaPlayer.play();
        } else {
            pause();
        }

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            currentTimeDuration.set((mediaPlayer == null) ? 0.0 : mediaPlayer.currentTimeProperty().get().toSeconds());
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            logger.info("at end of Song");
            stopRequested.setValue(true);
            playMode();
        });

    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            pause = true;
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            pause = false;
            mediaPlayer = null;
            mediaChanged.setValue("");
        }
    }

    @Override
    public void seekPosition(Duration seekTime) {
        mediaPlayer.seek(seekTime);

    }

    @Override
    public void playMode() {
        List<Song> songsList = getActivePlaylist();
        Song song = null;
        if (shuffle) {
            logger.info("Shuffle");
            song = getNextSongFromShuffleList(songsList);

        } else if (repeat) {
            logger.info("repeat");
            song = songsList.get(activeSongPosition);

        }  else if (repeatAll) {
            logger.info("repeatAll");
            if (activeSongPosition < songsList.size() - 1) {
                song = songsList.get(activeSongPosition + 1);
                activeSongPosition = activeSongPosition + 1;
            } else {
                song = songsList.get(0);
                setActiveSongPosition(0);
            }

        } else {
            logger.info("Normal");
            if (activeSongPosition < songsList.size() - 1) {
                song = songsList.get(activeSongPosition + 1);
                activeSongPosition = activeSongPosition + 1;
            }
        }

        if (song == null) {
            reset(songsList);
            return;
        }

        setActiveSong(song);
        play(song);
    }

    @Override
    public void next() {
        if(mediaPlayer != null) {
            List<Song> songsList = getActivePlaylist();
            Song song = null;

            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                stop();
            }

            if (shuffle) {
                song = getNextSongFromShuffleList(songsList);
            } else {
                if (activeSongPosition < songsList.size() - 1) {
                    song = songsList.get(activeSongPosition + 1);
                    activeSongPosition = activeSongPosition + 1;
                    if (pause) {
                        stop();
                        setActiveSongPosition(activeSongPosition);
                        setActiveSong(song);
                        return;
                    }
                } else if (repeatAll) {
                    song = songsList.get(0);
                    setActiveSongPosition(0);
                }
            }

            if (song == null) {
                reset(songsList);
                return;
            }

            setActiveSong(song);
            play(song);
        }
    }

    @Override
    public void previous() {
        if(mediaPlayer != null) {
            List<Song> songsList = getActivePlaylist();
            Song song = null;

            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                stop();
            }

            if (shuffle) {
                song = getNextSongFromShuffleList(songsList);
            } else {
                if (activeSongPosition > 0) {
                    song = songsList.get(activeSongPosition - 1);
                    activeSongPosition = activeSongPosition - 1;
                    if (pause) {
                        stop();
                        setActiveSongPosition(activeSongPosition);
                        setActiveSong(song);
                        return;
                    }
                } else if (repeatAll) {
                    song = songsList.get(songsList.size() - 1);
                    setActiveSongPosition(songsList.size() - 1);
                }
            }

            if (song == null) {
                reset(songsList);
                return;
            }

            setActiveSong(song);
            play(song);
        }
    }

    private void reset(List<Song> songsList) {
        stopRequested.setValue(true);
        //stop();
        shuffleList = null;
        setActiveSong(songsList.get(0));
        setActiveSongPosition(0);
    }

    private Song getNextSongFromShuffleList(List<Song> songsList) {
        Song song = null;

        if (shuffleList == null) {
            shuffleList = new ArrayList<>();
            for (int i = 0; i < songsList.size(); i++) {
                shuffleList.add(i);
            }

            shuffleList.remove(activeSongPosition);
        }

        if (shuffleList.size() > 0) {
            Random random = new Random();

            int i = random.nextInt(shuffleList.size());
            activeSongPosition = shuffleList.get(i);

            song = songsList.get(activeSongPosition);
            shuffleList.remove(activeSongPosition);
        } else if ((repeat || repeatAll)) {
            for (int i = 0; i < songsList.size(); i++) {
                shuffleList.add(i);
            }
            song = getNextSongFromShuffleList(songsList);
        }
        return song;
    }

    @Override
    public List<Song> getActivePlaylist() {
        return activePlaylist;
    }

    @Override
    public void setActivePlaylist(List<Song> activePlaylist) {
        this.activePlaylist = activePlaylist;
    }

    @Override
    public Song getActiveSong() {
        return activeSong;
    }

    @Override
    public void setActiveSong(Song activeSong) {
        this.activeSong = activeSong;
    }

    @Override
    public Integer getActiveSongPosition() {
        return activeSongPosition;
    }

    @Override
    public void setActiveSongPosition(Integer activeSongPosition) {
        this.activeSongPosition = activeSongPosition;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public Duration getCurrentTime() {
        return mediaPlayer.getCurrentTime();
    }

    @Override
    public Duration getDuration() {
        return mediaPlayer.getMedia().getDuration();
    }

    @Override
    public DoubleProperty currentTimeDurationProperty() {
        return currentTimeDuration;
    }

    @Override
    public BooleanProperty stopRequestedProperty() {
        return stopRequested;
    }

    @Override
    public StringProperty mediaChangedProperty() {
        return mediaChanged;
    }

    @Override
    public void setPower(double volume) {
        this.volume = volume;
        mediaPlayer.setVolume(volume);
    }

    @Override
    public double getPower(){
        return mediaPlayer.getVolume();
    }

    @Override
    public int getActivePlaylistId(){
        return activePlaylistId;
    }

    @Override
    public void setActivePlaylistId(int activePlaylistId) {
        this.activePlaylistId = activePlaylistId;
    }

    @Override
    public boolean isRepeat() {
        return repeat;
    }

    @Override
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    @Override
    public boolean isRepeatAll() {
        return repeatAll;
    }

    @Override
    public void setRepeatAll(boolean repeatAll) {
        this.repeatAll = repeatAll;
    }

    @Override
    public boolean isNormal() {
        return normal;
    }

    @Override
    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    @Override
    public boolean isShuffle() {
        return shuffle;
    }

    @Override
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    @Override
    public void setShuffleList(List<Integer> shuffleList) {
        this.shuffleList = shuffleList;
    }
}
