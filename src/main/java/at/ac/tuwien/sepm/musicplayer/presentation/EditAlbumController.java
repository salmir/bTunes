package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.impl.LibraryImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Tako on 28.05.2015.
 */

@UI
public class EditAlbumController {

    private static final Logger logger = Logger.getLogger(EditAlbumController.class);
    private Album albumToEdit;

    @FXML
    private Button bt_save;
    @FXML
    private Button bt_cancel;
    @FXML
    private TextField tf_album_name;
    @FXML
    private TextField tf_album_release;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private LibraryImpl library;

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setAlbumForEdit(Album albumForEdit){
        this.albumToEdit = albumForEdit;
        tf_album_name.setText(albumForEdit.getName());
        Integer release = new Integer(albumForEdit.getReleaseYear());
        tf_album_release.setText(release.toString());
    }
    @FXML
    private void saveButtonAction(ActionEvent actionEvent){
        if (tf_album_name != null || tf_album_name.getText() != "") {
            try {
                AlbumDTO album = albumService.read(albumToEdit.getId());
                album.setName(tf_album_name.getText());
                album.setReleaseYear(Integer.valueOf(tf_album_release.getText()));
                albumService.update(album);
                library.updateAlbum(album);
                logger.debug("AlbumName updated!");
            } catch (ValidationException | ServiceException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Invalid name for Album!");
        }
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}