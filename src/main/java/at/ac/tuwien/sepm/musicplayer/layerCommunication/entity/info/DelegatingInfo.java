package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.OwnerType;

import java.util.List;

/**
 * Created by Lena Lenz.
 *
 * A DelegatingInfo is an Info that is a helper interface that delegates methods from Info to InfoDelegation
 *
 */
public interface DelegatingInfo extends Info{
	public default <A extends InfoOwner> List<A> get(OwnerType<A> infoOwnerType){
		return getInfoDelegation().get(infoOwnerType);
	}

	@Override
	public default void addOwner(InfoOwner infoOwner){
		getInfoDelegation().addOwner(infoOwner);
	}
	@Override
	public default boolean hasOwner(OwnerType infoOwner){
		return getInfoDelegation().hasOwner(infoOwner);
	}
	@Override
	public default boolean isEmpty(){
		return getInfoDelegation().isEmpty();
	}
	@Override
	public default void removeOwner(InfoOwner infoOwner){
		getInfoDelegation().removeOwner(infoOwner);
	}

    /**
     * InfoDelegation-object to which all methods from Info should be delegated to
     *
     * @return the delegation object
     */
	public InfoDelegation getInfoDelegation();
}
