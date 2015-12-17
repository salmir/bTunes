package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.StatisticDTO;
import javafx.util.Pair;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Lena Lenz.
 */
public class RowMappers {

    static RowMapper<SongDTO> getSongRowMapper() {
        return (resultSet, i) -> {
            SongDTO newSongDTO = new SongDTO();
            newSongDTO.setId(resultSet.getInt(1));
            newSongDTO.setPath(resultSet.getString(2));
            newSongDTO.setName(resultSet.getString(3));
            newSongDTO.setLength(resultSet.getLong(4));
            newSongDTO.setArtistId(resultSet.getInt(5));
            newSongDTO.setAlbumId(resultSet.getInt(6));
            newSongDTO.setGenre(resultSet.getString(7));
            newSongDTO.setRating(resultSet.getInt(8));
            newSongDTO.setLyrics(resultSet.getString(9));

            return newSongDTO;
        };
    }


    static RowMapper<ArtistDTO> getArtistRowMapper() {
        return (resultSet, i) -> {
            ArtistDTO newArtistDTO = new ArtistDTO();
            newArtistDTO.setId(resultSet.getInt(1));
            newArtistDTO.setName(resultSet.getString(2));
            newArtistDTO.setBiography(resultSet.getString(3));
            newArtistDTO.setImage(resultSet.getString(4));
            return newArtistDTO;
        };
    }

    static RowMapper<AlbumDTO> getAlbumRowMapper() {
        return (resultSet, i) -> {
            AlbumDTO newAlbumDTO = new AlbumDTO();
            newAlbumDTO.setId(resultSet.getInt(1));
            newAlbumDTO.setName(resultSet.getString(2));
            newAlbumDTO.setReleaseYear(resultSet.getInt(3));
            newAlbumDTO.setArtistId(resultSet.getInt(4));
            newAlbumDTO.setCover(resultSet.getString(5));

            return newAlbumDTO;
        };
    }

    static RowMapper<PlaylistDTO> getPlaylistRowMapper() {
        return (resultSet, i) -> {
            PlaylistDTO newPlaylistDTO= new PlaylistDTO();
            newPlaylistDTO.setId(resultSet.getInt(1));
            newPlaylistDTO.setName(resultSet.getString(2));

            return newPlaylistDTO;
        };
    }

    static RowMapper<StatisticDTO> getStatisticRowMapper() {
        return (resultSet, i) -> {
                StatisticDTO newStatisticDTO = new StatisticDTO();
                newStatisticDTO.setId(resultSet.getInt(1));
                newStatisticDTO.setDateSongPlayed(resultSet.getTimestamp(2));
                newStatisticDTO.setSongPlayedId(resultSet.getInt(3));
                return newStatisticDTO;
        };
    }

    static RowMapper<Pair<Integer, Integer>> getStatisticGroupByCountMapper() {
        return (resultSet, i) -> {
            Pair<Integer, Integer> newPair = new Pair<>(resultSet.getInt(2), resultSet.getInt(1));
            return newPair;
        };
    }

    static RowMapper<Integer> getCountRowMapper() {
        return (rs, rowNum) -> rs.getInt(1);
    }
}
