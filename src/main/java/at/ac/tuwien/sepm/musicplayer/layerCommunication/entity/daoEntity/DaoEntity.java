package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;

/**
 * Created by Lena Lenz.
 */
public interface DaoEntity extends Entity{
    public int getId();
    public EntityType<? extends DaoEntity> getEntityType();
    public String getIdentifier();
}
