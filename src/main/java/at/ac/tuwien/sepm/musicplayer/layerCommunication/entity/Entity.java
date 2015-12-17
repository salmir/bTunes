package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;

/**
 * Created by Lena Lenz.
 */
public interface Entity {
    public String getName();
    public EntityType<? extends Entity> getEntityType();

}
