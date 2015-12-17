package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.DelegatingInfo;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.*;
import at.ac.tuwien.sepm.musicplayer.service.generators.SimilarArtistsPlaylistGenerator;
import at.ac.tuwien.sepm.musicplayer.service.generators.SimilarSongsPlaylistGenerator;
import at.ac.tuwien.sepm.musicplayer.service.presentation.PlayerMainControllerAlbumService;
import at.ac.tuwien.sepm.musicplayer.service.presentation.PlayerMainControllerSongService;
import at.ac.tuwien.sepm.musicplayer.service.retriever.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.controlsfx.control.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by marjaneh.
 */
@UI
public class PlayerMainController implements Initializable {

    private final String darkBlueTheme = getClass().getResource("/DarkBlue.css").toExternalForm();
    private final String flashRedTheme = getClass().getResource("/FlashRed.css").toExternalForm();
    private static Logger logger = Logger.getLogger(PlayerMainController.class);
    private ObservableList<Song> songData;
    private ObservableList<Song> songsFromPlaylistTable;
    private ObservableList<Playlist> playLists;
    private boolean playlists_tab, artists_tab, albums_tab;
    private boolean songTable;
    public static DataFormat INDEX = new DataFormat("text/index", "text/index");
    private ApplicationContext appContext;
    private static Image soundMidImage = new Image("/images/volumeMid.png");
    private static Image soundMaxImage = new Image("/images/volumeMax.png");
    private static Image soundMinImage = new Image("/images/volumeMin.png");
    private static Image soundMuteImage = new Image("/images/mute.png");
    private Double temporaryPower;

    @Autowired
    private Player playerService;

    @Autowired
    private SongService songService;

    @Autowired
    private Library library;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SimilarArtistsPlaylistGenerator similarArtistsPlaylistGenerator;

    @Autowired
    private SimilarSongsPlaylistGenerator similarSongsPlaylistGenerator;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private PlayerMainControllerAlbumService controllerAlbumService;

    @Autowired
    private PlayerMainControllerSongService controllerSongService;

    @FXML
    private TableView<Song> tv_song;

    @FXML
    private TableView<Song> playlistTableView;

    @FXML
    private ToggleButton tgbt_playpausesong;

    @FXML
    private Tab tab_concerts;
    @FXML
    private Button bt_stopsong, bt_mediaLibrary;

    @FXML
    private Button dj_btn;

    @FXML
    private Button bt_nextsong;

    @FXML
    private Button bt_previoussong;

    @FXML
    private Button bt_repeat;

    @FXML
    private ToggleButton tgbt_shuffle;

    @FXML
    private Button bt_sound;

    @FXML
    private Button bt_create;

    @FXML
    private Button bt_delete;

    @FXML
    private Button bt_edit;

    @FXML
    private Button bt_synchronize;

    @FXML
    private Button bt_similarplaylist;

    @FXML
    private ImageView img_albumcover;

    @FXML
    private ImageView img_artistimage;

    @FXML
    private TextField tf_albumname;

    @FXML
    private TextField tf_search;

    @FXML
    private Button bt_search;

    @FXML
    private Button youtube_btn;

    @FXML
    private Label lab_songname;

    @FXML
    private Slider sl_time;

    @FXML
    private Slider sl_sound;

    @FXML
    private Label lab_time, lab_duration;

    @FXML
    private TextArea ta_bio;

    @FXML
    private TextArea ta_lyrics;

    @FXML
    private TextArea ta_tracklist;

    @FXML
    private ListView<Playlist> listv_playlists;

    @FXML
    private ListView<Artist> listv_artists;

    @FXML
    private ListView<Album> listv_albums;

    @FXML
    private BorderPane bp_mainPane;

    @FXML
    private HBox pane;

    @FXML
    private Rating rating;

    @FXML
    private ImageView view;

    /*public PlayerMainController () {
        this.playerService = new PlayerImpl();
        try {
            library = new LibraryImpl();
            library.initialize();
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            // todo: show error dialog to user
        }
    }*/

