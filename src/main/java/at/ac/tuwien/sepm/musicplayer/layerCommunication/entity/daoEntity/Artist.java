package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.DelegatingInfo;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoDelegation;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

/**
 * Created by Lena Lenz.
 */
public class Artist implements DaoEntity, DelegatingInfo {
//    public static final InfoType<Artist> INFO_TYPE = InfoType.create("Artist");
//    public static final InfoOwnerType<Artist> TYPE = new InfoOwnerType<>();

    private InfoDelegation infoDelegation = new InfoDelegation(this);
    private int id;
    private String name;
    private String biography;
    private String image;

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Artist(ArtistDTO artistDTO) {
        this(artistDTO.getId(), artistDTO.getName());
        this.biography = artistDTO.getBiography();
        this.image = artistDTO.getImage();
    }

    @Override
    public InfoType<Artist> getInfoType() {
        return Type.ARTIST;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EntityType<? extends DaoEntity> getEntityType() {
        return Type.ARTIST;
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public InfoDelegation getInfoDelegation() {
        return infoDelegation;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}