package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;

/**
 * Created by marjaneh.
 */
@UI
public class SavePlaylistController implements Initializable {

    private static Logger logger = Logger.getLogger(SavePlaylistController.class);
    private Playlist playlistForEdit;

    @Autowired
    private PlayerMainController controller;

    @Autowired
    private PlaylistService playlistService;

    @FXML
    private TextField tf_playlistname;

    @FXML
    private Button bt_save;


    public void setPlaylistForEdit(Playlist playlistForEdit) {
        this.playlistForEdit = playlistForEdit;
        tf_playlistname.setText(playlistForEdit.getName());
        bt_save.setText("Save Changes");
    }

    @FXML
    private void saveButtonAction(ActionEvent actionEvent) {
        if (playlistForEdit != null) {
            PlaylistDTO updatePlaylist = new PlaylistDTO();
            updatePlaylist.setId(playlistForEdit.getId());
            updatePlaylist.setName(tf_playlistname.getText());

            try {
                playlistService.update(updatePlaylist);
                logger.info("change is saved");
            } catch (ValidationException e) {
                logger.error(e.getMessage());
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Failed to update playlist!");
                error.showAndWait();
            }

            //playlist updated closing dialog
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } else {
            PlaylistDTO playlistDTO = new PlaylistDTO();
            if (tf_playlistname.getText() == null || tf_playlistname.getText().isEmpty()) {
                playlistDTO.setName(tf_playlistname.getPromptText());
            } else {
                playlistDTO.setName(tf_playlistname.getText());
            }
            try {
                playlistService.persist(playlistDTO);
                logger.info("New playlist created");
            } catch (ValidationException e) {
                logger.error(e.getMessage());
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Failed to create playlist!");
                error.showAndWait();
            }

            //playlist saved closing dialog
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    private String getDefaultName() {
        int next = 1;
        List<PlaylistDTO> list = null;
        try {
            list = playlistService.readByName("Playlist");
            Collections.sort(list, new AlphanumComparator());
        } catch (ValidationException e) {
            logger.error(e.getMessage());
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        for(PlaylistDTO p: list) {
            try {
                //int i = Integer.parseInt(p.getName().substring(8));
                int i = Integer.parseInt(p.getName().substring("Playlist".length()));
                if (i == next) {
                    next++;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return "Playlist" + next;
    }

    @FXML
    private void cancelActionn(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tf_playlistname.setPromptText(getDefaultName());
        tf_playlistname.setFocusTraversable(false);
    }
}
