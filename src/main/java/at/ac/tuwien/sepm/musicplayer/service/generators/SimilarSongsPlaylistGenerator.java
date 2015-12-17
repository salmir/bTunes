package at.ac.tuwien.sepm.musicplayer.service.generators;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import org.springframework.stereotype.Service;

/**
 * Created by Lena Lenz.
 */
@Service
public interface SimilarSongsPlaylistGenerator extends PlaylistGenerator<Song> {
}
