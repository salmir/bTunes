package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;

/**
 * Created by Lena Lenz.
 */
public interface EntityType<A extends Entity> {
    public String getName();
}
