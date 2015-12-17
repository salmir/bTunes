package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.DelegatingInfo;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoDelegation;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;


/**
 * Created by Lena Lenz.
 */
public class Genre implements DelegatingInfo, Entity {
    private String name;
    private InfoDelegation infoDelegation = new InfoDelegation(this);

    public Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public InfoType<Genre> getInfoType() {
        return Type.GENRE;
    }

    @Override
    public InfoDelegation getInfoDelegation() {
        return infoDelegation;
    }

    @Override
    public EntityType<? extends Entity> getEntityType() {
        return Type.GENRE;
    }
}
