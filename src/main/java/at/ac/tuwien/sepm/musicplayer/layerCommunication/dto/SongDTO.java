package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;

import java.io.FileNotFoundException;

/**
 * Created by Lena Lenz.
 */
public class SongDTO extends DTO<Song> {
    private Integer releaseYear = -1;
    private String name;
    private String path;
    private long length = -1;
    private String artistName;
    private String albumName;
    private String genre;
    private int rating = -1;
    private String lyrics;

    private int artistId = -1;
    private int albumId = -1;


    public SongDTO() {}

    @Override
    public String getIdentifier() {
        return this.path;
    }

    public SongDTO(String name, String path) {

        this.name = name;
        this.path = path;

    }

    public SongDTO(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public Song createNew() throws FileNotFoundException {
        return new Song(this);
    }

    @Override
    public EntityType<Song> getEntityType() {
        return Type.SONG;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbum() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public boolean hasAlbum() {
        return albumName!=null;
    }

    public boolean hasArtistName() {
        return artistName!=null;
    }

    public boolean hasGenre() {
        return genre!=null;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
