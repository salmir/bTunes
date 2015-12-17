package at.ac.tuwien.sepm.musicplayer.service.generators;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import org.springframework.stereotype.Service;

/**
 * Created by Lena Lenz.
 */
@Service
public interface SimilarArtistsPlaylistGenerator extends PlaylistGenerator<Artist> {
}
