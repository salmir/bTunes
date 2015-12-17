package at.ac.tuwien.sepm.musicplayer.presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Created by Alexandra on 28.05.2015.
 */

public class ImageClickCellFactory implements Callback<TableColumn, TableCell> {

        private final EventHandler<Event> click;

        public ImageClickCellFactory(EventHandler click) {
            this.click = click;
        }

        @Override
        public TableCell call(TableColumn p) {
            TableCell<Object, FavoriteImage> cell
                    = new TableCell<Object, FavoriteImage>() {

                private final VBox vbox;
                private final ImageView imageview;

                // Constructor
                {
                    vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    imageview = new ImageView();
                    imageview.setFitHeight(16);
                    imageview.setFitWidth(16);
                    imageview.setVisible(true);
                    imageview.setCache(true);
                    vbox.getChildren().addAll(imageview);
                    setGraphic(vbox);
                }

                @Override
                protected void updateItem(FavoriteImage item,
                                          boolean empty) {

                    // calling super here is very important - don't skip this!
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);

                    } else {
                        Image image = new Image(
                                getClass().getResourceAsStream(
                                        item.getFavoriteImage()));

                        imageview.setImage(image);
                        setGraphic(vbox);
                    }
                }
            };

            // Simple click
            if (click != null) {
                cell.setOnMouseClicked(click);
            }

            return cell;
        }
    }





