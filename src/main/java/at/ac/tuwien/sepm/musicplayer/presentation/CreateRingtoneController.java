package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.service.RingtoneService;
import at.ac.tuwien.sepm.musicplayer.service.waveform.WaveformPanelContainer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tako
 */

@UI
public class CreateRingtoneController implements Initializable {

    @FXML
    private Button bt_create;
    @FXML
    private Button bt_cancel;
    @FXML
    public TextField start_field;
    @FXML
    public TextField end_field;
    @FXML
    public RadioButton check_sharefile;
    @FXML
    public Label songName;
    @FXML
    public SwingNode swingNodeWaveForm;
    @FXML
    public SplitPane splitPaneWaveform;
    @FXML
    private AnchorPane afterSequencePane;
    @FXML
    private ToggleButton bt_play;

    @Autowired
    private RingtoneService ringToneService;

    @Autowired
    private WaveformPanelContainer waveformContainer;

    private int von;
    private int bis;
    private Song songToCut;

    private Image stopImg;
    private Image playImg;

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("m:ss");
    private MediaPlayer mediaPlayer;

    @FXML
    private void createRingtone(ActionEvent actionEvent){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        if(start_field != null && end_field != null){
            try {
                this.von = (int)DATE_FORMAT.parse(start_field.getText()).getTime();
                this.bis = (int)DATE_FORMAT.parse(end_field.getText()).getTime();
                if((bis - von) > 30000){
                    showErrorDialog(null, "A Ringtone may not be longer then 30 sec.");
                    return;
                }
                if(von > bis || von < 0 || bis < 0){
                    showErrorDialog(null, "Invalid sec.");
                    return;
                }
            }catch(NumberFormatException e){
                showErrorDialog(null, "Invaild Input for Von - Bis");
            }catch(ParseException parseException) {
                showErrorDialog("Fehler bei der Umwandlung der Zeiten", parseException);
            }
        }else{
            showErrorDialog(null, "Von - Bis must not be empty");
        }

        Ringtone ringtone = new Ringtone(songToCut, von, bis);
        try {


            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a directory to save the ringtone");

            File defaultDirectory = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents");
            //fileChooser.setInitialDirectory(defaultDirectory);

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP3 Files", "mp3");
            fileChooser.getExtensionFilters().add(filter);

            File selectedFile = fileChooser.showSaveDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

            ringtone.setPath(selectedFile);

            ringToneService.createRingtone(ringtone);

            //in same Path with Ringtone a RingtoneShare Object to save
            if (check_sharefile.isSelected()) {

                Properties shareFile = new Properties();
                shareFile.setProperty("name", ringtone.getSong().getName());
                shareFile.setProperty("von", String.valueOf(ringtone.getVon()));
                shareFile.setProperty("bis", String.valueOf(ringtone.getBis()));

                try {
                    File f = new File(selectedFile.getParent() + "/share_" + ringtone.getPath().getName().substring(0,ringtone.getPath().getName().length()-4) + ".btu");
                    OutputStream out = new FileOutputStream(f);
                    shareFile.store(out, "");
                } catch (FileNotFoundException e) {
                    //e.printStackTrace();
                    showErrorDialog(null,"File not Found");
                } catch (IOException e) {
                    //e.printStackTrace();
                    showErrorDialog(null,"IO Exception");
                }
            }
        } catch (ServiceException e) {
            //e.printStackTrace();
            showErrorDialog(null,e.getMessage());
        } catch (ValidationException e) {
            //e.printStackTrace();
            showErrorDialog(null,"Validation failed");
        }

        /*//in same Path with Ringtone a RingtoneShare Object to save
        if(check_sharefile.isSelected()){
            try {
                ringToneService.createRingtone(ringtoneShare, selectedFile);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }*/
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelAction(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setSongToCut(Song songToCut){
        this.songToCut = songToCut;

        waveformContainer = new WaveformPanelContainer();
        File file = new File(songToCut.getFile().getPath());
        try {
            waveformContainer.setAudioToDisplay(file);
        } catch (ServiceException e) {
            showErrorDialog("Error while trying to display mp3 file", e);
        }
        swingNodeWaveForm.autosize();
        swingNodeWaveForm.setContent(waveformContainer);
        AnchorPane.setLeftAnchor(swingNodeWaveForm, 0.0);
        AnchorPane.setRightAnchor(swingNodeWaveForm, 0.0);
        AnchorPane.setTopAnchor(swingNodeWaveForm, 0.0);
        AnchorPane.setBottomAnchor(swingNodeWaveForm, 0.0);

        DateFormat format = new SimpleDateFormat("m:ss");

        start_field.setText(format.format(new Date((long) (songToCut.getLength() * splitPaneWaveform.getDividers().get(0).getPosition() * 1000))));
        end_field.setText(format.format(new Date((long) (songToCut.getLength() * splitPaneWaveform.getDividers().get(1).getPosition() * 1000))));
        initializeFromToDivider();

        initializeMediaPlayer();

    }

    private void initializeMediaPlayer() {
        Media ringtone;
        if(songToCut != null) {
            ringtone = new Media(songToCut.getFile().toURI().toString());

            mediaPlayer = new MediaPlayer(ringtone);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            try {
                mediaPlayer.setStartTime(new Duration(DATE_FORMAT.parse(start_field.getText()).getTime()));
                mediaPlayer.setStopTime(new Duration(DATE_FORMAT.parse(end_field.getText()).getTime()));
            }
            catch(ParseException parseException) {
                showErrorDialog("baaammm", parseException);
            }
        }
    }
    private void showErrorDialog(String message, Exception e) {
        showErrorDialog(message, e.getMessage());
    }

    private void showErrorDialog(String message, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void initializeFromToDivider() {
        List<SplitPane.Divider> dividers = splitPaneWaveform.getDividers();
        start_field.setText(DATE_FORMAT.format(
                new Date((long) (songToCut.getLength() * dividers.get(0).positionProperty().doubleValue() * 1000))));
        end_field.setText(DATE_FORMAT.format(
                new Date((long) (songToCut.getLength() * dividers.get(1).positionProperty().doubleValue() * 1000))));


        dividers.get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                start_field.setText(DATE_FORMAT.format(new Date((long) (songToCut.getLength() * newValue.doubleValue() * 1000))));
                mediaPlayer.setStartTime(new Duration(songToCut.getLength() * newValue.doubleValue() * 1000));

            }
        });
        dividers.get(1).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                end_field.setText(DATE_FORMAT.format(new Date((long) (songToCut.getLength() * newValue.doubleValue() * 1000))));
                mediaPlayer.setStopTime(new Duration(songToCut.getLength() * newValue.doubleValue() * 1000));
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start_field.setDisable(true);
        end_field.setDisable(true);
        createPlayStopButon(); //todo: eventuell enablen wegen der Farbe oder bidirectional!?
    }

    private void createPlayStopButon() {
        ImageView toggleImage = new ImageView();
        Image play = new Image("/images/play.png");
        Image stop = new Image("/images/stop.png");

        toggleImage.setFitHeight(bt_play.getPrefHeight());
        toggleImage.setFitWidth(bt_play.getPrefWidth());

         bt_play.setPadding(Insets.EMPTY);
        bt_play.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(javafx.beans.binding.Bindings
                        .when(bt_play.selectedProperty())
                        .then(stop)
                        .otherwise(play)
        );
    }

    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }

    public void playPauseAction(ActionEvent actionEvent) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        } else {
            mediaPlayer.play();
        }
    }
}

