package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;

/**
 * Created by Lena Lenz.
 */
public interface ArtistBioRetriever extends InfoRetriever<Artist, ArtistService> {
}
