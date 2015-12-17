package at.ac.tuwien.sepm.musicplayer.service.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.presentation.PlayerMainController;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.Player;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefan with love on 29.06.2015.
 */
@Service
public class PlayerMainControllerSongService {
    private static Logger logger = Logger.getLogger(PlayerMainControllerSongService.class);

    @Autowired
    private PlaylistService playlistService;

    public void changeSongPosition(PlayerMainController mainController, TableRow<Song> row) {
        row.setOnDragOver(ev -> {
            if (ev.getDragboard().hasContent(mainController.INDEX)) {
                if (row.getIndex() != ((Integer) ev.getDragboard().getContent(mainController.INDEX))) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    ev.consume();
                }
            }
        });

        row.setOnDragDropped(ev -> {
            Dragboard db = ev.getDragboard();
            boolean success = false;
            if (db.hasContent(mainController.INDEX)) {
                int draggedIndex = (Integer) db.getContent(mainController.INDEX);
                Song draggedSong = mainController.getTableViewPlaylist().getItems().remove(draggedIndex);
                int dropIndex;

                if (row.isEmpty()) {
                    dropIndex = mainController.getTableViewPlaylist().getItems().size();
                } else {
                    dropIndex = row.getIndex();
                }
                mainController.getTableViewPlaylist().getItems().add(dropIndex, draggedSong);

                Playlist selectedPlaylist = mainController.getListv_playlists().getSelectionModel().getSelectedItem();
                try {
                    // update song position in the playlist after drop
                    playlistService.updateSongsInPlaylist(selectedPlaylist.getId(),
                            convertAllSongsFromTableView(mainController.getTableViewPlaylist().getItems(), mainController.getLibrary()));
                } catch (ValidationException | ServiceException e) {
                    logger.info(e.getMessage());
                }
                success = true;

                // if selected playlist is active playlist, must active playlist
                // and active song position to be re-set after drop
                if (selectedPlaylist.getId() == mainController.getPlayer().getActivePlaylistId()) {
                    mainController.getPlayer().setActivePlaylist(mainController.getTableViewPlaylist().getItems());
                    if (draggedIndex < mainController.getPlayer().getActiveSongPosition() && dropIndex > mainController.getPlayer().getActiveSongPosition()) {
                        mainController.getPlayer().setActiveSongPosition(mainController.getPlayer().getActiveSongPosition() - 1);
                    } else if (draggedIndex > mainController.getPlayer().getActiveSongPosition() && dropIndex < mainController.getPlayer().getActiveSongPosition()) {
                        mainController.getPlayer().setActiveSongPosition(mainController.getPlayer().getActiveSongPosition() + 1);
                    } else if (draggedIndex == mainController.getPlayer().getActiveSongPosition()) {
                        mainController.getPlayer().setActiveSongPosition(dropIndex);
                    }
                }
            }
            ev.setDropCompleted(success);
            ev.consume();
        });
    }

    /**
     * For each song in the selected playlist it will returnd a Entitiy-Type of this song
     *
     * //@param selectedPlaylist contains SongDTO Objects which should "replaced" with Entity-Objects
     * @return a list with Song-Entity Objects
     */
    public List<Song> convertAllSongsFromPlaylist(Playlist selectedPlaylist, Library library) {
        Map<String, Song> songMap = library.getSongMap();
        List<Song> songEntityFromPlaylist = new LinkedList<>();
        try {
            List<SongDTO> songDTO = playlistService.getSongs(selectedPlaylist.getId());
            songDTO.forEach(SongDTO ->{
                Song song = songMap.get(SongDTO.getIdentifier());
                songEntityFromPlaylist.add(song);
            });
        } catch (ValidationException | ServiceException e) {
            logger.error(e.getMessage());
        }
        return songEntityFromPlaylist;
    }

    /**
     * For each song in the selected tableView it will returned a Entity-Type of this song
     *
     * @param songs
     * @return
     */
    public List<SongDTO> convertAllSongsFromTableView(List<Song> songs, Library library) {
        List<SongDTO> songsToInsert = new ArrayList<>();
        songs.forEach(song -> {
            SongDTO songDTO = new SongDTO(song.getName(), song.getFile().getAbsolutePath());
            songDTO.setId(song.getId());
            songsToInsert.add(songDTO);
        });
        return songsToInsert;
    }
}
