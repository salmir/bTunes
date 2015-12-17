package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;

/**
 * Created by Lena Lenz.
 *
 * An OwnerInfo is an Info that can be added to an Owner and
 * it is an Owner of Infos
 */
public abstract class OwnerInfo implements DelegatingInfo, DelegatingInfoOwner {
    private InfoDelegation infoDelegation = new InfoDelegation(this);
    private InfoOwnerDelegation infoOwnerDelegation = new InfoOwnerDelegation(this);

    @Override
    public InfoDelegation getInfoDelegation() {
        return infoDelegation;
    }

    @Override
    public InfoOwnerDelegation getOwnerDelegation() {
        return infoOwnerDelegation;
    }

    @Override
    public EntityType<? extends Entity> getEntityType() {
        return getInfoType();
    }
}
