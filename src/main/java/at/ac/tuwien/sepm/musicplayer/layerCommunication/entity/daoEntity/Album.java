package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.DelegatingInfo;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoDelegation;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

/**
 * Created by Lena Lenz.
 */
public class Album implements DaoEntity, DelegatingInfo {
    private int id;
    private String name;
    private int releaseYear=-1;
    private String cover;
    private String artistName;


    private InfoDelegation infoDelegation = new InfoDelegation(this);

    public Album(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Album(AlbumDTO albumDTO) {
        this(albumDTO.getId(), albumDTO.getName());
        this.releaseYear = albumDTO.getReleaseYear();
        this.cover = albumDTO.getCover();
        this.artistName = albumDTO.getArtistName();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EntityType<? extends DaoEntity> getEntityType() {
        return Type.ALBUM;
    }

    @Override
    public String getIdentifier() {
        return this.name + this.artistName;
    }

    @Override
    public at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType<Album> getInfoType() {
        return Type.ALBUM;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public InfoDelegation getInfoDelegation() {
        return infoDelegation;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setName(String name) {
        this.name = name;
    }
}
