package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.OwnerType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lena Lenz.
 *
 * An InfoOwnerDelegation is an InfoOwner which has an implementation for all InfoOwner-methods and
 * can be used by InfoOwners to delegate all methods to this object
 *
 */
public class InfoOwnerDelegation implements InfoOwner {
    private Map<InfoType, Info> infos = new HashMap<>();
    private InfoOwner owner;

    public InfoOwnerDelegation(InfoOwner owner) {
        this.owner = owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B extends Info> B get(InfoType<B> toGet){
        return (B) infos.get(toGet);
    }

    @Override
    public <B extends Info> boolean has(InfoType<B> infoType) {
        return infos.containsKey(infoType);
    }

    @Override
    public void remove(InfoType toGet) {
        infos.remove(toGet);
    }

    @Override
    public OwnerType<? extends InfoOwner> getOwnerType() {
        return owner.getOwnerType();
    }

    @Override
    public void set(Info toAdd) {
        if(infos.containsKey(toAdd.getInfoType())){
            get(toAdd.getInfoType()).removeOwner(owner);
        }
        infos.put(toAdd.getInfoType(), toAdd);
        toAdd.addOwner(owner);
    }

    @Override
    public String getName() {
        return owner.getName();
    }
}
