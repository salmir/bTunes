package at.ac.tuwien.sepm.musicplayer.presentation;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Youtube;
import at.ac.tuwien.sepm.musicplayer.service.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.net.URL;
import java.util.*;

/**
 * Created by Alexandra on 07.06.2015.
 */
@UI
public class YoutubeController implements Initializable {

    private static Logger logger = Logger.getLogger(YoutubeController.class);
    private String selectedUrl = "";

    @Autowired
    private YoutubeService youtube;

    @FXML
    TableView<Youtube> result_tb;

    @FXML
    TableColumn<Youtube, Integer> tc_number;

    @FXML
    TableColumn<Youtube, String> tc_title;

    @FXML
    ImageView image;

    @FXML
    Button play_btn;

    @FXML
    Button search_btn;

    @FXML
    TextField input_txt;

    @FXML
    Label label;

    private ObservableList<Youtube> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setPlayButtonImage();
        setImage("");
        setUpTable();
    }



    private void setImage(String imag){
        String path;
       if(imag== "") {
           path = "/images/youtube.jpg";
       }else{
           path = imag;
       }
        Image img = new Image(path);
        image.setImage(img);
    }

    private void setUpTable(){

        data.addAll(FXCollections.emptyObservableList());
        tc_number.setCellValueFactory(new PropertyValueFactory<Youtube, Integer>("nummer"));
        tc_number.prefWidthProperty().bind(result_tb.widthProperty().multiply(0.2));

        tc_title.setCellValueFactory(new PropertyValueFactory<Youtube, String>("titel"));
        tc_title.prefWidthProperty().bind(result_tb.widthProperty().multiply(0.9));

        result_tb.setRowFactory(tableView -> {
            final TableRow<Youtube> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    setImage(youtube.getThumbnail().get(row.getIndex()));
                    selectedUrl = youtube.getSoundURLs().get(row.getIndex());
                }
            });

            return row;
        });
       // result_tb.getColumns().addAll(tc_number, tc_title);
        result_tb.setItems(data);


    }




    private void setPlayButtonImage() {
        javafx.scene.image.Image play = new javafx.scene.image.Image("/images/playOld.png");
        ImageView image = new ImageView(play);
        image.setFitWidth(play_btn.getPrefWidth());
        image.setFitHeight(play_btn.getPrefHeight());
        play_btn.setPadding(Insets.EMPTY);
        play_btn.setGraphic(image);
    }


    @FXML
    private void playButtonAction() {

       if(selectedUrl == ""|| selectedUrl.isEmpty()){
           return;
       }else {
           Display display = Display.getDefault();
           Shell shell = new Shell(display);
           shell.setMaximized(true);
           Browser browser = new Browser(shell, SWT.NONE);

           shell.setLayout(new FillLayout());

           shell.open();
           browser.setUrl(selectedUrl);


           while (!shell.isDisposed()) {
               if (!display.readAndDispatch())
                   display.sleep();
           }
           display.dispose();

           if(!display.isDisposed()){
               return;
           }
       }
    }





    @FXML
    private void searchAction(){
       String search = input_txt.getText();
       youtube.search(search);
        System.out.println(youtube.getResults());
        //System.out.println(hMap);
       refreshTable();
        input_txt.clear();


    }

    private void refreshTable(){
        List<Youtube> list = youtube.getResults();
        data = FXCollections.observableArrayList(list);
        result_tb.setItems(null);
        result_tb.layout();
        result_tb.setItems(data);
    }


}
