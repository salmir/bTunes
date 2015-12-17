package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.Info;

/**
* Created by Lena Lenz.
*/
public interface InfoType<A extends Info> extends EntityType<A>{
    public String getName();
}
