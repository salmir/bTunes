package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;

import java.io.FileNotFoundException;

/**
 * Created by Lena Lenz.
 */
public abstract class DTO<A extends Entity> {
    private Integer id = -1;
    public DTO() {

    }

    public DTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public abstract String getIdentifier();

    public abstract A createNew() throws FileNotFoundException;

    public abstract EntityType<A> getEntityType();

    public boolean hasId() {
        return id!=-1;
    }
}
