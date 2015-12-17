package at.ac.tuwien.sepm.musicplayer.presentation;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Alexandra on 28.05.2015.
 */
public class FavoriteImage {


        private final SimpleStringProperty favoriteImage = new SimpleStringProperty();

        public FavoriteImage(String imagePath) {
            setFavoriteImage(imagePath);
        }

        public void setFavoriteImage(String favoriteImageFile) {
            favoriteImage.set(favoriteImageFile);
        }

        public String getFavoriteImage() {
            return favoriteImage.get();
        }
    }

