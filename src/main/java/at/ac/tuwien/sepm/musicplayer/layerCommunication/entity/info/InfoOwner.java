package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.OwnerType;

/**
 * Created by Lena Lenz.
 *
 * An InfoOwner is an owner of Infos
 *
 */
public interface InfoOwner extends Entity{
    public OwnerType<? extends InfoOwner> getOwnerType();
    public void set(Info info);
    public <B extends Info> B get(InfoType<B> toGet);
    public <B extends Info> boolean has(InfoType<B> infoType);
    public void remove(InfoType toGet);

    @Override
    public default EntityType<? extends Entity> getEntityType(){
        return getOwnerType();
    }
}