    public Rating initRating(int id, int value) {
        rating = new Rating();
        //rating.getStyleClass().add("rating");
        //rating.setPrefSize(1,0.25);
        //rating.prefWidthProperty().bind(tc_rating.minWidthProperty());
        //rating.setStyle("rating: scale_half;");
        rating.setRating(value);
        rating.ratingProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                try {
                    // System.out.println(id +" "+ (int)Double.parseDouble(newValue.toString()));

                    songService.persistRating(id, (int) Double.parseDouble(newValue.toString()));
                } catch (ValidationException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        });
        return rating;
    }

    public void initSongTable() {
        if (tv_song.equals(null) || tv_song.getColumns().size() == 0) {
            tv_song.setFixedCellSize(25);
            TableColumn<Song, String> tc_artist = new TableColumn<>("Artist");
            tc_artist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ARTIST).getName()));
            tc_artist.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.14));
            tc_artist.setSortType(TableColumn.SortType.ASCENDING);
            tc_artist.setSortable(true);

            TableColumn<Song, String> tc_title = new TableColumn<>("Title");
            tc_title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
            tc_title.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.27));

            TableColumn<Song, String> tc_album = new TableColumn<>("Album");
            tc_album.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ALBUM).getName()));
            tc_album.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.15));

            TableColumn<Song, String> tc_genre = new TableColumn<>("Genre");
            tc_genre.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.GENRE).getName()));
            tc_genre.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.1));

            TableColumn<Song, String> tc_duration = new TableColumn<>("Duration");
            tc_duration.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getLength() * 1 / (long) 60) + ":" + ((param.getValue().getLength() % 60) < 10 ? ("0" + Long.toString(param.getValue().getLength() % 60)) : (Long.toString(param.getValue().getLength() % 60)))));
            tc_duration.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.1));
            tc_duration.getStyleClass().add("duration");

            TableColumn<Song, Rating> tc_rating = new TableColumn<>("Rating");
            tc_rating.setCellValueFactory(param -> new SimpleObjectProperty<Rating>(initRating(param.getValue().getId(), param.getValue().getRating())));
            tc_rating.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.23));
            tv_song.getColumns().addAll(tc_artist, tc_title, tc_album, tc_genre, tc_duration, tc_rating);
        }
        //tc_rating.prefWidthProperty().bind(tv_song.widthProperty().multiply(0.15));
        //tc_rating = new TableColumn<Song,Rating>("Rating");
        //tc_rating.setCellValueFactory(new PropertyValueFactory<Song, Rating>("tc_rating"));


        // init songs
        List<Song> songList = library.getAllSongs();
        songData = FXCollections.observableList(songList);

        logger.info("All songs are set in the table");
        tv_song.setItems(songData);

        // add sorting order
        tv_song.getSortOrder().add(tv_song.getColumns().get(0));

        // set the play-button disabled if table is empty
        tgbt_playpausesong.disableProperty().bind(Bindings.size(songData).isEqualTo(0));
        tv_song.getSelectionModel().clearSelection();

        tv_song.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> tableView) {
                final TableRow<Song> row = new TableRow<Song>() {
                    @Override
                    protected void updateItem(Song item, boolean empty) {
                        super.updateItem(item, empty);
                        // active song will be highlighted in the table
                        if (item != null && playerService.getActiveSongPosition() != null && playerService.getMediaPlayer() != null) {
                            if (getIndex() == playerService.getActiveSongPosition() && playerService.getActivePlaylist().equals(tv_song.getItems()) &&
                                    playerService.getActivePlaylistId() == getActiveSongsListId()) {
                                setStyle("-fx-background-color: #3d6b89");
                            } else {
                                setStyle("");
                            }
                        } else {
                            setStyle("");
                        }
                        tv_song.getSelectionModel().clearSelection();
                    }
                };

                final ContextMenu contextMenu = new ContextMenu();
                // right click -> Edit
                final MenuItem editMenuItem = new MenuItem("Edit");
                editMenuItem.setOnAction(event -> songEditAction(row.getItem()));

                // right click -> Delete
                final MenuItem deleteMenuItem = new MenuItem("Delete");
                deleteMenuItem.setOnAction(event -> songDeleteAction(row.getItem()));

                // right click -> Generate Playlist from Selection
                final MenuItem createRingtoneMenuItem = new MenuItem("Create Ringtone");
                createRingtoneMenuItem.setOnAction(event -> PlayerMainController.this.createRingtone(row.getItem()));

                final MenuItem similarPlaylistItem = new MenuItem("Generate Playlist from Selection");
                similarPlaylistItem.setOnAction(event -> createSimilarSongsPlaylist(row.getItem()));

                contextMenu.getItems().addAll(editMenuItem, similarPlaylistItem, deleteMenuItem, createRingtoneMenuItem);
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );

                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        setPlayerVariables(tv_song, row);
                        stopButtonAction();
                        playPauseButtonAction();
                        tgbt_playpausesong.setSelected(true);
                    } else if (playerService.getMediaPlayer() == null) {
                        setPlayerVariables(tv_song, row);
                    }
                });

                row.setOnDragDetected(ev -> {
                    if (!row.isEmpty()) {
                        // drag was detected, start a drag-and-drop gesture
                        // allow any transfer mode
                        Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                        db.setDragView(row.snapshot(null, null));

                        // put row.getIndex() on dragboard
                        ClipboardContent content = new ClipboardContent();
                        content.put(INDEX, row.getIndex());
                        db.setContent(content);
                        ev.consume();
                    }
                });
                return row;
            }
        });
        ta_bio.setEditable(false);
        ta_tracklist.setEditable(false);
        songTable = true;

    }

    /**
     * Initialize the ListView for the playlist_tab and configure the listView items for adding songs to a playlist with drag and drop.
     */
    private void initPlayListView() {
        List<Playlist> entityPlaylist = new ArrayList<>();
        try {
            List<PlaylistDTO> dtoPlaylist = playlistService.readAll();
            Collections.sort(dtoPlaylist, new AlphanumComparator());
            dtoPlaylist.forEach(playlistDTO -> {
                try {
                    entityPlaylist.add(playlistDTO.createNew());
                } catch (FileNotFoundException e) {
                    logger.error(e.getMessage());
                }
            });
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        // init PlayListView
        playLists = FXCollections.observableList(entityPlaylist);
        listv_playlists.setItems(playLists);

        listv_playlists.setCellFactory(new Callback<ListView<Playlist>, ListCell<Playlist>>() {
            @Override
            public ListCell<Playlist> call(ListView<Playlist> param) {
                ListCell<Playlist> cell = new ListCell<Playlist>() {
                    @Override
                    public void updateItem(Playlist item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());

                            // active playlist will be highlighted
                            if (playerService.getMediaPlayer() != null && playerService.getActivePlaylist() != null &&
                                    playerService.getActivePlaylist().equals(controllerSongService.convertAllSongsFromPlaylist(item, library)) && playerService.getActivePlaylistId() == item.getId()) {
                                setStyle("-fx-background-color: #3d6b89");
                                listv_playlists.getSelectionModel().select(getIndex());
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };

                final ContextMenu contextMenu = new ContextMenu();
                // right click -> Edit Playlist
                final MenuItem editMenuItem = new MenuItem("Edit Playlist");
                editMenuItem.setOnAction(event -> editButtonAction());

                // right click -> Delete Playlist
                final MenuItem deleteMenuItem = new MenuItem("Delete Playlist");
                deleteMenuItem.setOnAction(event -> deleteButtonAction());

                // right click -> Mix it up!
                final MenuItem djMenuItem = new MenuItem("Mix it up!");
                djMenuItem.setOnAction(event -> djAction(listv_playlists.getSelectionModel().getSelectedItem()));

                contextMenu.getItems().addAll(djMenuItem, editMenuItem, deleteMenuItem);
                cell.contextMenuProperty().bind(
                        Bindings.when(cell.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );

                cell.setOnDragOver(ev -> {
                    // data is dragged over the target, accept it only if it is not
                    // dragged from the same playlist and if it has a INDEX(row-index)
                    if (!cell.isSelected() && ev.getDragboard().hasContent(INDEX)) {
                        ev.acceptTransferModes(TransferMode.ANY);
                    }
                    ev.consume();
                });

                cell.setOnDragDropped(ev -> {
                    // data dropped
                    Dragboard db = ev.getDragboard();
                    boolean success = false;

                    if (db.hasContent(INDEX)) {
                        int draggedIndex = (Integer) db.getContent(INDEX);
                        Song draggedSong = null;

                        if (songTable) {
                            draggedSong = tv_song.getItems().get(draggedIndex);
                        } else {
                            draggedSong = playlistTableView.getItems().get(draggedIndex);
                        }

                        insertSongIntoPlaylist(cell.getItem(), draggedSong);
                        success = true;
                    }
                    ev.setDropCompleted(success);
                    ev.consume();
                });
                return cell;
            }
        });
    }

    public void djAction(Playlist djPlaylist) {
        logger.debug("DJ-Mode activated!");
        List<Song> djSongsList;
        djSongsList = controllerSongService.convertAllSongsFromPlaylist(djPlaylist, library);

        if (djPlaylist == null) {
            showAlert(Alert.AlertType.WARNING, "Warning Dialog", null,
                    "Please select a Playlist to start the DJ-Mode!");
            return;
        }
        stopButtonAction();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/DJMainGui.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DJMainController djMainController = fxmlLoader.getController();
        djMainController.setPlaylist(djSongsList);

        Stage djStage = new Stage();
        djStage.setTitle("bTunes DJ Mode");
        djStage.setScene(new Scene(root));
        djStage.setResizable(false);
        djStage.initModality(Modality.APPLICATION_MODAL);
        djStage.showAndWait();

        //close DJ-Mode
        djMainController.stopAll();
    }


    /**
     * Sets up the playlist-TableView after "converting" SongDTOs to Song-Entities when a playlist is selected.
     * If no playlist is selected, the song-TableView will stay in the main pane.
     */
    private void initPlaylistSongTable() {
        Playlist selectedPlaylist = listv_playlists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist == null) {
            return;
        }

        // init PlaylistSongTable
        songsFromPlaylistTable = FXCollections.observableArrayList(
                controllerSongService.convertAllSongsFromPlaylist(selectedPlaylist, library));
        TableView<Song> playlistTV = playlistSongTableView();
        playlistTV.setItems(songsFromPlaylistTable);
    }

    /**
     * Set up the TableView for the playlists
     *
     * @return playlist-TableView
     */
    private TableView<Song> playlistSongTableView() {
        playlistTableView = new TableView<>();
        playlistTableView.setFixedCellSize(25);
        TableColumn<Song, String> artist = new TableColumn<>("Artist");
        artist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ARTIST).getName()));
        artist.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.14));

        TableColumn<Song, String> title = new TableColumn<>("Title");
        title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        title.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.27));

        TableColumn<Song, String> album = new TableColumn<>("Album");
        album.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.ALBUM).getName()));
        album.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.15));

        TableColumn<Song, String> genre = new TableColumn<>("Genre");
        genre.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(Type.GENRE).getName()));
        genre.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.1));

        TableColumn<Song, String> duration = new TableColumn<>("Duration");
        duration.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getLength() * 1 / (long) 60) + ":" + ((param.getValue().getLength() % 60) < 10 ? ("0" + Long.toString(param.getValue().getLength() % 60)) : (Long.toString(param.getValue().getLength() % 60)))));
        duration.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.1));

        TableColumn<Song, Rating> ratingtb = new TableColumn<>("Rating");
        ratingtb.setCellValueFactory(param -> new SimpleObjectProperty<Rating>(initRating(param.getValue().getId(), param.getValue().getRating())));
        ratingtb.prefWidthProperty().bind(playlistTableView.widthProperty().multiply(0.23));

        playlistTableView.getColumns().addAll(artist, title, album, genre, duration, ratingtb);

        playlistTableView.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {

            @Override
            public TableRow<Song> call(TableView<Song> tableView) {
                final TableRow<Song> row = new TableRow<Song>() {
                    @Override
                    protected void updateItem(Song item, boolean empty) {
                        super.updateItem(item, empty);
                        //this.setPrefHeight(25);
                        // active song will be highlighted in the table
                        if (item != null && playerService.getActiveSongPosition() != null && playerService.getMediaPlayer() != null) {
                            if (getIndex() == playerService.getActiveSongPosition() && playerService.getActivePlaylist().equals(playlistTableView.getItems()) &&
                                    playerService.getActivePlaylistId() == getActiveSongsListId()) {
                                setStyle("-fx-background-color: #3d6b89");
                            } else {
                                setStyle("");
                            }
                        } else {
                            setStyle("");
                        }
                        playlistTableView.getSelectionModel().clearSelection();
                    }
                };

                final ContextMenu contextMenu = new ContextMenu();
                //right click -> Edit
                final MenuItem editMenuItem = new MenuItem("Edit");
                editMenuItem.setOnAction(event -> songEditAction(row.getItem()));

                //right click -> Delete from Playlist
                final MenuItem deleteMenuItem = new MenuItem("Delete from Playlist");
                deleteMenuItem.setOnAction(event -> deleteSongFromPlaylistAction(row.getItem()));

                contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );

                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        setPlayerVariables(playlistTableView, row);
                        stopButtonAction();
                        playPauseButtonAction();
                        tgbt_playpausesong.setSelected(true);
                    } else if (playerService.getMediaPlayer() == null) {
                        setPlayerVariables(playlistTableView, row);
                    }
                });

                row.setOnDragDetected(ev -> {
                    if (!row.isEmpty()) {
                        // drag was detected, start a drag-and-drop gesture
                        // allow any transfer mode
                        Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                        db.setDragView(row.snapshot(null, null));

                        // put row.getIndex() on dragboard
                        ClipboardContent content = new ClipboardContent();
                        content.put(INDEX, row.getIndex());
                        db.setContent(content);
                        ev.consume();
                    }
                });

                controllerSongService.changeSongPosition(getMainController(), row);

                return row;
            }
        });
        songTable = false;

        return playlistTableView;
    }


    private void insertSongIntoPlaylist(Playlist playlist, Song song) {
        List<SongDTO> songsToInsert = new ArrayList<>();
        SongDTO songDTO = new SongDTO(song.getName(), song.getFile().getAbsolutePath());
        songDTO.setId(song.getId());
        songsToInsert.add(songDTO);

        try {
            // if selected playlist is active playlist, must active playlist
            // to be re-set after insert
            if (playerService.getActivePlaylistId() == playlist.getId()) {
                playlistService.insertSongs(playlist.getId(), songsToInsert);
                playerService.getActivePlaylist().add(song);
                playerService.setActivePlaylist(playerService.getActivePlaylist());
            } else {
                playlistService.insertSongs(playlist.getId(), songsToInsert);
            }
        } catch (ValidationException | ServiceException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Replace (remove old and add new) the playlist-table (songs from the choosen playlist) with the song-table (all songs in the library).
     * Refreshes the playlisttable after replacing
     */
    public void replacePlaylistTable() {
        if (playlistTableView != null) {
            bp_mainPane.getChildren().remove(playlistTableView);
            bp_mainPane.getChildren().remove(tv_song);
            bp_mainPane.setCenter(tv_song);
            refreshSongTable();
        }
    }

    /**
     * Initialize the artist listView in the artist_tab. The actual tableView will be replaced if a playlist_tableView is set.
     * Before it sets all songs of this artist, this method checks if any artist ist selected.
     */
    @FXML
    private void initArtistListView() {
        replacePlaylistTable();
        List<Artist> artistList = library.getAllArtists(); // todo: artist is not updated in library after updating in the db. Write a method to update artist and his/her songs in the library
        ObservableList<Artist> artists = FXCollections.observableList(artistList);
        artists.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        listv_artists.setItems(artists);

        //To show only the name of the artists in the ListView on the left side
        listv_artists.setCellFactory(new Callback<ListView<Artist>, ListCell<Artist>>() {
            @Override
            public ListCell<Artist> call(ListView<Artist> param) {
                ListCell<Artist> cell = new ListCell<Artist>() {
                    @Override
                    public void updateItem(Artist item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                };
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem similarPlaylistItem = new MenuItem("Generate Playlist from Selection");
                similarPlaylistItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        createSimilarArtistsPlaylist(listv_artists.getSelectionModel().getSelectedItem());
                    }
                });

                final MenuItem editMenuItem = new MenuItem("Edit Artist");
                editMenuItem.setOnAction(event -> editButtonAction());

                final MenuItem deleteMenuItem = new MenuItem("Delete Artist");
                deleteMenuItem.setOnAction(event -> deleteButtonAction());

                contextMenu.getItems().addAll(similarPlaylistItem, editMenuItem, deleteMenuItem);
                cell.setContextMenu(contextMenu);
                cell.contextMenuProperty().bind(
                        Bindings.when(cell.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return cell;
            }
        });

        //check if any artist is selected
        Artist artist = listv_artists.getSelectionModel().getSelectedItem();
        if (artist == null) {
            return;
        } else {
            setSongsForInfo(artist);
        }
    }


    /**
     * Clears the actual TableView and sets all songs from given artist or album in the song-tableView
     *
     * @param info - could be an artist or an album entity.
     */
    public void setSongsForInfo(DelegatingInfo info) {
        ObservableList<Song> allSongsOfThisInfo = FXCollections.observableArrayList(info.get(Type.SONG));
        tv_song.setItems(allSongsOfThisInfo);
        tgbt_playpausesong.disableProperty().bind(Bindings.size(allSongsOfThisInfo).isEqualTo(0));
    }

    /**
     * todo: look at library.importFolder()
     */
    @FXML
    private void addFileToMediathekAction() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the music you want to import");
        logger.info("music path: \t" + System.getProperty("user.home") + System.getProperty("file.separator") + "Music");
        File defaultDirectory = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Music");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(null);
        if (selectedDirectory != null) {
            logger.info("selected directory: " + selectedDirectory.getPath());
            Map<EntityType, List<InfoRetriever>> retriever = new HashMap<>();
            // check for internet connection
            if (!isInternetReachable("http://www.last.fm")) {
                ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "You are offline",
                        "No internet connection available", "Information of songs will get loaded when " +
                                "internet connection is present.\nDo you want to proceed?");
                // do not load song information
                retriever = null;

                if (result != ButtonType.OK) {
                    return;
                }
            } else {
                // add information retrievers
                // retriever for artists
                List<InfoRetriever> artistRetriever = new ArrayList<>();
                artistRetriever.add(new ArtistBioLastFMRetriever());
                retriever.put(Type.ARTIST, artistRetriever);

                // retriever for albums
                List<InfoRetriever> albumRetriever = new ArrayList<>();
                albumRetriever.add(new AlbumCoverLastFMRetriever());
                retriever.put(Type.ALBUM, albumRetriever);


                List<InfoRetriever> artistImageRetriever = new ArrayList<>();
                artistImageRetriever.add(new ArtistImageWikiRetriever());
                retriever.put(Type.ARTIST, artistImageRetriever);


                List<InfoRetriever> concertRetriever = new ArrayList<>();
                concertRetriever.add(new ConcertDataLastFMRetriever());
                retriever.put(Type.ARTIST, concertRetriever);
                // todo: add other retrievers (for photo etc.)

            }
            try {
                List<Song> importedSongs = library.importFolder(selectedDirectory, retriever);
                tv_song.getItems().addAll(importedSongs);
                sortByArtist();
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                logger.error(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to import song(s)", "Your selected songs could not get imported.\nPlease try again.\nNotice that non-mp3-files are not allowed.");
            }
        } else {
            logger.info("no directory selected");
        }
    }

    private void sortByArtist() {
        tv_song.getSortOrder().clear();
        tv_song.getSortOrder().add(tv_song.getColumns().get(0));
    }

    @FXML
    private void showAboutAction() {
        showAlert(Alert.AlertType.INFORMATION, "bTunes", "bTunes Media Player", "Version 1\n\n" +
                "Created by Camur Ali Kemal, Dalic Salmir, Lenz Lena, Mai Alexandra, Setayeshi Marjaneh");
    }

    /**
     * Opens the statistic window
     */
    @FXML
    private void showStatisticsAction() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/Statistic.fxml"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        StatisticController statisticController = fxmlLoader.getController();
        statisticController.setMainApp(this);

        Stage statisticStage = new Stage();
        statisticStage.setTitle("Statistics");
        statisticStage.setScene(new Scene(root));
        statisticStage.initModality(Modality.APPLICATION_MODAL);
        statisticController.refreshTab();
        statisticStage.show();
    }

    @FXML
    private void youtubeAction() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/YoutubeGui.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage createEditStage = new Stage();
        createEditStage.setTitle("Youtube");
        createEditStage.setScene(new Scene(root));
        createEditStage.initModality(Modality.APPLICATION_MODAL);
        createEditStage.showAndWait();
    }

    /**
     * Opens the creating and editing window for a playlist
     */
    @FXML
    private void playlistCreateAction() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/SavePlaylist.fxml"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        Stage createEditStage = new Stage();
        createEditStage.setTitle("Save/Edit Playlist");
        createEditStage.setScene(new Scene(root));
        createEditStage.initModality(Modality.APPLICATION_MODAL);
        createEditStage.showAndWait();
        initPlayListView();
    }

    /**
     * Configures the edit button below the tabs on the left side.
     * Depending to the tab which is actually opened, it will edits a playlist, an artist or an album.
     * todo: create now windows
     */
    @FXML
    public void editButtonAction() {
        if (playlists_tab) {
            logger.debug("playlistEditButton");

            if (listv_playlists.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Warning Dialog", null, "Please select a Playlist to edit!");
                return;
            }
            Playlist playlistForEdit = listv_playlists.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader();
            //add spring context to JavaFX
            fxmlLoader.setControllerFactory(appContext::getBean);
            Parent root = null;
            try {
                root = fxmlLoader.load(getClass().getResourceAsStream("/SavePlaylist.fxml"));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            SavePlaylistController savePlaylistController = fxmlLoader.getController();
            savePlaylistController.setPlaylistForEdit(playlistForEdit);

            Stage createEditStage = new Stage();
            createEditStage.setTitle("Save/Edit Playlist");
            createEditStage.setScene(new Scene(root));
            createEditStage.initModality(Modality.APPLICATION_MODAL);
            createEditStage.showAndWait();

            initPlayListView();
            listv_playlists.getSelectionModel().clearSelection();

        } else if (albums_tab) { //todo: for the next meeting: how to edit albums & artists?
            logger.debug("albumEditButton");
            Album albumToEdit = listv_albums.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader();
            //add spring context to JavaFX
            fxmlLoader.setControllerFactory(appContext::getBean);
            Parent root = null;
            try {
                root = fxmlLoader.load(getClass().getResourceAsStream("/EditAlbum.fxml"));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            EditAlbumController editAlbumController = fxmlLoader.getController();
            editAlbumController.setAlbumForEdit(albumToEdit);

            Stage createEditStage = new Stage();
            createEditStage.setTitle("Edit Album");
            createEditStage.setScene(new Scene(root));
            createEditStage.initModality(Modality.APPLICATION_MODAL);
            createEditStage.showAndWait();
            refreshSongTable();
            controllerAlbumService.initAlbumListView(this);
            listv_albums.getSelectionModel().clearSelection();

        } else if (artists_tab) {
            logger.debug("artistEditButton");
            Artist artistToEdit = listv_artists.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader();
            //add spring context to JavaFX
            fxmlLoader.setControllerFactory(appContext::getBean);
            Parent root = null;
            try {
                root = fxmlLoader.load(getClass().getResourceAsStream("/EditArtist.fxml"));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            EditArtistController editArtistController = fxmlLoader.getController();
            editArtistController.setArtistToEdit(artistToEdit);

            Stage createEditStage = new Stage();
            createEditStage.setTitle("Edit Artist");
            createEditStage.setScene(new Scene(root));
            createEditStage.initModality(Modality.APPLICATION_MODAL);
            createEditStage.showAndWait();
            refreshSongTable();
            initArtistListView();
            listv_artists.getSelectionModel().clearSelection();
        }
    }

    /**
     * Configures the delete button below the tabs on the left side.
     * Depending to the tab which is now opened, it will removes a playlist, an artist or an album.
     * It does not only removes it from the list in the selected tab, it also delete the selected item from the library.
     * Deleting an artist will delete all songs and albums of him/her
     * Deleting an album will delete all songs in this album too but only the artist since he/her dont have any songs in the library.
     */
    @FXML
    public void deleteButtonAction() {
        if (playlists_tab) {
            logger.debug("playlistDeleteButton!");
            if (listv_playlists.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Warning Dialog", null, "Please select a Playlist!");
                return;
            }

            Playlist playlistForDelete = listv_playlists.getSelectionModel().getSelectedItem();

            ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Delete Playlist: '" + playlistForDelete.getName() + "' ?");

            if (result != ButtonType.OK) {
                listv_playlists.getSelectionModel().clearSelection();
                return;
            }
            if (playerService.getActivePlaylistId() == playlistForDelete.getId()) {
                stopButtonAction();
            }

            listv_playlists.getItems().remove(playlistForDelete);
            try {
                playlistService.remove(playlistForDelete.getId());
            } catch (ServiceException | ValidationException e) {
                logger.error(e.getMessage());
            }

            initPlayListView();
            playlistTableView.setItems(null);
            playlistTableView.layout();
            listv_playlists.getSelectionModel().clearSelection();

        } else if (artists_tab) {
            logger.debug("artistDeleteButton!");
            // if no artist is selected but the delete button was clicked.
            if (listv_artists.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Warning Dialog", null, "Please select an artist!");
                return;
            }
            //to find the usage of songs from this artist, which are in any playlists.
            List<SongDTO> songsInPlaylists = null;
            try {
                songsInPlaylists = playlistService.getAllSongs();
            } catch (ServiceException e) {
                e.getMessage();
            }
            logger.debug("Songs in playlist: " + songsInPlaylists.size());

            Artist artistToDelete = listv_artists.getSelectionModel().getSelectedItem();

            //confirmation dialog
            ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Delete Artist: '" + artistToDelete.getName() + "' with all of her/his songs?");

            if (result != ButtonType.OK) {
                listv_artists.getSelectionModel().clearSelection();
                return;
            }

            List<Song> songsToDelete = new ArrayList<Song>();
            for (Song s : artistToDelete.get(Type.SONG)) {
                songsToDelete.add(s);
            }
            for (SongDTO songDTO : songsInPlaylists) {
                for (Song songEntity : library.getAllSongs()) {
                    if (songDTO.getId() == songEntity.getId()) {
                        if (songDTO.getArtistId() == artistToDelete.getId()) {
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
                for (int i = 0; i < songsToDelete.size(); i++) {
                    Song s = songsToDelete.get(i);
                    logger.debug("remove song: '" + s.getName() + "' from library!");
                    library.removeSong(s); //
                }
                artistService.remove(artistToDelete.getId());
                listv_artists.getItems().remove(artistToDelete);

            } catch (ServiceException e) {
                logger.error(e.getMessage());
            } catch (ValidationException e) {
                logger.error(e.getMessage());
            }

            showAlert(Alert.AlertType.INFORMATION, "Information Dialog", null, "Artist: '" + artistToDelete.getName() + "' is removed!");

            initArtistListView();
            listv_artists.getSelectionModel().clearSelection();
            tv_song.getItems().removeAll(songsToDelete);

        } else if (albums_tab) {
            logger.debug("albumDeleteButton!");
            // if no album is selected but the delete button was clicked.
            controllerAlbumService.deleteAlbumAction(this);
        }
    }

    /**
     * removes a song from the playlist
     *
     * @param song which should be removed from the playlist
     */
    @FXML
    private void deleteSongFromPlaylistAction(Song song) {
        Playlist selectedPlaylist = listv_playlists.getSelectionModel().getSelectedItem();
        int songIndex = playlistTableView.getSelectionModel().getSelectedIndex();

        ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Delete song: '" + song.getName() + "' from this playlist?");

        if (result != ButtonType.OK) {
            playlistTableView.getSelectionModel().clearSelection();
            return;
        }

        if (songIndex == playerService.getActiveSongPosition()) {
            stopButtonAction();
        }

        playlistTableView.getItems().remove(songIndex);

        try {
            // update song position in the playlist after delete
            playlistService.updateSongsInPlaylist(selectedPlaylist.getId(),
                    controllerSongService.convertAllSongsFromTableView(playlistTableView.getItems(), library));
        } catch (ValidationException | ServiceException e) {
            logger.error(e.getMessage());
        }

        if (selectedPlaylist.getId() == playerService.getActivePlaylistId()) {
            if (songIndex < playerService.getActiveSongPosition()) {
                playerService.setActiveSongPosition(playerService.getActiveSongPosition() - 1);
            } else {
                playerService.setActiveSongPosition(playlistTableView.getItems().indexOf(playerService.getActiveSong()));
            }
            playerService.setActivePlaylist(playlistTableView.getItems());
        }
        //refreshPlayListTable();
        playlistTableView.getSelectionModel().clearSelection();
    }

    /**
     * @param song
     */
    private void songEditAction(Song song) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/EditSong.fxml"));
        } catch (IOException e) {
            logger.error(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error on edit song!", null, e.getMessage());
        }

        EditSongController editSongController = fxmlLoader.getController();
        editSongController.setSongForEdit(song);

        Stage createEditStage = new Stage();
        createEditStage.setTitle("Edit Song");
        Scene scene = new Scene(root);
        createEditStage.setScene(scene);
        createEditStage.initModality(Modality.APPLICATION_MODAL);
        createEditStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // todo: edit song in library
                initSongTable();
            }
        });
        createEditStage.showAndWait();
    }

    /**
     * todo: complete
     *
     * @param song which should be removed from the library
     */
    @FXML
    private void songDeleteAction(Song song) {
        ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Delete song: '" + song.getName() + "' from your library?");

        if (result != ButtonType.OK) {
            tv_song.getSelectionModel().clearSelection();
            return;
        }
        try {
            library.removeSong(song);
            tv_song.getItems().remove(song);
            refreshSongTable();
            if (playerService.getActivePlaylist().equals(tv_song.getItems())) {
                playerService.setActiveSongPosition(tv_song.getItems().indexOf(playerService.getActiveSong()));
            }
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to remove song");
        }
    }


    @FXML
    private void ringtoneSharefileImportAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Ringtone Sharefile you want to import");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Ringtone share files", "*.btu"));
        File defaultDirectory = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents");
        if (!defaultDirectory.isDirectory()) {
            defaultDirectory = new File(System.getProperty("user.home"));
        }
        fileChooser.setInitialDirectory(defaultDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Ringtone ringtone = new Ringtone();
            ringtone.setPath(selectedFile);

            final Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(ringtone.getPath()));

                ringtone.setBis(Integer.parseInt(properties.getProperty("bis")));
                ringtone.setVon(Integer.parseInt(properties.getProperty("von")));
                ringtone.setName(properties.getProperty("name"));


            } catch (IOException e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "No File", null, "ShareFile can not be read");
                return;
            }

            List<Song> songs = null;
            try {
                songs = library.findSongsByName(ringtone.getName());
            } catch (ServiceException e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
                return;
            }
            if (songs == null || songs.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "No File", null, "No 'Song with this Name");
                return;
            }
            for (Song s : songs) {
                if (s.getName().equals(ringtone.getName())) {
                    ringtone.setSong(s);
                    break;
                }
            }
            if (ringtone.getSong() == null) {
                showAlert(Alert.AlertType.ERROR, "No File", null, "No Song with this Name");
                return;
            }


            FXMLLoader fxmlLoader = new FXMLLoader();
            //add spring context to JavaFX
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> clazz) {
                    return appContext.getBean(clazz);
                }
            });

            Parent root = null;
            try {
                root = fxmlLoader.load(getClass().getResourceAsStream("/CreateRingtone.fxml"));
            } catch (IOException e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "No File", null, "Create Dialog can not be opened");
                return;
            }

            CreateRingtoneController createRingtoneController = fxmlLoader.getController();
            createRingtoneController.setSongToCut(ringtone.getSong());
            createRingtoneController.songName.setText("Ringtone creating from:   " + ringtone.getSong().getName());
            createRingtoneController.check_sharefile.setVisible(false);
            double vonPro = ((100 * (double) ringtone.getVon()) / (double) ringtone.getSong().getLength() / 100) / 1000;
            double bisPro = ((100 * (double) ringtone.getBis()) / (double) ringtone.getSong().getLength() / 100) / 1000;
            createRingtoneController.splitPaneWaveform.setDividerPosition(0, vonPro);
            createRingtoneController.splitPaneWaveform.setDividerPosition(1, bisPro);

            Stage createRingtoneStage = new Stage();
            createRingtoneStage.setTitle("Create Ringtone");
            Scene scene = new Scene(root);
            createRingtoneStage.setScene(scene);
            createRingtoneStage.initModality(Modality.APPLICATION_MODAL);

            createRingtoneStage.showAndWait();

        } else {
            logger.info("no directory selected");
        }
    }

    /**
     * creats a RingTone from RingtoneShare and saves it in File System
     *
     * @param song
     * @return path from saved Rington
     */
    @FXML
    public void createRingtone(Song song) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return appContext.getBean(clazz);
            }
        });
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/CreateRingtone.fxml"));
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "No File", null, "Create Dialog can not be opened");
        }

        CreateRingtoneController createRingtoneController = fxmlLoader.getController();
        createRingtoneController.setSongToCut(song);
        createRingtoneController.songName.setText("Song:   " + song.get(Type.ARTIST).getName() + " - " + song.getName());

        Stage createRingtoneStage = new Stage();
        createRingtoneStage.setTitle("Create Ringtone");
        Scene scene = new Scene(root);
        createRingtoneStage.setScene(scene);
        createRingtoneStage.initModality(Modality.APPLICATION_MODAL);

        createRingtoneStage.showAndWait();

        createRingtoneController.getPlayer().stop();
    }


    /**
     * Replace (remove old and add new) the song-table (all songs in the library) with the playlist-table (songs from the choosen playlist).
     * Refreshes the playlisttable after replacing
     */
    private void replaceSongTable() {
        if (playlistTableView != null) {
            bp_mainPane.getChildren().remove(playlistTableView);
            bp_mainPane.getChildren().remove(tv_song);
            bp_mainPane.setCenter(playlistTableView);
            //songTable = false;
            refreshPlayListTable();
        }
    }

    /**
     * Clears the song-table, reload the songs from the library and set it into the table again.
     */
    private void refreshSongTable() {
        songData = tv_song.getItems();
        tv_song.setItems(null);
        tv_song.layout();
        tv_song.setItems(songData);
        sortByArtist();
        //tgbt_playpausesong.disableProperty().bind(Bindings.size(songData).isEqualTo(0));
    }

    /**
     * todo: javaDoc!
     */
    private void refreshPlayListTable() {
        if (playlistTableView == null) {
            return;
        }
        songsFromPlaylistTable = playlistTableView.getItems();
        playlistTableView.setItems(null);
        playlistTableView.layout();
        playlistTableView.setItems(songsFromPlaylistTable);
        /*tgbt_playpausesong.disableProperty().bind(new BooleanBinding() {
            {
                bind(songsFromPlaylist);
            }
            @Override
            protected boolean computeValue() {
                return (songsFromPlaylist.size() == 0 && playerService.getActiveSong() == null) || songData.size() == 0;
            }
        });*/
    }

    public void refreshPlayListView() {
        int index = listv_playlists.getSelectionModel().getSelectedIndex();

        playLists = listv_playlists.getItems();
        listv_playlists.setItems(null);
        listv_playlists.layout();
        listv_playlists.setItems(playLists);
        listv_playlists.getSelectionModel().select(index);

    }

    @FXML
    private void volumeEdited() {
        playerService.setPower(sl_sound.getValue() / 100);
        setVolumeButton();
    }

    @FXML
    private void playPauseButtonAction() {
        Song song = getSongToPlay();
        playerService.setActiveSong(song);
        playerService.play(song);

        // adds a ChangeListener which will be notified whenever current
        // media time changes and updated the values.
        playerService.currentTimeDurationProperty().addListener((observable, oldValue, newValue) -> {
            updateValue();
        });

        // adds a ChangeListener which will be notified whenever the
        // StopRequestProperty changes
        playerService.stopRequestedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.booleanValue()) {
                tgbt_playpausesong.setSelected(true);
            } else {
                stopButtonAction();
                refreshSongTable();
            }
        });
    }

    /**
     * Check if a song is actually played and return this song or if no song is 'activ' now, it returns the first song
     * from the song-table (library)
     *
     * @return
     */
    private Song getSongToPlay() {
        Song song;
        if (playerService.getActiveSong() == null) {
            song = tv_song.getItems().get(0);
            setFirstSongToPlay(tv_song);
        } else {
            song = playerService.getActiveSong();
        }
        return song;
    }

    @FXML
    private void stopButtonAction() {
        tgbt_playpausesong.setSelected(false);
        playerService.stop();
    }

    @FXML
    public void nextSongButtonAction() {
        playerService.next();
    }

    @FXML
    public void previousSongButtonAction() {
        playerService.previous();
    }

    /**
     * Sets the image of the repeat button but not the functionality
     */
    @FXML
    private void repeatButtonAction() {
        if (playerService.isNormal()) {
            playerService.setRepeat(true);
            playerService.setNormal(false);
            playerService.setRepeatAll(false);
            setRepeatButtonImage();
            return;
        }

        if (playerService.isRepeat()) {
            playerService.setRepeatAll(true);
            playerService.setNormal(false);
            playerService.setRepeat(false);
            setRepeatAllButtonImage();
            return;
        }

        if (playerService.isRepeatAll()) {
            playerService.setNormal(true);
            playerService.setRepeat(false);
            playerService.setRepeatAll(false);
            setUnRepeatButtonImage();
            return;
        }
    }

    @FXML
    private void shuffleButtonAction() {
        if (tgbt_shuffle.isSelected()) {
            tgbt_shuffle.setOpacity(1.0);
            playerService.setShuffle(true);
        } else {
            tgbt_shuffle.setOpacity(0.3);
            playerService.setShuffle(false);
        }
    }

    /**
     * Try to synchronize the artist and album informations.
     * the user may choose which informations he would to synchronize.
     */
    /*@FXML
    private void synchronizeButtonAction() {
        //Map<EntityType, List<InfoRetriever>> retriever = new HashMap<>();
        //retriever.put(Type.ARTIST, new ArrayList<InfoRetriever>());
        //retriever.get(Type.ARTIST).add(new ArtistBioLastFMRetriever());

        // retrieve artist biography
        try {
            Artist selectedArtist = playerService.getActiveSong().get(Type.ARTIST);
            retrieveArtistBiography(selectedArtist);
            setArtistBiography();

        } catch (ServiceException e) {
            logger.error("failed to update artist biography information", e);
        }

        // todo: update artist/cover photo, concerts etc.
    }*/

    /**
     * Try to get the biography information from "LastFM" and write it in the lokal library to the artist.
     * This method will be called when you import new Songs from a directory to your media library
     * a login on the website with a key is requierd to get the biography
     *
     * @param artist which biography you want to get from LastFM
     * @throws ServiceException // todo: look at the service classes to complete
     */
    private void retrieveArtistBiography(Artist artist) throws ServiceException {
        ArtistBioLastFMRetriever artistBioLastFMRetriever = new ArtistBioLastFMRetriever();
        artistBioLastFMRetriever.retrieveInfo(artist, artistService);
    }


    private void retrieveArtistImage(Artist artist) throws ServiceException {
        ArtistImageWikiRetriever artistImageWikiRetriever = new ArtistImageWikiRetriever();
        artistImageWikiRetriever.retrieveInfo(artist, artistService);
    }

    private String retrieveConcerts(Artist artist) throws ServiceException {
        ConcertDataLastFMRetriever concertDataLastFMRetriever = new ConcertDataLastFMRetriever();
        concertDataLastFMRetriever.retrieveInfo(artist, artistService);
        return concertDataLastFMRetriever.getAusgabe();
    }

    private void retrieveSongLyrics(Song song) throws ServiceException {
        SongLyricsRetrieverImpl songLyricsRetriever = new SongLyricsRetrieverImpl();
        songLyricsRetriever.retrieveInfo(song, songService);
    }

    private void initBiography() {
        // disable horizontal scrollbar
        ta_bio.setWrapText(true);
    }

    private void initConcerts() {
        // disable horizontal scrollbar
        ta_tracklist.setWrapText(true);
    }

    private void initLyrics() {
        // disable horizontal scrollbar
        ta_lyrics.setWrapText(true);
    }

    @FXML
    private void createSimilarPlaylistAction() {
        Song playedSong = playerService.getActiveSong();
        createSimilarSongsPlaylist(playedSong);
    }

    private void createSimilarSongsPlaylist(Song selectedSong) {
        // todo: optimize?
        String songName = selectedSong.getName();
        String artistName = selectedSong.get(Type.ARTIST).getName();
        if (artistName.trim().equals("Unknown")) {
            showAlert(Alert.AlertType.ERROR, "Please select an other song", null, "The artist to the song is unknown." +
                    "\nPlease select a song where the artist is known to establish an automatically generated playlist.");
            return;
        }
        // confirmation of user
        ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null, "Do you want to create an automatically generated playlist out of song '" + songName + "'?");
        if (result != ButtonType.OK) {
            return;
        }

        // check for internet connection
        if (!isInternetReachable("http://www.last.fm")) {
            showAlert(Alert.AlertType.ERROR, "You are offline", null, "No internet connection available." +
                    "\nIn order to generate this playlist you have to have an internet connection.");
            return;
        }
        // generate playlist
        Playlist generatedPlaylist = null;
        try {
            generatedPlaylist = similarSongsPlaylistGenerator.generatePlaylist(selectedSong);
        } catch (ValidationException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        }
        // check if generated successfully or not
        if (generatedPlaylist == null) {
            showAlert(Alert.AlertType.ERROR, "Too few media files in bTunes", null, "You do not have enough music to generate a playlist.");
            return;
        }
        // successfully created playlist
        showAlert(Alert.AlertType.INFORMATION, "Information Dialog", null, "Playlist '" + generatedPlaylist.getName() +
                "' for song '" + songName + "'successfully created");
        // refresh playlist view
        initPlayListView();
    }

    private void createSimilarArtistsPlaylist(Artist selectedArtist) {
        // todo: optimize?
        // check whether artist unknown or not
        String artistName = selectedArtist.getName();
        if (artistName.trim().equals("Unknown")) {
            showAlert(Alert.AlertType.ERROR, "Please select an other artist", null, "The artist is unknown." +
                    "\nPlease select a known artist to establish an automatically generated playlist.");
            return;
        }
        // confirmation of user
        ButtonType result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation Dialog", null,
                "Do you want to create an automatically generated playlist out of artist '" + artistName + "'?");
        if (result != ButtonType.OK) {
            return;
        }
        // check for internet connection
        if (!isInternetReachable("http://www.last.fm")) {
            showAlert(Alert.AlertType.ERROR, "You are offline", null, "No internet connection available." +
                    "\nIn order to generate this playlist you have to have an internet connection.");
            return;
        }
        // generate playlist
        Playlist generatedPlaylist = null;
        try {
            generatedPlaylist = similarArtistsPlaylistGenerator.generatePlaylist(selectedArtist);
        } catch (ValidationException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            showErrorPlaylist();
            return;
        }
        // check if generated successfully or not
        if (generatedPlaylist == null) {
            showAlert(Alert.AlertType.ERROR, "Too few media files in bTunes", null,
                    "You do not have enough music to generate a playlist.");
            return;
        }
        // successfully created playlist
        showAlert(Alert.AlertType.INFORMATION, "Information Dialog", null, "Playlist '"
                + generatedPlaylist.getName() + "' for artist '" + artistName + "'successfully created");
        // refresh playlist view
        initPlayListView();
    }

    private void showErrorPlaylist() {
        showAlert(Alert.AlertType.ERROR, "Playlist creating failed.", null, "Failed to generate playlist!");
    }

    private void updateValue() {
        Platform.runLater(() -> {
            if (playerService.getMediaPlayer() == null) {
                lab_time.setText("");
                lab_duration.setText("");
                sl_time.setValue(0);
                return;
            }
            Duration currentTime = playerService.getCurrentTime();
            Duration duration = playerService.getDuration();
            lab_time.setText(currentTimeString(currentTime, duration));
            lab_duration.setText(durationTimeString(duration));
            if (duration.greaterThan(Duration.ZERO) && !sl_time.isValueChanging()) {
                sl_time.setValue(currentTime.toMillis() / (duration).toMillis() * 100);
            }
        });
    }


    private static String currentTimeString(Duration currentTime, Duration duration) {
        int intCurrentTime = (int) Math.floor(currentTime.toSeconds());
        int intDuration = (int) Math.floor(duration.toSeconds());
        int currentTimeHour, currentTimeMin, currentTimeSec;

        if (intDuration / 3600 > 0) {
            currentTimeHour = intCurrentTime / 3600;
            currentTimeMin = (intCurrentTime % 3600) / 60;
            currentTimeSec = intCurrentTime % 60;
            return String.format("%d:%02d:%02d", currentTimeHour, currentTimeMin, currentTimeSec);

        } else {
            currentTimeMin = (intCurrentTime % 3600) / 60;
            currentTimeSec = intCurrentTime % 60;
            return String.format("%02d:%02d", currentTimeMin, currentTimeSec);
        }
    }

    private static String durationTimeString(Duration duration) {

        int intDuration = (int) Math.floor(duration.toSeconds());
        int durationHour, durationMin, durationSec;

        if (intDuration / 3600 > 0) {
            durationHour = intDuration / 3600;
            durationMin = (intDuration % 3600) / 60;
            durationSec = intDuration % 60;
            return String.format("%d:%02d:%02d", durationHour, durationMin, durationSec);
        } else {
            durationMin = (intDuration % 3600) / 60;
            durationSec = intDuration % 60;
            return String.format("%02d:%02d", durationMin, durationSec);
        }
    }

    /**
     * Formats the biography text for the now-playing artist and set it in the right biography field
     */
    private void setArtistBiography() {
        Artist artist;
        String artistName;

        if (playerService.getMediaPlayer() != null) {
            Song playedSong = playerService.getActiveSong();
            artist = playedSong.get(Type.ARTIST);
            artistName = artist.getName();
        } else {
            artist = listv_artists.getSelectionModel().getSelectedItem();
            artistName = artist.getName();
        }

        String biography = artist.getBiography();
        ta_bio.setText("");
        if (isInternetReachable("http://www.last.fm")) {
            if (biography == null || (biography != null && biography.trim().equals(""))) {
                try {
                    retrieveArtistBiography(artist);
                    String biographyNew = artist.getBiography();
                    ta_bio.setText((biographyNew != null && !biographyNew.trim().equals("")) ? (artistName.toUpperCase() + "\n" + biographyNew) : "");
                } catch (ServiceException e) {
                    logger.error("failed to retrieve artist biography", e);
                }
            } else {
                ta_bio.setText(artistName.toUpperCase() + "\n");
                ta_bio.appendText(biography);
                //ta_bio.setScrollTop(0.0);
            }
        }
    }


    /**
     * Set the artist image for the now-playing song on the right side above the biography field (artist information)
     */
    private void setArtistImage() {
        Image artistImage = null;

        Artist artist;
        String image;

        if (playerService.getMediaPlayer() != null) {
            Song playedSong = playerService.getActiveSong();
            artist = playedSong.get(Type.ARTIST);
            image = artist.getImage();
        } else {
            artist = listv_artists.getSelectionModel().getSelectedItem();
            image = artist.getImage();
        }
        if (isInternetReachable("http://www.last.fm")) {
            if (image == null || (image.equals("")) || (image.contains("Artist.png"))) {
                try {
                    retrieveArtistImage(artist);
                    String newImage = artist.getImage();
                    if (newImage.contains("Artist.png")) {
                        artistImage = new Image(newImage);
                    } else {
                        artistImage = new Image(new File(newImage).toURI().toString());
                    }
                    img_artistimage.setImage(artistImage);
                } catch (ServiceException e) {
                    logger.error("failed to retrieve artist image", e);
                }
            } else {
                File file = new File(image);
                if (file.exists() && !file.isDirectory()) {
                    artistImage = new Image(file.toURI().toString());
                } else {
                    try {
                        retrieveArtistImage(artist);
                        String newImage = artist.getImage();
                        artistImage = new Image(new File(newImage).toURI().toString());
                    } catch (ServiceException e) {
                        logger.error("failed to retrieve artist image", e);
                    }

                }
                img_artistimage.setImage(artistImage);
            }
        }
    }

    private void setConcerts() {
        Artist artist;
        String artistName;
        if (tv_song.getSelectionModel().isEmpty() && listv_artists.getSelectionModel().isEmpty()) {
            return;
        }
        if (playerService.getMediaPlayer() != null) {
            Song playedSong = playerService.getActiveSong();
            artist = playedSong.get(Type.ARTIST);
            artistName = artist.getName();
        }
        else {
            artist = listv_artists.getSelectionModel().getSelectedItem();
            artistName = artist.getName();
        }
        String concert = "";
        ta_tracklist.setText("");
        if (isInternetReachable("http://www.last.fm")) {
            if (concert == null || (concert != null && concert.trim().equals(""))) {
                try {

                    String concertNew = retrieveConcerts(artist);
                    ta_tracklist.setText((concertNew != null && !concertNew.trim().equals("")) ? concertNew : "");
                } catch (ServiceException e) {
                    logger.error("failed to retrieve artist concert", e);
                }
            } else {
                ta_tracklist.appendText(concert);
                //ta_bio.setScrollTop(0.0);
            }
        }
    }

    /**
     * Set the lyrics for the now-playing song
     */
    private void setSongLyrics() {
        Song playedSong = null;
        if (playerService.getMediaPlayer() != null) {
            playedSong = playerService.getActiveSong();
        }

        String lyrics = playedSong.getLyrics();

        // check for internet connection
        if (isInternetReachable("http://www.azlyrics.com")) {
            if (lyrics == null || lyrics.equals("")) {
                try {
                    retrieveSongLyrics(playedSong);
                    String lyricsNew = playedSong.getLyrics();
                    ta_lyrics.setText((lyricsNew != null && !lyricsNew.equals("")) ? lyricsNew : "No Lyrics available");
                } catch (ServiceException e) {
                    logger.error("failed to retrieve song lyrics", e);
                }
            } else {
                ta_lyrics.setText(lyrics);
            }
        }
    }


    /**
     * set now-playing artist-name and-song titel in the bottom-left corner
     */
    private void setSongLabel() {
        Song playedSong = playerService.getActiveSong();
        String artistName = playedSong.get(Type.ARTIST).getName();
        String title = playedSong.getName();
        lab_songname.setText(artistName + " - " + title);
    }

    private void setFirstSongToPlay(TableView<Song> tableView) {
        playerService.setActiveSong(tableView.getItems().get(0));
        playerService.setActiveSongPosition(0);
        playerService.setActivePlaylist(tableView.getItems());
        playerService.setActivePlaylistId(getActiveSongsListId());
    }

    private void setButtonsImage() {
        setPlayPauseButtonImage();
        setStopButtonImage();
        setNextButtonImage();
        setPreviousButtonImage();
        setUnRepeatButtonImage();
        setShuffleButtonImage();
        setCreatePlaylistButtonImage();
        setDJButtonImage();
        bt_similarplaylist.setDisable(true);
        //setDeletePlaylistButtonImage();
        //setEditPlaylistButtonImage();
        setSoundButtonImage(soundMidImage);
        //setSynchronizeButtonImage();
        setCreateSimilarPlaylistButtonImage();
        setYoutubeButtonImage();
        setLibraryButtonImage();
    }

    private void setPlayPauseButtonImage() {
        Image unselected = new Image("/images/play.png");
        Image selected = new Image("/images/pause.png");
        ImageView toggleImage = new ImageView();
        toggleImage.setFitWidth(tgbt_playpausesong.getPrefWidth());
        toggleImage.setFitHeight(tgbt_playpausesong.getPrefHeight());
        tgbt_playpausesong.setPadding(Insets.EMPTY);
        tgbt_playpausesong.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                        .when(tgbt_playpausesong.selectedProperty())
                        .then(selected)
                        .otherwise(unselected)
        );
    }

    private void setStopButtonImage() {
        Image stopImage = new Image("/images/stop.png");
        ImageView imageView = new ImageView(stopImage);
        imageView.setFitWidth(bt_stopsong.getPrefWidth());
        imageView.setFitHeight(bt_stopsong.getPrefHeight());
        bt_stopsong.setPadding(Insets.EMPTY);
        bt_stopsong.setGraphic(imageView);
    }

    private void setYoutubeButtonImage() {
        Image youtubeImage = new Image("/images/youtube_main.png");
        ImageView imageView = new ImageView(youtubeImage);
        imageView.setFitWidth(youtube_btn.getPrefWidth());
        imageView.setFitHeight(youtube_btn.getPrefHeight());
        youtube_btn.setPadding(Insets.EMPTY);
        youtube_btn.setGraphic(imageView);

    }

    private void setNextButtonImage() {
        Image nextImage = new Image("/images/next.png");
        ImageView imageView = new ImageView(nextImage);
        imageView.setFitWidth(bt_nextsong.getPrefWidth());
        imageView.setFitHeight(bt_nextsong.getPrefHeight());
        bt_nextsong.setPadding(Insets.EMPTY);
        bt_nextsong.setGraphic(imageView);
    }

    private void setPreviousButtonImage() {
        Image previousImage = new Image("/images/previous.png");
        ImageView imageView = new ImageView(previousImage);
        imageView.setFitWidth(bt_previoussong.getPrefWidth());
        imageView.setFitHeight(bt_previoussong.getPrefHeight());
        bt_previoussong.setPadding(Insets.EMPTY);
        bt_previoussong.setGraphic(imageView);
    }

    private void setLibraryButtonImage() {
        Image previousImage = new Image("/images/library.png");
        ImageView imageView = new ImageView(previousImage);
        imageView.setFitWidth(bt_mediaLibrary.getPrefWidth());
        imageView.setFitHeight(bt_mediaLibrary.getPrefHeight());
        bt_mediaLibrary.setPadding(Insets.EMPTY);
        bt_mediaLibrary.setGraphic(imageView);
    }

    private void setRepeatButtonImage() {
        Image previousImage = new Image("/images/repeatOne.png");
        ImageView imageView = new ImageView(previousImage);
        imageView.setFitWidth(bt_repeat.getPrefWidth());
        imageView.setFitHeight(bt_repeat.getPrefHeight());
        bt_repeat.setPadding(Insets.EMPTY);
        bt_repeat.setGraphic(imageView);
        bt_repeat.setOpacity(1.0);
    }

    private void setRepeatAllButtonImage() {
        Image previousImage = new Image("/images/repeatAll.png");
        ImageView imageView = new ImageView(previousImage);
        imageView.setFitWidth(bt_repeat.getPrefWidth());
        imageView.setFitHeight(bt_repeat.getPrefHeight());
        bt_repeat.setPadding(Insets.EMPTY);
        bt_repeat.setGraphic(imageView);
        bt_repeat.setOpacity(1.0);
    }

    private void setUnRepeatButtonImage() {
        Image previousImage = new Image("/images/repeatAll.png");
        ImageView imageView = new ImageView(previousImage);
        imageView.setFitWidth(bt_repeat.getPrefWidth());
        imageView.setFitHeight(bt_repeat.getPrefHeight());
        bt_repeat.setPadding(Insets.EMPTY);
        bt_repeat.setGraphic(imageView);
        bt_repeat.setOpacity(0.3);
    }

    private void setDJButtonImage() {
        Image djImage = new Image("/images/startDJ.png");
        ImageView imageView = new ImageView(djImage);
        imageView.setFitWidth(dj_btn.getPrefWidth());
        imageView.setFitHeight(dj_btn.getPrefHeight());
        dj_btn.setPadding(Insets.EMPTY);
        dj_btn.setGraphic(imageView);
    }

    private void setSoundButtonImage(Image soundImage) {
        ImageView imageView = new ImageView(soundImage);
        imageView.setFitWidth(bt_sound.getPrefWidth());
        imageView.setFitHeight(bt_sound.getPrefHeight());
        bt_sound.setPadding(Insets.EMPTY);
        bt_sound.setGraphic(imageView);
    }

    private void setShuffleButtonImage() {
        Image unselected = new Image("/images/shuffle.png");
        Image selected = new Image("/images/shuffle.png");
        ImageView toggleImage = new ImageView();
        toggleImage.setFitWidth(tgbt_shuffle.getPrefWidth());
        toggleImage.setFitHeight(tgbt_shuffle.getPrefHeight());
        tgbt_shuffle.setPadding(Insets.EMPTY);
        tgbt_shuffle.setOpacity(0.3);
        tgbt_shuffle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                        .when(tgbt_shuffle.selectedProperty())
                        .then(selected)
                        .otherwise(unselected)
        );
    }

    private void setCreatePlaylistButtonImage() {
        Image createImage = new Image("/images/create.png");
        ImageView imageView = new ImageView(createImage);
        imageView.setFitWidth(bt_create.getPrefWidth());
        imageView.setFitHeight(bt_create.getPrefHeight());
        bt_create.setPadding(Insets.EMPTY);
        bt_create.setGraphic(imageView);
    }

    private void setCreateSimilarPlaylistButtonImage() {
        Image createSimilarPlaylistImage = new Image("/images/similarplaylist.png");
        ImageView imageView = new ImageView(createSimilarPlaylistImage);
        imageView.setFitWidth(bt_similarplaylist.getPrefWidth());
        imageView.setFitHeight(bt_similarplaylist.getPrefHeight());
        bt_similarplaylist.setPadding(Insets.EMPTY);
        bt_similarplaylist.setGraphic(imageView);
    }

    private void setUpdateAlbumButtonImage() {
        Image createImage = new Image("/images/update.png");
        ImageView imageView = new ImageView(createImage);
        imageView.setFitWidth(bt_create.getPrefWidth());
        imageView.setFitHeight(bt_create.getPrefHeight());
        bt_create.setPadding(Insets.EMPTY);
        bt_create.setGraphic(imageView);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTabs();
        setButtonsImage();
        initSongTable();
        initPlayListView();
        initArtistListView();
        controllerAlbumService.initAlbumListView(this);
        initLyrics();
        initBiography();
        initConcerts();
        sl_sound.setValue(50);

        listv_playlists.setOnMouseClicked(event -> {
            initPlaylistSongTable();
            playlistTabSelected(event);

            // if no song in the table is selected and play button is
            // pressed, the first song will be played
            if (playerService.getMediaPlayer() == null) {
                if (playlistTableView != null && playlistTableView.getItems().size() != 0) {
                    setFirstSongToPlay(playlistTableView);
                }
            }
            replaceSongTable();
        });

        listv_artists.setOnMouseClicked(event -> {
            initArtistListView();
            artistTabSelected(event);
            if (tab_concerts.isSelected()) {
                setConcerts();
            }
            setArtistImage();
            setArtistBiography();
        });

        listv_albums.setOnMouseClicked(event -> {
            controllerAlbumService.initAlbumListView(this);
            albumTabSelected(event);
            img_albumcover.setImage(controllerAlbumService.setAlbumCover(playerService, listv_albums));
            controllerAlbumService.setAlbumName(playerService, listv_albums, tf_albumname);
        });

        // adds an InvalidationListener which will be notified whenever
        // the slider-position manually changed.
        sl_time.valueProperty().addListener(observable -> {
            if (sl_time.isValueChanging() && playerService.getMediaPlayer() != null) {
                playerService.seekPosition(playerService.getDuration().multiply(sl_time.getValue() / 100));
            }
        });

        sl_sound.valueProperty().addListener(observable -> {
            if (sl_sound.isValueChanging() && playerService.getMediaPlayer() != null) {
                playerService.setPower(sl_sound.getValue() / 100);
                setVolumeButton();
            }
        });

        // adds a ChangeListener which will be notified whenever media changes.
        playerService.mediaChangedProperty().addListener((observable, oldValue, newValue) -> {
            refreshPlayListTable();
            refreshSongTable();
            logger.info("mediaChanged: " + newValue);
            if (newValue.equals("")) {
                lab_songname.setText("");
                bt_similarplaylist.setDisable(true);
                return;
            }
            setSongLabel();
            controllerAlbumService.setAlbumName(playerService, listv_albums, tf_albumname);
            img_albumcover.setImage(controllerAlbumService.setAlbumCover(playerService, listv_albums));
            setArtistImage();
            setArtistBiography();
            if (tab_concerts.isSelected()) {
                setConcerts();
            }
            setSongLyrics();
            bt_similarplaylist.setDisable(false);

            refreshPlayListView();
        });

        tf_search.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                searchAction();
            }
        });

        sl_sound.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                volumeEdited();
            }
        });

        // clears Selection if we drag a song
        tv_song.setOnMouseDragged(event -> listv_playlists.getSelectionModel().clearSelection());
    }

    private void setVolumeButton() {
        if (sl_sound.getValue() >= 50) {
            setSoundButtonImage(soundMaxImage);
        } else if (sl_sound.getValue() < 50 && sl_sound.getValue() > 1) {
            setSoundButtonImage(soundMidImage);
        } else {
            setSoundButtonImage(soundMinImage);
        }
    }

    @FXML
    private void searchAction() {
        try {
            // filter by songnames
            List<Song> filteredSongs = library.findSongsByName(tf_search.getText());
            List<Album> filteredAlbums = library.findAlbumsByName(tf_search.getText());
            List<Artist> filteredArtists = library.findArtistsByName(tf_search.getText());

            List<Song> songsToAddToFilteredSongs;

            for (Album album : filteredAlbums) {
                songsToAddToFilteredSongs = album.get(Type.SONG);
                for (Song s : songsToAddToFilteredSongs) {
                    if (!filteredSongs.contains(s)) {
                        filteredSongs.add(s);
                    }
                }
            }

            for (Artist artist : filteredArtists) {
                songsToAddToFilteredSongs = artist.get(Type.SONG);
                for (Song s : songsToAddToFilteredSongs) {
                    if (!filteredSongs.contains(s)) {
                        filteredSongs.add(s);
                    }
                }
            }

            songData = FXCollections.observableList(filteredSongs);

            tv_song.setItems(null);
            tv_song.layout();
            tv_song.setItems(songData);
            sortByArtist();
            tgbt_playpausesong.disableProperty().bind(Bindings.size(songData).isEqualTo(0));
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    public void djButtonAction(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(appContext::getBean);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResourceAsStream("/DjPlaylists.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SelectPlaylistController selectPlaylistController = fxmlLoader.getController();
        selectPlaylistController.setMainController(this);
        selectPlaylistController.setPlaylist(listv_playlists.getItems());

        Stage djStage = new Stage();
        djStage.setTitle("Select Playlist");
        djStage.setScene(new Scene(root));
        djStage.setResizable(false);
        djStage.initModality(Modality.APPLICATION_MODAL);
        djStage.showAndWait();

    }


    private void setTabs() {
        playlists_tab = false;
        artists_tab = false;
        albums_tab = false;
    }

    public void artistTabSelected(Event event) {
        initArtistListView();
        artists_tab = true;
        playlists_tab = false;
        albums_tab = false;
    }

    public void playlistTabSelected(Event event) {
        playlists_tab = true;
        artists_tab = false;
        albums_tab = false;
    }

    public void albumTabSelected(Event event) {
        controllerAlbumService.initAlbumListView(this);
        albums_tab = true;
        playlists_tab = false;
        artists_tab = false;
    }

    public void playlistDeleteAction(ActionEvent actionEvent) {
        playlists_tab = true;
        artists_tab = false;
        albums_tab = false;
        deleteButtonAction();
    }

    public void playlistEditAction(ActionEvent actionEvent) {
        playlists_tab = true;
        artists_tab = false;
        albums_tab = false;
        editButtonAction();
    }


    //checks for connection to the internet through dummy request
    public static boolean isInternetReachable(String urlString) {
        try {
            //make a URL to a known source
            URL url = new URL(urlString);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            logger.error("internet connection failed -" + e.getMessage());
            return false;
        } catch (IOException e) {
            logger.error("internet connection failed -" + e.getMessage());
            return false;
        }
        return true;
    }

    public ListView<Playlist> getListv_playlists() {
        return listv_playlists;
    }

    private int getActiveSongsListId() {
        if (playlists_tab && !listv_playlists.getSelectionModel().isEmpty()) {
            return listv_playlists.getSelectionModel().getSelectedItem().getId();
        } else if (artists_tab && !listv_artists.getSelectionModel().isEmpty()) {
            return listv_artists.getSelectionModel().getSelectedItem().getId();
        } else if (albums_tab && !listv_albums.getSelectionModel().isEmpty()) {
            return listv_albums.getSelectionModel().getSelectedItem().getId();
        } else {
            return -1;
        }
    }

    private void setPlayerVariables(TableView<Song> tableView, TableRow<Song> tableRow) {
        playerService.setActivePlaylist(tableView.getItems());
        playerService.setActiveSong(tableRow.getItem());
        playerService.setActiveSongPosition(tableRow.getIndex());
        playerService.setActivePlaylistId(getActiveSongsListId());
        playerService.setShuffleList(null);
    }

    @FXML
    public void soundButtonAction(ActionEvent actionEvent) {
        if (sl_sound.getValue() >= 1) {
            temporaryPower = sl_sound.getValue();
            setSoundButtonImage(soundMuteImage);
            sl_sound.setValue(0);
            playerService.setPower(0);
        } else if (sl_sound.getValue() == 0) {
            sl_sound.setValue(temporaryPower);
            volumeEdited();
            temporaryPower = 0.0;
        }
    }

    @FXML
    public void mediaLibraryClicked(ActionEvent actionEvent) {
        initSongTable();
        setTabs();
        listv_playlists.getSelectionModel().clearSelection();

        // if no song in the table is selected and play button is
        // pressed, the first song will be played
        if (playerService.getMediaPlayer() == null) {
            if (tv_song != null && tv_song.getItems().size() != 0) {
                setFirstSongToPlay(tv_song);
            }
        }
        replacePlaylistTable();
    }

    public ButtonType showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert error = new Alert(alertType);
        error.setTitle(title);
        error.setHeaderText(headerText);
        error.setContentText(contentText);
        error.showAndWait();
        return error.getResult();
    }

    public Library getLibrary() {
        return library;
    }

    public ListView<Album> getListViewAlbums() {
        return listv_albums;
    }

    public TableView<Song> getTableViewSong() {
        return tv_song;
    }

    public TableView<Song> getTableViewPlaylist() {
        return playlistTableView;
    }

    public Player getPlayer() {
        return playerService;
    }

    public PlayerMainController getMainController() {
        return this;
    }

    @FXML
    public void tabConcertSelected(Event event) {
        setConcerts();
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.appContext = applicationContext;
    }
    
    /*public void changeStyleToDarkBlue(ActionEvent actionEvent) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(darkBlueTheme);
    }

    public void changeStyleToFlashRed(ActionEvent actionEvent) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(flashRedTheme);
    }*/
}
