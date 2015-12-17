package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

/**
 * Created by Lena Lenz.
 *
 * An AbstractInfoOwner is an abstract implementation of DelegatingInfoOwner
 * It sets the InfoOwnerDelegation-variable
 *
 */
public abstract class AbstractInfoOwner implements DelegatingInfoOwner {
    private InfoOwnerDelegation infoContainer = new InfoOwnerDelegation(this);

    @Override
    public InfoOwnerDelegation getOwnerDelegation() {
        return infoContainer;
    }
}
