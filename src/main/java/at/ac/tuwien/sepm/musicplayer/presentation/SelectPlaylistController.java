package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Salmir on 26.06.2015.
 */
@UI
public class SelectPlaylistController {

    private List<Playlist> playlists;

    public void setMainController(PlayerMainController mainController) {
        this.mainController = mainController;
    }

    private PlayerMainController mainController;

    @FXML
    private TableView tv_playlists;

    @FXML
    private TableColumn<Playlist, String> tc_playlistName;

    @FXML
    private Button btn_djCancel, btn_djSelectPlaylist;

    @FXML
    public void djCancelAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void djSelectAction(ActionEvent actionEvent) {
        if (!tv_playlists.getSelectionModel().isEmpty()) {
            Playlist selectedPlaylist = (Playlist) tv_playlists.getSelectionModel().getSelectedItem();
            mainController.djAction(selectedPlaylist);
        }
    }

    public void setPlaylist(ObservableList<Playlist> playlist) {
        this.playlists = playlist;
        tv_playlists.setItems(FXCollections.observableArrayList(playlists));
        tc_playlistName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
    }
}
