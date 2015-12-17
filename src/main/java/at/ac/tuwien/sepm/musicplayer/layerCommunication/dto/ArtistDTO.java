package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

/**
 * Created by Lena Lenz.
 */
public class ArtistDTO extends DTO<Artist>{
    private String name;
    private String biography;
    private String image;

    public ArtistDTO(String name) {

        this.name = name;
    }

    public ArtistDTO() {

    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Artist: "+this.getName()+"\nBiography: "+this.getBiography()+"\nImagePath: "+this.getImage()+"\n-------------------";
    }

    @Override
    public Artist createNew() {
        return new Artist(this);
    }

    @Override
    public EntityType<Artist> getEntityType() {
        return Type.ARTIST;
    }
}
