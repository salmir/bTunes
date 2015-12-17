package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

/**
 * Created by marjaneh.
 */
public class Playlist implements DaoEntity {
    private int id;
    private String name;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Playlist(PlaylistDTO playlistDTO) {
        this(playlistDTO.getId(), playlistDTO.getName());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EntityType<? extends DaoEntity> getEntityType() {
        return Type.PLAYLIST;
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
