package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Genre;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.DaoEntity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.AlbumService;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import at.ac.tuwien.sepm.musicplayer.service.retriever.InfoRetriever;
import com.mpatric.mp3agic.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Lena Lenz.
 */
@Service
@Configurable
public class LibraryImpl implements Library {

    private static Logger logger = Logger.getLogger(LibraryImpl.class);

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;


    private Map<EntityType, List<EntityModifiedListener>> modifiedListeners = new HashMap<>();

    private Map<String, Song> songs = new HashMap<>(); // (Pathname, Song)
    private Map<String, Artist> artists = new HashMap<>(); // (Artistname, Artist)
    private Map<String, Album> albums = new HashMap<>(); // (albumname+artistname, Album)
    private Map<String, Genre> genres = new HashMap<>(); // (Genrename, Genre)

    @PostConstruct
    public void initialize() throws ServiceException {
        logger.info("initializing library");
        if(artistService == null || albumService == null || songService == null) {
            // should never occur
            logger.error(" SERVICE(S) NULL");
            return;
        }
        try {
            // init stored artists
            List<ArtistDTO> storedArtists = artistService.readAll();
            if(storedArtists != null) {
                for (ArtistDTO artistDto : storedArtists) {
                    addArtist(artistDto, null);
                    //logger.info("artistmap: "+ artists.toString());
                    //artists.put(artistDto.getIdentifier(), artistDto.createNew());
                }
            }
            // init stored albums
            List<AlbumDTO> storedAlbums = albumService.readAll();
            if(storedAlbums != null) {
                for (AlbumDTO albumDTO : storedAlbums) {
                    addAlbum(albumDTO, null);
                    //logger.info("albummap: "+ albums.toString());
                    //albums.put(albumDTO.getIdentifier(), albumDTO.createNew());
                }
            }
            // init stored songs
            List<SongDTO> storedSongs = songService.readAll();
            if(storedSongs != null) {
                for (SongDTO songDto : storedSongs) {
                    try {
                        addSong(songDto, null);
                    } catch (FileNotFoundException e) {
                        try {
                            songService.remove(songDto.getId());
                        } catch (ValidationException e1) {
                            logger.error(e1.getMessage());
                        }
                        logger.error("File not found: " + e.getMessage());
                    }
                }
            }
        } catch(NullPointerException e) {
            logger.error("!Some Service(s) not correctly implemented yet!\nException is only allowed to be thrown before service implementation done.");
            throw new ServiceException("!Some Service(s) not correctly implemented yet!\n" +
                    "Exception is only allowed to be thrown before service implementation done. "+ e.getMessage());
        } catch(PersistenceException e) {
            logger.error("Failed to import song");
            throw new ServiceException("Failed to import song: "+ e.getMessage());
        }
        logger.info("library initialized.");
    }

