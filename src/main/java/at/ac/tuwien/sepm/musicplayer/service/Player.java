package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Collection;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface Player {

    void initialize();

    /**
     * starts playing the active song
     * @param song the song to play
     */
    void play(Song song);

    /**
     * pauses the playback
     */
    void pause();

    /**
     * stops the playback.
     */
    void stop();

    /**
     * goes to the specified time in the currently active song
     * @param seekTime
     */
    void seekPosition(Duration seekTime);
    /**
     *
     */
    void playMode();

    /**
     * jumps to the next track in the active playlist
     */
    void next();

    /**
     * jumps to the previous track in the active playlist
     */
    void previous();

    /**
     * gets a list of songs from the active playlist
     * @return a list of songs from the active playlist
     */
    List<Song> getActivePlaylist();

    /**
     * gets the song which is currently active
     * @return the active song
     */
    Song getActiveSong();

    /**
     * sets the the current active song
     * @param activeSong
     */
    void setActiveSong(Song activeSong);

    /**
     * the position of the active song in the active playlist
     * @return the position of the active song
     */
    Integer getActiveSongPosition();

    /**
     * sets the position of the active song
     * @param activeSongPosition
     */
    void setActiveSongPosition(Integer activeSongPosition);

    /**
     * sets the the current active playlist
     * @param activePlaylist
     */
    void setActivePlaylist(List<Song> activePlaylist);

    /**
     * gets media player
     * @return media player
     */
    MediaPlayer getMediaPlayer();

    /**
     * gets the current media time
     * @return the current media time
     */
    Duration getCurrentTime();

    /**
     * gets the duration of the media
     * @return the duration of the media
     */
    Duration getDuration();

    /**
     * returns the value of currentTimeDurationProperty
     * @return
     */
    DoubleProperty currentTimeDurationProperty();

    /**
     * returns the value of stopRequestedProperty
     * @return
     */
    BooleanProperty stopRequestedProperty();

    /**
     * returns the value of mediaChangedProperty
     * @return
     */
    StringProperty mediaChangedProperty();

    /**
     *
     * sets specific volume power
     */
    void setPower(double volume);

    /**
     *
     * sets specific volume power
     */
    double getPower();

    /**
     * returns the active playlist id
     * @return id of active playlist
     */
    int getActivePlaylistId();

    /**
     * sets the id of active playlist
     * @param activePlaylistId
     */
    void setActivePlaylistId(int activePlaylistId);

    /**
     * shows whether playMode is repeat or not
     * @return true if playMode is repeat or false if not
     */
    boolean isRepeat();

    /**
     * sets repeat of true if play mode is repeat or false if not
     * @param repeat
     */
    void setRepeat(boolean repeat);

    /**
     * shows whether playMode is repeatAll or not
     * @return true if playMode is repeatAll or false if not
     */
    boolean isRepeatAll();

    /**
     * sets repeatAll of true if playMode is repeatAll or false if not
     * @param repeatAll
     */
    void setRepeatAll(boolean repeatAll);

    /**
     * shows whether playMode is normal or not
     * @return true if playMode is normal or false if not
     */
    boolean isNormal();

    /**
     * sets normal of true if playMode is normal or false if not
     * @param normal
     */
    void setNormal(boolean normal);

    /**
     * shows whether playMode is shuffle or not
     * @return true if playMode is shuffle or false if not
     */
    boolean isShuffle();

    /**
     * sets shuffle of true if playMode is shuffle or false if not
     * @param shuffle
     */
    void setShuffle(boolean shuffle);

    void setShuffleList(List<Integer> shuffleList);
}
