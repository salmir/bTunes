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
import de.umass.lastfm.Track;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;


/**
 * Created by Lena Lenz.
 */
@Service
public class SimilarSongsLastFMPlaylistGenerator implements SimilarSongsPlaylistGenerator {

    private static final Logger logger = Logger.getLogger(SimilarSongsLastFMPlaylistGenerator.class);

    @Autowired
    private Library library;

    @Autowired
    private PlaylistService playlistService;


    private Collection<Track> getSimilarSongs(String songName, String artistName) {
        return Track.getSimilar(artistName, songName, getLastFMKey());
    }


    @Override
    public Playlist generatePlaylist(Song song) throws ValidationException, ServiceException, FileNotFoundException {
        String songName = song.getName();
        String artistName = song.get(Type.ARTIST).getName();
        // check if enough songs in library
        List<Song> songsInLibrary = library.getAllSongs();
        if(songsInLibrary.size() < 25) {
            logger.error("too few songs in library ("+ songsInLibrary.size() +" but should be >= 25)");
            return null;
        }
        // search for similar songs in library
        Collection<Track> similarSongsTracks = getSimilarSongs(songName, artistName);
        logger.info("retrieved similar songs: "+ similarSongsTracks.size());
        List<Song> similarSongsInLibrary = new ArrayList<>();
        List<Song> backUpSimilarSongsInLibrary = new ArrayList<>(); // backup if too few similar songs in library
        List<String> similarSongArtistsInLibrary = new ArrayList<>(); // backup if too few artists of similar songs are in library
        Map<String, Artist> artistsInLibraryMap = library.getArtistMap();
        for(Track track : similarSongsTracks) {
            String trackArtist = track.getArtist();
            if(artistsInLibraryMap.containsKey(trackArtist)) {
                logger.info("similar artist in library: "+ trackArtist);
                if(!similarSongArtistsInLibrary.contains(trackArtist)) {
                    similarSongArtistsInLibrary.add(trackArtist);
                }
                int countSongsToArtist = 0;
                for(Song artistSong : artistsInLibraryMap.get(trackArtist).get(Type.SONG)) {
                    if(artistSong.getName().equals(track.getName()) && !similarSongsInLibrary.contains(artistSong)) {
                        // similar song in library
                        similarSongsInLibrary.add(artistSong);
                        logger.info("similar song in library: "+ artistSong.getName());
                    }
                    else {
                        // if later on we have too few similar songs in library, take songs of artist of similar song
                        if(!backUpSimilarSongsInLibrary.contains(artistSong) && countSongsToArtist < 5) { // do not store songs of seed artist twice, only store max 5 songs of each artist
                            backUpSimilarSongsInLibrary.add(artistSong);
                            countSongsToArtist++;
                            logger.info("song to artist in library: "+ artistSong.getName());
                        }
                    }
                }
            }
        }
        // check if enough similar songs in library
        int countSimilarSongs = similarSongsInLibrary.size();
        if(countSimilarSongs < 25) {
            int residual = (25-countSimilarSongs);
            int countBackupSongs = backUpSimilarSongsInLibrary.size();

            // check if not enough songs even with songs of artists of similar songs added
            if(countBackupSongs < residual) {
                // not enough songs of artists of similar songs
                // => add songs of similar artists of seed song (use SimilarArtistPlaylist)
                logger.info("not enough songs of artists of similar songs => add songs of similar artists of artist to seed song ("+ artistName +")");
                List<Artist> similarArtistsInLibrary = new ArrayList<>();
                SimilarArtistsLastFMPlaylistGenerator similarArtistsGenerator = new SimilarArtistsLastFMPlaylistGenerator();
                for(String similarArtist : similarArtistsGenerator.getSimilarArtists(artistName)) {
                    if(artistsInLibraryMap.containsKey(similarArtist) && !similarSongArtistsInLibrary.contains(similarArtist) && !similarArtistsInLibrary.contains(similarArtist)) {
                        similarArtistsInLibrary.add(artistsInLibraryMap.get(similarArtist));
                        logger.info("similar artist in library: "+ similarArtist);
                    }
                }
                // get library songs of similar artists
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
                // check if still not enough songs
                int countSimilarArtistSongs = similarPlaylistSongs.size();
                if((countSimilarSongs+countBackupSongs+countSimilarArtistSongs) < 25) {
                    logger.error("not enough similar songs in library (" + (countSimilarSongs + countBackupSongs + countSimilarArtistSongs) + " but should be >= 25)");
                    return null;
                }
                // success => add songs of similar artists
                Collections.shuffle(similarPlaylistSongs);
                for(int j = 0; j < (25-countSimilarSongs-countBackupSongs); j++) {
                    similarSongsInLibrary.add(similarPlaylistSongs.get(j));
                }
            }
            // success => add songs of artists of similar songs
            logger.info("not enough similar songs in library => add songs of artists of similar songs");
            Collections.shuffle(backUpSimilarSongsInLibrary);
            for(int i = 0; i < residual; i++) {
                similarSongsInLibrary.add(backUpSimilarSongsInLibrary.get(i));
            }
        }

        // randomize song sequence
        Collections.shuffle(similarSongsInLibrary);
        // take 25 songs
        similarSongsInLibrary.subList(0, 25);

        // persist new playlist
        PlaylistDTO similarPlaylistDTO = new PlaylistDTO();
        similarPlaylistDTO.setName("Similar Playlist for song: "+ songName);
        playlistService.persist(similarPlaylistDTO);

        // add songs to playlist
        List<SongDTO> similarSongsInLibraryDTO = new ArrayList<>();
        for(Song songInLibrary : similarSongsInLibrary) {
            SongDTO songDTO = new SongDTO(songInLibrary.getName(), songInLibrary.getFile().getAbsolutePath());
            songDTO.setId(songInLibrary.getId());
            similarSongsInLibraryDTO.add(songDTO);
        }
        // persist into playlist
        playlistService.insertSongs(similarPlaylistDTO.getId(), similarSongsInLibraryDTO);

        // successfully created playlist
        return similarPlaylistDTO.createNew();
    }

}
