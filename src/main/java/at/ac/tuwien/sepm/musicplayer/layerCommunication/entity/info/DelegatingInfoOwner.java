package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;

/**
 * Created by Lena Lenz.
 *
 * A DelegatingInfoOwner is an owner of Infos and delegates to InfoOwnerDelegation-object
 *
 */
public interface DelegatingInfoOwner extends InfoOwner{
    @Override
    public default void set(Info info){
        getOwnerDelegation().set(info);
    }
    @Override
    public default <B extends Info> B get(InfoType<B> toGet){
        return getOwnerDelegation().get(toGet);
    }
    @Override
    public default <B extends Info> boolean has(InfoType<B> infoType){
        return getOwnerDelegation().has(infoType);
    }

    @Override
    public default void remove(InfoType toGet){
        getOwnerDelegation().remove(toGet);
    }

    public InfoOwnerDelegation getOwnerDelegation();
}
