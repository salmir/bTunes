package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Genre;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.service.retriever.InfoRetriever;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lena Lenz.
 */
public interface Library {


    public List<Song> getAllSongs();
    public List<Album> getAllAlbums();
    public List<Artist> getAllArtists();
    public List<Genre> getAllGenres();

    public Map<String,Song> getSongMap();
    public Map<String,Album> getAlbumMap();
    public Map<String,Artist> getArtistMap();
    public Map<String,Genre> getGenreMap();

    public List<Song> findSongsByName(String name) throws ServiceException;
    public List<Artist> findArtistsByName(String name) throws ServiceException;
    public List<Album> findAlbumsByName(String name) throws ServiceException;

    /**
     *
     * @throws ServiceException
     */
    public void initialize() throws ServiceException;

    /**
     *
     * @param file
     * @param retriever
     * @return
     * @throws ServiceException
     */
    public default List<Song> importFolder(File file, Map<EntityType, List<InfoRetriever>> retriever) throws ServiceException {
        List<Song> importedSongs = new ArrayList<>();
        if(file.exists()){
            for(File f : file.listFiles()){
                if(!f.isDirectory()){
                    Song importedSong = importSong(f, retriever);
                    if(importedSong != null) {
                        importedSongs.add(importedSong);
                    }
                }else{
                    importFolder(f, retriever);
                }
            }
        }else{
            throw new ServiceException.ServiceFileNotFoundException(file);
        }
        return importedSongs;
    }

    /**
     *
     *
     * @param songFile
     * @throws ServiceException
     */
    public default void importSong(File songFile) throws ServiceException {
        if(!songFile.getPath().endsWith(".mp3")) {
            //throw new ServiceException("File is not in mp3-format!");
            return;
        }
        importSong(songFile, null);
    }

    /**
     *
     *
     * @param song
     * @param retriever
     * @throws ServiceException
     */
    public Song importSong(File song, Map<EntityType, List<InfoRetriever>> retriever) throws ServiceException;

    /**
     *
     * @param songDTO
     * @param retriever
     * @return
     * @throws ServiceException
     */
    public Song updateSong(SongDTO songDTO, Map<EntityType, List<InfoRetriever>> retriever) throws ServiceException;

    /**
     *
     *
     * @param song
     * @throws ServiceException
     */
    public void removeSong(Song song) throws ServiceException;

    /**
     *
     *
     * @param toAdd
     * @param type
     * @param <A>
     */
    public <A extends Entity> void addListener(EntityModifiedListener<A> toAdd, EntityType<A> type);

    void updateArtist(ArtistDTO artist) throws ServiceException;

    void updateAlbum(AlbumDTO album) throws ServiceException;

    /**
     * Listener for Entity
     * if any object maintained by Library is removed, all corresponding listeners get notified
     * @param <A> Entity
     */
    public interface EntityModifiedListener<A extends Entity>{
        public void entityAdded(A entity);
        public void beforeRemove(A entity);
        public void afterRemove(A entity);
    }

    /**
     * Returns the song with the specified unique ID
     * @param id Unique identifier
     * @return found song or null
     */
    public Song getSongById(Integer id);
}
