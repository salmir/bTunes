package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.service.ArtistService;

/**
 * Created by Alexandra on 03.06.2015.
 */
public interface ConcertDataRetriever extends InfoRetriever<Artist, ArtistService> {
    public String getAusgabe();
}
