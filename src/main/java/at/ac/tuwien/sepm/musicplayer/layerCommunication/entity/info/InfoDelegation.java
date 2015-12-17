package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.OwnerType;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lena Lenz.
 *
 * An InfoDelegation is an Info that can be added to Info-owners
 *
 */
public class InfoDelegation implements Info {
    private Info delegateFrom;
    private HashMap<OwnerType, List<InfoOwner>> owners = new HashMap<>();

    public InfoDelegation(Info delegateFrom) {
        this.delegateFrom = delegateFrom;
    }

    @Override
    public InfoType<? extends Info> getInfoType() {
        return delegateFrom.getInfoType();
    }

    @Override
    public String getName() {
        return delegateFrom.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends InfoOwner> List<A> get(OwnerType<A> infoOwnerType) {
        if(owners.containsKey(infoOwnerType)){
            return (List<A>) owners.get(infoOwnerType);
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public void addOwner(InfoOwner infoOwner) {
        if(!owners.containsKey(infoOwner.getOwnerType())){
            owners.put(infoOwner.getOwnerType(), new LinkedList<>());
        }
        owners.get(infoOwner.getOwnerType()).add(infoOwner);
    }

    @Override
    public void removeOwner(InfoOwner infoOwner) {
        if(owners.containsKey(infoOwner.getOwnerType())){
            owners.get(infoOwner.getOwnerType()).remove(infoOwner);
            if(owners.get(infoOwner.getOwnerType()).isEmpty()){
                owners.remove(infoOwner.getOwnerType());
            }
        }
    }


    @Override
    public boolean isEmpty(){
        return owners.isEmpty();
    }

    @Override
    public boolean hasOwner(OwnerType ownerType){
        return !owners.isEmpty() && !owners.get(ownerType).isEmpty();
    }
}