    @Override
    public Song importSong(File songFile, Map<EntityType, List<InfoRetriever>> retriever) throws ServiceException {
        if(!songFile.exists()){
            throw new ServiceException.ServiceFileNotFoundException(songFile);
        }
        if(!songFile.getPath().endsWith(".mp3")) {
            //throw new ServiceException("File is not in mp3-format!");
            logger.error("File is not in mp3-format!");
            return null;
        }
        // read Id3-tags
        SongDTO songDto = readID3Tags(songFile);
        try {
            return addSong(songDto, retriever);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + songFile.getAbsolutePath());
            throw new ServiceException.ServiceFileNotFoundException(songFile);
        } catch (PersistenceException e) {
            logger.error("Failed to add Song", e);
            throw new ServiceException("Failed to add Song", e);
        }
    }

    private SongDTO readID3Tags(File songFile) throws ServiceException {
        // use of mp3agic-library
        SongDTO songDTO = new SongDTO(songFile.getAbsolutePath());
        try {
            Mp3File mp3File = new Mp3File(songFile.getAbsolutePath());
            songDTO.setLength(mp3File.getLengthInSeconds());
            if(mp3File.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();

                // title
                if(mp3File.hasId3v2Tag()) {
                    // v2tag
                    if (id3v1Tag.getTitle().equals(null) || id3v1Tag.getTitle().trim().equals("")) {
                        songDTO.setName(songFile.getName());
                    } else {
                        songDTO.setName(id3v2Tag.getTitle());
                    }
                } else {
                    // v1tag
                    String title = id3v1Tag.getTitle();
                    if (title.trim().equals("")) {
                        songDTO.setName(songFile.getName());
                    } else {
                        songDTO.setName(id3v1Tag.getTitle());
                    }
                }
                // year
                String year = id3v1Tag.getYear();
                if(!year.trim().equals("")) {
                    try {
                        songDTO.setReleaseYear(Integer.parseInt(year));
                    }
                    catch(NumberFormatException numberFormatException) {
                        logger.error(numberFormatException.getMessage());
                    }
                }
                // artist
                String artistName = id3v1Tag.getArtist();
                if(artistName.trim().equals("")) {
                    songDTO.setArtistName("Unknown");
                }
                else {
                    songDTO.setArtistName(artistName);
                }
                // album
                String albumName = id3v1Tag.getAlbum();
                if(albumName.trim().equals("")) {
                    songDTO.setAlbumName("Unknown");
                }
                else {
                    songDTO.setAlbumName(albumName);
                }
                // genre
                String genre = id3v1Tag.getGenreDescription();
                if(genre.trim().equals("")) {
                    songDTO.setGenre("Unknown");
                }
                else {
                    songDTO.setGenre(id3v1Tag.getGenreDescription());
                }
                //logger.info("track: "+ id3v1Tag.getTrack());
                //logger.info("title: "+ title);
                //logger.info("year: "+ year);
                //logger.info("artist: "+ artistName);
                //logger.info("album: "+ albumName);
                //logger.info("genre: "+ genre);
                //logger.info("songfilename: "+ songFile.getName());
            }
            else {
                // no tags => set everything to 'Unknown'
                songDTO.setName(songFile.getName());
                songDTO.setArtistName("Unknown");
                songDTO.setAlbumName("Unknown");
                songDTO.setGenre("Unknown");
            }
        } catch (IOException e) {
            logger.error("Failed to load id3-tags: "+ e.getMessage());
            throw new ServiceException("Failed to load id3-tags: "+ e.getMessage());
        } catch (UnsupportedTagException e) {
            logger.error("Id3tag-Unsupported tag: "+ e.getMessage());
            throw new ServiceException("Id3tag-Unsupported tag: "+ e.getMessage());
        } catch (InvalidDataException e) {
            logger.error("Id3-tag: "+ e.getMessage());
            throw new ServiceException("Id3tag: "+ e.getMessage());
        }

        return songDTO;
    }

    @Override
    public void removeSong(Song song)throws ServiceException {
        notifyRemove(song);
        // remove song from all infos it owns
        song.removeFromAllInfos();
        try {
            remove(song, songService, songs);
            //logger.info("hasgenre: " + song.has(Type.GENRE));
            //logger.info("songs to genre: "+ song.get(Type.GENRE).get(Type.SONG).toString());
            if (song.has(Type.GENRE) && song.get(Type.GENRE).get(Type.SONG).isEmpty()) {
                logger.info("genre now empty => delete");
                removeGenre(song.get(Type.GENRE));
            }
            try {
                //logger.info("hasalbum: "+ song.has(Type.ALBUM));
                //logger.info("songs to album: " + song.get(Type.ALBUM).get(Type.SONG).toString());
                if (song.has(Type.ALBUM) && song.get(Type.ALBUM).get(Type.SONG).isEmpty()) {
                    logger.info("album now empty => delete");
                    removeAlbum(song.get(Type.ALBUM));
                }
            } catch(PersistenceException e){
                // should never occur
                logger.error("Failed to remove Album of removed Song", e);
            } catch(ValidationException e){
                // should never occur
                logger.error("invalid album", e);
            }
            try {
                //logger.info("hasartist: "+ song.has(Type.ARTIST));
                //logger.info("songs to artist: "+ song.get(Type.ARTIST).get(Type.SONG).toString());
                if (song.has(Type.ARTIST) && song.get(Type.ARTIST).get(Type.SONG).isEmpty()) {
                    logger.info("artist now empty => delete");
                    removeArtist(song.get(Type.ARTIST));
                }
            } catch(PersistenceException e){
                // should never occur
                logger.error("Failed to remove Artist of removed Song", e);
            } catch (ValidationException e) {
                logger.error("invalid artist", e);
            }
            song.removeAllInfos();
        }catch (PersistenceException e){
            throw new ServiceException();
        }
        catch (ValidationException e) {
            logger.error("invalid song", e);
            throw new ServiceException();//todo
        }
    }

    @Override
    public <A extends Entity> void addListener(EntityModifiedListener<A> toAdd, EntityType<A> type){
        if(!modifiedListeners.containsKey(type)){
            modifiedListeners.put(type, new LinkedList<>());
        }
        modifiedListeners.get(type).add(toAdd);
    }

    @Override
    public Song getSongById(Integer id) {
        return null;
    }

    @Override
    public List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<>();
        for(Song song : songs.values()) {
            songList.add(song);
        }
        return songList;
    }

    @Override
    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<>();
        for(Album album : albums.values()) {
            albumList.add(album);
        }
        return albumList;
    }

    @Override
    public List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();
        for(Artist artist : artists.values()) {
            artistList.add(artist);
        }
        return artistList;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genreList = new ArrayList<>();
        for(Genre genre : genres.values()) {
            genreList.add(genre);
        }
        return genreList;
    }

    @Override
    public Map<String, Song> getSongMap() {
        return songs;
    }

    @Override
    public Map<String, Album> getAlbumMap() {
        return albums;
    }

    @Override
    public Map<String, Artist> getArtistMap() {
        return artists;
    }

    @Override
    public Map<String, Genre> getGenreMap() {
        return genres;
    }

    @Override
    public List<Song> findSongsByName(String name) throws ServiceException {
        try {
            List<SongDTO> songDTOList = songService.findByName(name);
            List<Song> filteredSongs = new ArrayList<>();
            for(SongDTO songDTO : songDTOList) {
                if(songs.containsKey(songDTO.getIdentifier())) {
                    filteredSongs.add(songs.get(songDTO.getIdentifier()));
                }
            }
            return filteredSongs;
        } catch (ValidationException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Artist> findArtistsByName(String name) throws ServiceException {
        try {
            List<ArtistDTO> artistDTOList = artistService.findByName(name);
            List<Artist> filteredArtists = new ArrayList<>();

            for(ArtistDTO artistDTO : artistDTOList) {
                if(artists.containsKey(artistDTO.getIdentifier())) {
                    filteredArtists.add(artists.get(artistDTO.getIdentifier()));
                }
            }
            return filteredArtists;
        } catch (ValidationException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Album> findAlbumsByName(String name) throws ServiceException {
        try {
            List<AlbumDTO> albumDTOList = albumService.findByName(name);
            List<Album> filteredAlbums = new ArrayList<>();
            for(AlbumDTO albumDTO : albumDTOList) {
                if(albums.containsKey(albumDTO.getIdentifier())) {
                    filteredAlbums.add(albums.get(albumDTO.getIdentifier()));
                }
            }
            return filteredAlbums;
        } catch (ValidationException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }


    // private methods

    private void removeGenre(Genre genre) {
        notifyRemove(genre);
        remove(genre);
    }

    private void removeAlbum(Album album) throws ServiceException, ValidationException, PersistenceException {
        notifyRemove(album);
        remove(album, albumService, albums);
    }

    private void removeArtist(Artist artist) throws ServiceException, ValidationException, PersistenceException {
        notifyRemove(artist);
        remove(artist, artistService, artists);
    }

    private Artist addArtist(ArtistDTO toAdd, Map<EntityType, List<InfoRetriever>> retriever) throws PersistenceException{
        try {
            return convert(toAdd, artists, artistService, retriever);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //should never be thrown
            return null;
        }
    }

    private Album addAlbum(AlbumDTO toAdd, Map<EntityType, List<InfoRetriever>> retriever) throws PersistenceException {
        try {
            return convert(toAdd, albums, albumService, retriever);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //should never be thrown
            return null;
        }
    }

    public Song addSong(SongDTO toAdd, Map<EntityType, List<InfoRetriever>> retriever) throws FileNotFoundException, PersistenceException {
        //Song song = convert(toAdd, songById, songService, retriever);
        Album albumSong = null;
        Artist artistSong = null;
        Genre genreSong = null;
        if(toAdd.hasArtistName()) {
            //logger.info("has artist");
            artistSong = convert(new ArtistDTO(toAdd.getArtistName()), artists, artistService, retriever);
            //logger.info("artistname: "+ artistSong.getName());
            //song.set(convert(new ArtistDTO(toAdd.getArtistName()), artistById, artistService, retriever));
        }
        if(toAdd.hasAlbum()) {
            //logger.info("has album");
            AlbumDTO albumDTO = new AlbumDTO(toAdd.getAlbum(), toAdd.getArtistName());
            albumDTO.setReleaseYear(toAdd.getReleaseYear());
            albumSong = convert(albumDTO, albums, albumService, retriever);
            //logger.info("albumname: "+ albumSong.getName());
            //song.set(convert(new AlbumDTO(toAdd.getAlbum(), toAdd.getArtistName()), albumById, albumService, retriever));
        }
        if(toAdd.hasGenre()) {
            //logger.info("has genre");
            //Genre genre;
            if (!genres.containsKey(toAdd.getGenre())) {
                genreSong = new Genre(toAdd.getGenre());
                genres.put(toAdd.getGenre(), genreSong);
            }else{
                genreSong = genres.get(toAdd.getGenre());
            }
            //song.set(genre);
        }
        boolean alreadyInMap = false;
        if(songs.containsKey(toAdd.getPath())) {
            alreadyInMap = true;
        }
        Song song = convert(toAdd, songs, songService, retriever);
        if(song == null) {
            return null;
        }
        if(artistSong != null) {
            song.set(artistSong);
            logger.debug("artist to song set ("+ artistSong.getName() +")");
        }
        if(albumSong != null) {
            song.set(albumSong);
            logger.debug("album to song set ("+ albumSong.getName() +")");
        }
        if(genreSong != null) {
            song.set(genreSong);
            logger.debug("genre to song set ("+ genreSong.getName() +")");
        }
        if(alreadyInMap) {
            return null;
        }
        return song;
    }

    @Override
    public Song updateSong(SongDTO toAdd, Map<EntityType, List<InfoRetriever>> retriever) throws ServiceException {
        try {
            return addSong(toAdd, retriever);
        } catch (FileNotFoundException e) {
            logger.error("file not found");
            throw new ServiceException.ServiceFileNotFoundException(new File(toAdd.getPath()));
        } catch (PersistenceException e) {
            logger.error("failed to update song");
        }
        return null;
    }

    @Override
    public void updateArtist(ArtistDTO newArtist) throws ServiceException {
        Artist oldArtist = findArtistByID(newArtist.getId());
        Artist updatedArtist = artists.get(oldArtist.getName());
        artists.remove(oldArtist.getName());

        try {
            if (oldArtist.get(Type.SONG).isEmpty()) {
                artistService.remove(oldArtist.getId());
            }

            convert(newArtist,artists,artistService,null);
            List<SongDTO> songList = songService.readAll();
            for (SongDTO s : songList) {
                addSong(s, null);
            }
        } catch (FileNotFoundException | PersistenceException | ServiceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        } catch (ValidationException e) {
            logger.error("invalid Artist!");
        }
        artists.putIfAbsent(newArtist.getName(), updatedArtist);
    }

    @Override
    public void updateAlbum(AlbumDTO newAlbum) throws ServiceException {
        Album oldAlbum = findAlbumsByID(newAlbum.getId());
        Album updatedAlbum = albums.get(oldAlbum.getName()+oldAlbum.getArtistName());
        albums.remove(oldAlbum.getName() + oldAlbum.getArtistName());
        try {
            convert(newAlbum,albums,albumService,null);
            List<SongDTO> songList = songService.readAll();
            for (SongDTO s : songList) {
                addSong(s,null);
            }
        } catch (FileNotFoundException | PersistenceException | ServiceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        albums.putIfAbsent(newAlbum.getName()+newAlbum.getArtistName(), updatedAlbum);
    }

    private Album findAlbumsByID(Integer id) {
        for (Album a: albums.values()){
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    private Artist findArtistByID(Integer id) {
        for (Artist a : artists.values()){
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    /*private <A extends DaoEntity, B extends DTO<A>> List<A> convert(List<B> dtos, Map<Integer, A> map, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<EntityType, List<InfoRetriever>> retriever)throws PersistenceException {
        List<A> list = new ArrayList<>(dtos.size());
        for(B dto : dtos){
            try {
                list.add(convert(dto, map, service, retriever));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //should never be thrown
            }
        }
        return list;
    }*/

    //todo: delete if never used.
    private <A extends DaoEntity, B extends DTO<A>> List<A> convert(List<B> dtos, Map<String, A> map, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<EntityType, List<InfoRetriever>> retriever)throws PersistenceException {
        List<A> list = new ArrayList<>(dtos.size());
        for(B dto : dtos){
            try {
                list.add(convert(dto, map, service, retriever));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //should never be thrown
            }
        }
        return list;
    }

    /*private <A extends DaoEntity, B extends DTO<A>> A convert(B dto, Map<Integer, A> map, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<EntityType, List<InfoRetriever>> retriever) throws FileNotFoundException, PersistenceException {
        if (dto.hasId()) {
            if (!map.containsKey(dto.getId())) {
                A newA = dto.createNew();
                map.put(dto.getId(), newA);
                return newA;
            }
        } else {
            A newA = dto.createNew();
            try {
                logger.info("trying to create new "+ dto.getEntityType().getName());
                service.persist(dto);
            } catch (ValidationException e) {
                logger.error(e.getMessage());
                // todo: show error to user
                return null;
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                // todo: show error to user
                return null;
            }
            map.put(dto.getId(), newA);
            logger.info("added to map: ("+ dto.getId() +", "+ newA.getEntityType().getName() + ": "+ newA.getName() +")");
            if (retriever != null) {
                retrieveInformation(newA, service, retriever);
            }
        }

        return map.get(dto.getId());
    }*/

    private <A extends DaoEntity, B extends DTO<A>> A convert(B dto, Map<String, A> map, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<EntityType, List<InfoRetriever>> retriever) throws FileNotFoundException, PersistenceException {
        if (dto.hasId()) {
            if (!map.containsKey(dto.getIdentifier())) {
                A newA = dto.createNew();
                map.put(dto.getIdentifier(), newA);
                return newA;
            }
        } else {
            if(!map.containsKey(dto.getIdentifier())) {
                A newA = null;
                try {
                    //logger.info("trying to create new " + dto.getEntityType());
                    service.persist(dto);
                    newA = dto.createNew();
                } catch (ValidationException e) {
                    logger.error(e.getMessage());
                    // todo: show error to user
                    return null;
                } catch (ServiceException e) {
                    logger.error(e.getMessage());
                    // todo: show error to user
                    return null;
                }
                if (retriever != null) {

                    retrieveInformation(newA, service, retriever);
                }
                map.put(dto.getIdentifier(), newA);
                logger.info("id of "+ newA.getEntityType().getName() +": "+ dto.getId()+ "(entityid: "+ newA.getId()+")");
                logger.info("added to map: ("+ dto.getIdentifier() +", "+ newA.getEntityType().getName() + ": "+ newA.getName() +")");
            }
        }

        return map.get(dto.getIdentifier());
    }

    @SuppressWarnings("unchecked")
    private <A extends Entity> void notifyRemove(A e) {
        if(modifiedListeners != null && modifiedListeners.get(e.getEntityType()) != null) {
            for (EntityModifiedListener modifiedListener : modifiedListeners.get(e.getEntityType())) {
                modifiedListener.beforeRemove(e);
            }
        }
    }

    private <A extends DaoEntity, B extends DTO<A>> void remove(A e, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<String, A> byIdentifier) throws PersistenceException, ValidationException, ServiceException {
        service.remove(e.getId());
        byIdentifier.remove(e.getIdentifier());
        remove(e);
    }

    @SuppressWarnings("unchecked")
    private <A extends Entity> void remove(A e){
        if(modifiedListeners != null && modifiedListeners.get(e.getEntityType()) != null) {
            for (EntityModifiedListener modifiedListener : modifiedListeners.get(e.getEntityType())) {
                modifiedListener.afterRemove(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends DaoEntity, B extends DTO<A>> void retrieveInformation(A newA, at.ac.tuwien.sepm.musicplayer.service.Service<B> service, Map<EntityType, List<InfoRetriever>> retriever) {
        logger.info("trying to retrieve infos for " + newA.getEntityType());
        if(retriever.get(newA.getEntityType()) != null) {
            logger.info("retriever not null for " + newA.getEntityType());
            for (InfoRetriever<A, at.ac.tuwien.sepm.musicplayer.service.Service<B>> infoRetriever : retriever.get(newA.getEntityType())) {
                try {
                    infoRetriever.retrieveInfo(newA, service);
                } catch (ServiceException e) {
                    logger.error("failed to retrieve infos of " + newA.getEntityType().getName());
                    //todo
                }
            }
        }
    }

//    public <A extends Entity> void addModifiedListeners(EntityType<A> type, EntityModifiedListener<A> listener) {
//        modifiedListeners.get(type).add(listener);
//    }
}
