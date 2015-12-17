package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import at.ac.tuwien.sepm.musicplayer.service.impl.LibraryImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Created by Salmir on 01.06.2015.
 */

@UI
public class EditArtistController {

    private static final Logger logger = Logger.getLogger(EditArtistController.class);
    private Artist artistToEdit;

    @Autowired
    private PlayerMainController controller;

    @Autowired
    private ArtistService artistService;

    @FXML
    private TextField tf_artist_name;

    @Autowired
    private LibraryImpl library;

    public void setArtistToEdit(Artist artistToEdit) {
        this.artistToEdit = artistToEdit;
        tf_artist_name.setText(artistToEdit.getName());
    }

    @FXML
    private void saveButtonAction(ActionEvent actionEvent) {
        if (tf_artist_name != null || tf_artist_name.getText() != "") {
            try {
                ArtistDTO artist = artistService.read(artistToEdit.getId());
                artist.setName(tf_artist_name.getText());
                artistService.update(artist);
                library.updateArtist(artist);
                logger.debug("ArtistName updated!");
            } catch (ValidationException | ServiceException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Invalid name for Artist!");
        }
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
