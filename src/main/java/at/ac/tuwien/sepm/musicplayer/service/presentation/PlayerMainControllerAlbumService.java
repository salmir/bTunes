package at.ac.tuwien.sepm.musicplayer.service.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.presentation.PlayerMainController;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.Player;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import at.ac.tuwien.sepm.musicplayer.service.retriever.AlbumCoverLastFMRetriever;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stefan with love on 28.06.2015.
 */

@Service
public class PlayerMainControllerAlbumService {

    private static Logger logger = Logger.getLogger(PlayerMainControllerAlbumService.class);

    @Autowired
    private AlbumService albumService;
    @Autowired
    private PlaylistService playlistService;

    /**
     * Set the album cover for the now-playing song
     */
    public Image setAlbumCover(Player playerService, ListView<Album> listv_albums) {
        Image albumImage = null;
        Album album;

        if (playerService.getMediaPlayer() != null) {
            Song playedSong = playerService.getActiveSong();
            album = playedSong.get(Type.ALBUM);
        } else {
            album = listv_albums.getSelectionModel().getSelectedItem();
        }

        String cover = album.getCover();

        // check for internet connection


        if (cover == null || (cover.equals("")) || (cover.contains("Album.jpg")) ) {
            if(isInternetReachable("http://www.last.fm")) {
                try {
                    retrieveAlbumCover(album);
                    String newCover = album.getCover();
                    if (newCover.contains("Album.jpg")) {
                        albumImage = new Image(newCover);
                    } else {
                        albumImage = new Image(new File(newCover).toURI().toString());
                    }
                    return albumImage;
                } catch (ServiceException e) {
                    logger.error("failed to retrieve album cover", e);
                }
            }
            return albumImage;
        } else {
            File file = new File(cover);
            if (file.exists() && !file.isDirectory()) {
                //albumImage = new Image(new File(cover).toURI().toString());
                albumImage = new Image(file.toURI().toString());
            } else {
                if(isInternetReachable("http://www.last.fm")) {
                    try {
                        retrieveAlbumCover(album);
                        String newCover = album.getCover();
                        albumImage = new Image(new File(newCover).toURI().toString());
                    } catch (ServiceException e) {
                        logger.error("failed to retrieve album cover", e);
                    }
                }
            }
            return albumImage;
        }
    }

    private void retrieveAlbumCover(Album album) throws ServiceException {
        AlbumCoverLastFMRetriever albumCoverLastFMRetriever = new AlbumCoverLastFMRetriever();
        albumCoverLastFMRetriever.retrieveInfo(album, albumService);
    }


    //checks for connection to the internet through dummy request
    public static boolean isInternetReachable(String urlString)
    {
        try {
            //make a URL to a known source
            URL url = new URL(urlString);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            logger.error("internet connection failed -"+ e.getMessage());
            return false;
        }
        catch (IOException e) {
            logger.error("internet connection failed -" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Set the album name for the now-playing song
     */
    public void setAlbumName(Player playerService, ListView<Album> listv_albums, TextField tf_albumname) {
        String albumName;
        Song playedSong = playerService.getActiveSong();

        if (playerService.getMediaPlayer() != null) {
            albumName = playedSong.get(Type.ALBUM).getName();
        } else {
            albumName = listv_albums.getSelectionModel().getSelectedItem().getName();
        }
        tf_albumname.setText(albumName);
        tf_albumname.setEditable(false);
    }


    public void deleteAlbumAction(PlayerMainController mainController) {
        if (mainController.getListViewAlbums().getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Warning Dialog", null, "Please select an album!");
            return;
        }

        //to find the usage of songs from this album, which are in any playlists.
        List<SongDTO> songsInPlaylists = null;
        try {
            songsInPlaylists = playlistService.getAllSongs();
        } catch (ServiceException e) {
            e.getMessage();
        }
        logger.debug("Songs in playlist: " + songsInPlaylists.size());

        Album albumToDelete = mainController.getListViewAlbums().getSelectionModel().getSelectedItem();

        //confirmation dialog
        ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Delete Album: '" + albumToDelete.getName() + "' with all contained songs?");

        if (result != ButtonType.OK) {
            mainController.getListViewAlbums().getSelectionModel().clearSelection();
            return;
        }

        List<Song> songsToDelete = new ArrayList<Song>();
        for(Song s : albumToDelete.get(Type.SONG)){
            songsToDelete.add(s);
        }

        for (SongDTO songDTO : songsInPlaylists) {
            for (Song songEntity : mainController.getLibrary().getAllSongs()) {
                if (songDTO.getId() == songEntity.getId()) {
                    if (songDTO.getAlbumId() == albumToDelete.getId()) {
                        try {
                            playlistService.deleteSongFromPlaylist(songEntity.getId());
                            logger.debug("remove song :" + songEntity.getName() + " from playlist!");
                        } catch (ServiceException e) {
                            showAlert(Alert.AlertType.WARNING, "Error", null, "Failed to remove song: '" + songEntity.getName() + "'.");
                        }
                    }
                }
            }
        }
        try {
            for (int i = 0; i < songsToDelete.size() ;i++) {
                Song s = songsToDelete.get(i);
                logger.debug("remove song: '" + s.getName() + "' from library!");
                mainController.getLibrary().removeSong(s); //
            }
            mainController.getListViewAlbums().getItems().remove(albumToDelete);

        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        showAlert(Alert.AlertType.INFORMATION, "Information Dialog", null, "Album: '" + albumToDelete.getName() + "' is removed!");

        initAlbumListView(mainController);
        mainController.getListViewAlbums().getSelectionModel().clearSelection();
        mainController.getTableViewSong().getItems().removeAll(songsToDelete);
    }


    /**
     * Initialize the album listView in the album_tab. The actual tableView will be replaced if a playlist_tableView is set.
     * Before it sets all songs of this album, this method checks if any album ist selected.
     */
    public void initAlbumListView(PlayerMainController mainController) {
        Album album;
        mainController.replacePlaylistTable();
        List<Album> albumList = mainController.getLibrary().getAllAlbums();
        ObservableList<Album> albums = FXCollections.observableList(albumList);
        albums.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        mainController.getListViewAlbums().setItems(albums);

        //To show only the name of the album in the ListView on the left side
        mainController.getListViewAlbums().setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {
            @Override
            public ListCell<Album> call(ListView<Album> param) {
                ListCell<Album> cell = new ListCell<Album>() {
                    @Override
                    public void updateItem(Album item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                };
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem editMenuItem = new MenuItem("Edit Album");
                editMenuItem.setOnAction(event -> mainController.editButtonAction());

                final MenuItem deleteMenuItem = new MenuItem("Delete Album");
                deleteMenuItem.setOnAction(event -> mainController.deleteButtonAction());

                contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
                cell.setContextMenu(contextMenu);
                cell.contextMenuProperty().bind(
                        Bindings.when(cell.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return cell;
            }
        });

        //check if any album is selected
        album = mainController.getListViewAlbums().getSelectionModel().getSelectedItem();
        if (album == null) {
            return;
        } else {
            mainController.setSongsForInfo(album);
        }
    }


    public ButtonType showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert error = new Alert(alertType);
        error.setTitle(title);
        error.setHeaderText(headerText);
        error.setContentText(contentText);
        error.showAndWait();
        return error.getResult();
    }
}
