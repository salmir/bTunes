package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Album;

/**
 * Created by Alexandra Mai.
 */
public class AlbumDTO extends DTO<Album> {
    private Integer releaseYear=-1;
    private String name;
    private String artistName;
    //private String songName;
    private int artistId = -1;
    //private int songId;
    private String cover;


    public AlbumDTO(String name) {

        this.name = name;
    }

    public AlbumDTO(String name, String artistName) {
        this.artistName = artistName;
        this.name = name;
    }

    public AlbumDTO() {}

    @Override
    public String getIdentifier() {
        return this.name + this.artistName;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Integer getReleaseYear() {return releaseYear;}

    public void setReleaseYear(Integer releaseYear) {this.releaseYear = releaseYear;}

    /*public int getSongId() {return songId;}

    public void setSongId(int songId) {this.songId = songId;}*/

    public int getArtistId() {return artistId;}

    public void setArtistId(int artistId) {this.artistId = artistId;}

    public String getCover() {return cover; }

    //public boolean hasSong() { return songName!=null; }

    public boolean hasArtistName() {return artistName!=null; }

    public void setCover(String cover) {this.cover = cover; }
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /*public String getSong() {
        return songName;
    }*/

    /*public void setSongName(String songName) {
        this.songName = songName;
    }*/

    @Override
    public Album createNew() {
        return new Album(this);
    }

    @Override
    public EntityType<Album> getEntityType() {
        return Type.ALBUM;
    }
}
