package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.presentation.DJMainController;
import javafx.scene.media.MediaPlayer;

/**
 * This interface contains the most important methods for the DJ-Player.
 * Your implementation have to initialize two media player objects and two song (Entity)
 * objects for the fading, to play the songs at the same time, to swap them from
 * a list to the other and to control the volume for each playing song.
 *
 * Created by Salmir on 04.06.2015.
 */
public interface DJService {

    /**
     * Try to move a song from a playlist into an other.
     * Depending which button (left or right) was clicked, the selected song will
     * move on the other side. The list from the old position must not be empty!
     * In this case an exception will be thrown.
     *
     * @param song that should change the list.
     * @throws ServiceException when the list is empty or the song is not in any list!
     */
    void switchSongFromPlaylist(Song song) throws ServiceException;

    /**
     * Try to start the next song before the first song ends.
     * The time t is set by a local variable and the next song will start at
     * (songFirst.getDuration - t). If there are invalid time values or there is no
     * next or first song, this method will throw an Exception.
     *
     * @param songFirst is the first song that is now playing.
     * @param songNext is the next song that will start in (songFirst.Duration-t) seconds.
     * @throws ServiceException when any song is invalid or null, or the time is invalid!
     */
    void fade(Song songFirst, Song songNext) throws ServiceException;

    /**
     * Depending on the boolean parameter, this method try to start playing a media loaded
     * into a mediaplayer. The song (with its path) was set by selecting a song from the list
     * or the first song (if the list is not empty) in the list will start to play.
     *
     * @param leftSide if true: left song and media player selected, else: right side.
     * @throws ServiceException if any list is empty or the song is not valid!
     */
    MediaPlayer play(boolean leftSide) throws ServiceException;

    /**
     * Depending on the boolean parameter, this method will try to pause the playing song
     * on the left or on the right side of the Player. If the selected side has no valid
     * mediaplayer or song, this method will throw an exception.
     *
     * @param leftSide if true: left song and media player selected, else: right side.
     * @throws ServiceException if there is no song playing on this side!
     */
    void pause(boolean leftSide) throws ServiceException;

    /**
     * Depending on the boolean parameter, this method will stop the playing song on the left
     * or on the right side of the Player. This method will be used too if the DJ player stage
     * will be closed to stop playing songs.
     * The song will be stopped, but the media player will be set to null.
     *
     * @param leftSide if true: left song and media player selected, else: right side.
     */
    void stop(boolean leftSide);

    /**
     * Depending on the boolean parameter, this method will try to switch to the next song
     * in the playlist on the selected side. If the actual song is the last one, the list
     * will start again with the first position.
     *
     * @param leftSide if true: left song and playlist selected, else: right side.
     * @throws ServiceException if the selected playlist is empty!
     */
    void next(boolean leftSide) throws ServiceException;

    /**
     * Depending on the boolean parameter, this method will try to switch to the last song
     * in the playlist on the selected side. If the actual song is the first one, the first
     * song will start again from the beginning.
     *
     * @param leftSide if true: left song and playlist selected, else: right side.
     * @throws ServiceException if the selected playlist is empty!
     */
    void last(boolean leftSide) throws ServiceException;

    /**
     * Sets the volume of the selected media player as product of master-volume and side-volume
     * left Mediaplayer.setVolume = (masterVolume * leftVolume) or
     * right Mediaplayer.setVolume = (masterVolume * rightVolume).
     *
     * All values are Double values between 0.0 and 1.0. Only the combination of
     * masterVolume = 1.0 and sideVolume = 1.0 will get the maximum.
     */
    void setVolume();

    /**
     * @return The fading time witch is set in DJ Mode
     */
    Integer getTimeForNextSong();


}
