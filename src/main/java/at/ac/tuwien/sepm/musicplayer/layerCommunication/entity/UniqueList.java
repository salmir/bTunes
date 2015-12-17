package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lena Lenz.
 */
public class UniqueList<A> extends ArrayList<A> {
    private Set<A> unique = new HashSet<>();
    @Override
    public boolean add(A toAdd) {
        if(!unique.contains(toAdd)){
            unique.add(toAdd);
            return super.add(toAdd);
        }
        return false;
    }

    @Override
    public boolean remove(Object toRemove) {
        if(unique.contains(toRemove)){
            unique.remove(toRemove);
            return super.remove(toRemove);
        }
        return false;
    }
}
