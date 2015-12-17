package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

import java.io.FileNotFoundException;

/**
 * Created by marjaneh.
 */
public class PlaylistDTO extends DTO<Playlist> {
    private String name;

    public PlaylistDTO() {
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    public PlaylistDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Playlist createNew() throws FileNotFoundException {
        return new Playlist(this);
    }

    @Override
    public EntityType<Playlist> getEntityType() {
        return Type.PLAYLIST;
    }
}
