package at.ac.tuwien.sepm.musicplayer.service.generators;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Artist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Playlist;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.Library;
import at.ac.tuwien.sepm.musicplayer.service.PlaylistService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Lena Lenz.
 */
@Service
public class SimilarArtistsLastFMPlaylistGenerator implements SimilarArtistsPlaylistGenerator {

    private static final Logger logger = Logger.getLogger(SimilarArtistsLastFMPlaylistGenerator.class);

    @Autowired
    private Library library;

    @Autowired
    private PlaylistService playlistService;



    public List<String> getSimilarArtists(String artistName) {
        return getSimilarArtists(artistName, 500);
    }

    public List<String> getSimilarArtists(String artistName, int limit) {
        List<String> similarArtists = new ArrayList<>();
        for(de.umass.lastfm.Artist similarArtist :  de.umass.lastfm.Artist.getSimilar(artistName, limit, getLastFMKey())) {
            similarArtists.add(similarArtist.getName());
            logger.info("similar artist: " + similarArtist.getName());
        }

        return similarArtists;
    }

    @Override
    public Playlist generatePlaylist(Artist artist) throws ValidationException, ServiceException, FileNotFoundException {
        String name = artist.getName();
        Map<String, Artist> artistsInLibrary = library.getArtistMap();
        if(artistsInLibrary.size() < 5) {
            logger.info("not enough artists in library ("+ artistsInLibrary.size() +" but should be >= 5)");
            return null;
        }
        List<String> similarArtistNames = getSimilarArtists(name);
        List<Artist> similarArtistsInLibrary = new ArrayList<>();
        for(String similarArtistName : similarArtistNames) {
            if(artistsInLibrary.containsKey(similarArtistName)) {
                // similar artist in library
                logger.info("similar artist in library: "+ similarArtistName);
                similarArtistsInLibrary.add(artistsInLibrary.get(similarArtistName));
            }
        }

        // check if enough similar artists in library
        if(similarArtistsInLibrary.size() < 5) {
            logger.info("not enough similar artists in library ("+ similarArtistsInLibrary.size() +" but should be >= 5)");
            return null;
        }

        // add 5 songs (or less if artists does not have as many songs) of each similar artist
        List<Song> similarPlaylistSongs = new ArrayList<>();
        for(Artist similarArtistInLibrary : similarArtistsInLibrary) {
            List<Song> artistSongs = similarArtistInLibrary.get(Type.SONG);
            int countSongs = artistSongs.size();
            int max = countSongs < 5 ? countSongs : 5;
            for(int i = 0; i < max; i++) {
                similarPlaylistSongs.add(artistSongs.get(i));
            }
        }
        // check if enough songs
        if(similarPlaylistSongs.size() < 25) {
            logger.info("not enough songs of similar artists ("+ similarPlaylistSongs.size() +" but should be 25)");
            return null;
        }

        // randomize song sequence
        Collections.shuffle(similarPlaylistSongs);

        // take 25 songs
        similarPlaylistSongs.subList(0, 25);

        // store playlist
        PlaylistDTO similarPlaylistDTO = new PlaylistDTO("Similarity Playlist for artist: "+ name);

        playlistService.persist(similarPlaylistDTO);

        // add songs to playlist
        List<SongDTO> similarPlaylistSongsDTO = new ArrayList<>();
        for(Song song : similarPlaylistSongs) {
            SongDTO songDTO = new SongDTO(song.getName(), song.getFile().getAbsolutePath());
            songDTO.setId(song.getId());
            similarPlaylistSongsDTO.add(songDTO);
        }
        // persist into playlist
        playlistService.insertSongs(similarPlaylistDTO.getId(), similarPlaylistSongsDTO);

        // successfully created playlist
        return similarPlaylistDTO.createNew();
    }
}
