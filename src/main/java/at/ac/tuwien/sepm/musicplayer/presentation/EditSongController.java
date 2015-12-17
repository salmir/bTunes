package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.retriever.AlbumCoverLastFMRetriever;
import at.ac.tuwien.sepm.musicplayer.service.retriever.ArtistBioLastFMRetriever;
import at.ac.tuwien.sepm.musicplayer.service.retriever.InfoRetriever;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;

/**
 * Created by marjaneh.
 */
@UI
public class EditSongController implements Initializable {

    private static final Logger logger = LogManager.getLogger(EditSongController.class);
    private Song songForEdit;
    private Song updatedSong;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private Library library;

    @FXML
    private ComboBox<String> cb_genre;

    @FXML
    private TextField tf_artist;

    @FXML
    private TextField tf_title;

    @FXML
    private TextField tf_album;

    @FXML
    private void initComboBox() {
        logger.debug("initComboBox!");
        cb_genre.getItems().clear();
        cb_genre.getItems().addAll("Pop", "Rock", "Alternative", "Indie", "R&B", "HipHop", "Dance", "Electronic", "House", "Jazz", "Blues", "Funk", "Country", "Instrumental", "Orchestral", "Unknown");
    }

    public void setSongForEdit(Song songForEdit) {
        this.songForEdit = songForEdit;
        tf_artist.setText(songForEdit.get(Type.ARTIST).getName());
        tf_title.setText(songForEdit.getName());
        tf_album.setText(songForEdit.get(Type.ALBUM).getName());
        cb_genre.getSelectionModel().select(songForEdit.get(Type.GENRE).getName());
    }

    @FXML
    private void saveAction(ActionEvent actionEvent) {
        // todo: rename file
        String name = tf_title.getText();
        String artist = tf_artist.getText();
        String album = tf_album.getText();
        String genre = cb_genre.getSelectionModel().getSelectedItem();
        if(name == null || name.trim().equals("") || artist == null || artist.trim().equals("") || album == null || album.trim().equals("") || genre == null) {
            logger.error("Invalid parameters selected!");
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("You have entered invalid parameters!");
            error.setContentText("Please check your given input.");
            error.showAndWait();
            return;
        }

        // set up updated song
        SongDTO toUpdate = new SongDTO(songForEdit.getFile().getAbsolutePath());
        //toUpdate.setId(songForEdit.getId());
        toUpdate.setName(name);
        toUpdate.setArtistName(artist);
        toUpdate.setAlbumName(album);
        toUpdate.setGenre(genre);
        toUpdate.setReleaseYear(songForEdit.getReleaseyear());
        toUpdate.setLyrics(songForEdit.getLyrics());
        toUpdate.setRating(songForEdit.getRating());
        toUpdate.setLength(songForEdit.getLength());

        // remove old song
        try {
            library.removeSong(songForEdit);
        } catch (ServiceException e) {
            logger.error("failed to remove old song!");
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Failed to update song!");
            error.showAndWait();
            // close window
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
        // add new song
        try {
            Map<EntityType, List<InfoRetriever>> retriever = new HashMap<>();
            // retriever for artist
            List<InfoRetriever> artistRetriever = new ArrayList<>();
            artistRetriever.add(new ArtistBioLastFMRetriever());
            retriever.put(Type.ARTIST, artistRetriever);

            // retriever for albums
            List<InfoRetriever> albumRetriever = new ArrayList<>();
            albumRetriever.add(new AlbumCoverLastFMRetriever());
            retriever.put(Type.ALBUM, albumRetriever);
            // todo: add rest of retrievers (cover photo etc.)

            updatedSong = library.updateSong(toUpdate, retriever);
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Failed to update song!");
            error.showAndWait();
        }

        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public Song getUpdatedSong() {
        return updatedSong;
    }

    @FXML
    private void cancelActionn(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBox();
    }

}
