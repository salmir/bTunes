package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.*;

import java.util.List;

/**
 * Created by Lena Lenz.
 *
 * Infos represent information which can be added to InfoOwners
 * One Info can be owned by multiple InfoOwners
 *
 */
public interface Info extends Entity{

    /**
     * Get InfoType of Info
     *
     * @return InfoType of corresponding Info
     */
    public InfoType<? extends Info> getInfoType();

    /**
     * Get InfoOwners of Info
     *
     * @param infoOwnerType OwnerType of InfoOwner A
     * @param <A> InfoOwner
     * @return list of InfoOwners of this Info
     */
    public <A extends InfoOwner> List<A> get(OwnerType<A> infoOwnerType);

    /**
     * Add new InfoOwner to Info
     *
     * @param infoOwner InfoOwner of this Info
     */
    public void addOwner(InfoOwner infoOwner);

    /**
     * Check if Info has an InfoOwner
     *
     * @param infoOwner owner to be checked
     * @return true if Info has infoOwner as owner, else false
     */
    public boolean hasOwner(OwnerType infoOwner);

    /**
     * Checks if object is empty
     *
     * @return true if empty, else false
     */
    public boolean isEmpty();

    /**
     * Removes an InfoOwner of this Info
     *
     * @param infoOwner InfoOwner which gets removed as owner from this Info
     */
    public void removeOwner(InfoOwner infoOwner);

    /**
     * Get EntityType of Info
     *
     * @return EntityType of this Info
     */
    @Override
    public default EntityType<? extends Entity> getEntityType(){
        return getInfoType();
    }
}
