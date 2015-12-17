package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.PlaylistDAO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by marjaneh.
 */
@Repository
public class JdbcPlaylistDao extends JdbcBaseDao implements PlaylistDAO {

    private static final Logger logger = LogManager.getLogger(JdbcPlaylistDao.class);

    private static final int MAX_LENGTH_NAME = 200;

    private static final String INSERT = "INSERT INTO Playlist(id,name) VALUES(DEFAULT, ?)";
    private static final String UPDATE = "UPDATE Playlist SET name = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM Playlist WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM Playlist";
    private static final String FIND_BY_ID = "SELECT * FROM Playlist WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT * FROM Playlist WHERE upper(name) like upper(?)";
    private static final String COUNT = "SELECT COUNT(*) FROM Playlist WHERE id = ?";
    private static final String COUNT_BY_NAME = "SELECT COUNT(*) FROM Playlist WHERE upper(name) like upper(?)";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM Playlist";
    private static final String INSERT_SONGS = "INSERT INTO Playlist_Songs(playlist_id, song_id, priority) VALUES(?, ?, ?)";
    private static final String DELETE_SONGS = "DELETE FROM Playlist_Songs WHERE playlist_id = ?";
    private static final String GET_ALL_PLAYLIST_SONGS = "SELECT s.ID, s.FILEPATH, s.NAME, s.LENGTH, s.ARTIST_ID, s.ALBUM_ID, s.GENRE, s.RATING, s.LYRICS FROM SONG s, PLAYLIST_SONGS ps WHERE ps.SONG_ID = s.ID";
    private static final String GET_ALL_SONGS = "SELECT s.id, s.filepath, s.name, s.length, s.artist_id, s.album_id, s.genre, s.rating, s.lyrics " +
            "FROM Song s, Playlist_Songs ps WHERE s.id = ps.song_id AND ps.playlist_id = ? ORDER BY priority";
    private static final String COUNT_ALL_SONGS = "SELECT COUNT(*) FROM Song s, Playlist_Songs ps WHERE s.id = ps.song_id and ps.playlist_id = ?";
    private static final String COUNT_ALL_USED_SONGS = "SELECT COUNT(*) FROM SONG s, PLAYLIST_SONGS ps WHERE s.ID = ps.SONG_ID";
    private static final String COUNT_PLAYLIST = "SELECT COUNT(*) FROM Playlist WHERE id = ?";
    private static final String DELETE_SONGS_FROM_PLAYLIST_2 = "DELETE FROM PLAYLIST_SONGS WHERE song_id = ?";

    @Autowired
    JdbcArtistDao jdbcArtistDao;

    @Autowired
    JdbcAlbumDao jdbcAlbumDao;

    @Override
    public void persist(PlaylistDTO toPersist) throws PersistenceException {
        if (toPersist == null) {
            logger.error("playlist is null");
            throw new PersistenceException("playlist is null");
        }
        if (toPersist.getName() != null && toPersist.getName().length() > MAX_LENGTH_NAME) {
            logger.error(ErrorMessages.errorPlaylistNameSize(MAX_LENGTH_NAME));
            throw new PersistenceException(ErrorMessages.errorPlaylistNameSize(MAX_LENGTH_NAME));
        }

        int newId;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT,
                        new String[]{"id"});

                preparedStatement.setString(1, toPersist.getName());
                return preparedStatement;
            }, keyHolder);

            newId = keyHolder.getKey().intValue();
            toPersist.setId(newId);

        } catch (DataAccessException e) {
            logger.error("failed to persist playlist");
            throw new PersistenceException("failed to persist playlist", e);
        }
        logger.debug("new playlist with id=" + newId + " stored.");
    }

    @Override
    public List<PlaylistDTO> readAll() throws PersistenceException {
        List<PlaylistDTO> playLists = new ArrayList<>();
        try {
            playLists = jdbcTemplate.query(GET_ALL, RowMappers.getPlaylistRowMapper());
            if(playLists.size() <= 0) {
                return Collections.emptyList();
            }

        } catch (EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting playLists");
            throw new PersistenceException("ResultSet error while counting playLists", e);
        } catch (DataAccessException e) {
            logger.error("failed to read playLists");
            throw new PersistenceException("failed to read playLists", e);
        }
        return playLists;
    }

    @Override
    public PlaylistDTO read(int id) throws PersistenceException {
        PlaylistDTO playlist = new PlaylistDTO();
        try {
            playlist = jdbcTemplate.queryForObject(FIND_BY_ID, RowMappers.getPlaylistRowMapper(), id);
            if(playlist == null) {
                throw new PersistenceException("No Playlist with id = " + id);
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting playlist");
            throw new PersistenceException("ResultSet error while counting playlist", e);
        } catch (DataAccessException e) {
            logger.error("failed to remove playlist with id = " + id);
            throw new PersistenceException("failed to read playlist with id=" + id, e);
        }
        return playlist;
    }

    @Override
    public List<PlaylistDTO> readByName(String name) throws PersistenceException {
        List<PlaylistDTO> playLists = new ArrayList<>();
        try {
            playLists = jdbcTemplate.query(FIND_BY_NAME, RowMappers.getPlaylistRowMapper(), name + "%");
            if(playLists.size() <= 0) {
                return Collections.emptyList();
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting playLists");
            throw new PersistenceException("ResultSet error while counting playLists", e);
        } catch (DataAccessException e) {
            logger.error("failed to read playLists");
            throw new PersistenceException("failed to read playLists", e);
        }
        return playLists;
    }

    @Override
    public void remove(int id) throws PersistenceException {
        try {
            if(jdbcTemplate.update(DELETE, id) <= 0) {
                logger.debug("no playlist with this id stored!");
                throw new PersistenceException("no playlist with id = " + id);
            }
        } catch (DataAccessException e) {
            logger.error("failed to remove playlist with id=" + id);
            throw new PersistenceException("failed to remove playlist with id = " + id, e);
        }
        logger.debug("removed playlist with id = " + id + ".");
    }

    @Override
    public void update(PlaylistDTO toUpdate) throws PersistenceException {
        if (toUpdate == null) {
            logger.error("playlist is null");
            throw new PersistenceException("playlist is null");
        }
        try {
            jdbcTemplate.update(UPDATE, toUpdate.getName(), toUpdate.getId());
        } catch (DataAccessException e) {
            logger.error("failed to update playlist");
            throw new PersistenceException("failed to update playlist", e);
        }
    }

    @Override
    public List<SongDTO> getSongs(int id) throws PersistenceException {
        List<SongDTO> songs = new ArrayList<>();
        try {
            songs = jdbcTemplate.query(GET_ALL_SONGS, RowMappers.getSongRowMapper(), id);
            if(songs.size() <= 0) {
                return Collections.emptyList();
            }
            for (SongDTO s : songs) {

                s.setArtistName(jdbcArtistDao.read(s.getArtistId()).getName());
                s.setAlbumName(jdbcAlbumDao.read(s.getAlbumId()).getName());
            }

        } catch (EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting songs");
            throw new PersistenceException("ResultSet error while counting songs", e);
        } catch (DataAccessException e) {
            logger.error("failed to read songs from this playlist with id = " + id);
            throw new PersistenceException("failed to read songsfrom this playlist with id = " + id, e);
        }

        return songs;
    }

    @Override
    public void deleteSongs(int id) throws PersistenceException {
        if (jdbcTemplate.queryForObject(COUNT_PLAYLIST, RowMappers.getCountRowMapper(), id) == 0) {
            logger.debug("no playlist with this id stored!");
            throw new PersistenceException("no playlist with id = " + id);
        }
        try {
            jdbcTemplate.update(DELETE_SONGS, id);
        } catch (DataAccessException e) {
            logger.error("failed to remove playlist with id = " + id);
            throw new PersistenceException("failed to remove playlist with id = " + id, e);
        }
        logger.debug("removed playlist with id = " + id + ".");
    }

    @Override
    public void deleteSongInPlaylistSong(int id) throws PersistenceException {
        try {
            jdbcTemplate.update(DELETE_SONGS_FROM_PLAYLIST_2, id);
        } catch (DataAccessException e) {
            logger.error("failed to remove songs (from playlist) with id = " + id);
            throw new PersistenceException("failed to remove songs (from playlist) with id = "+id, e);
        }
        logger.debug("removed song (from playlist) with id = "+id+".");
    }

    @Override
    public void insertSongs(int playlistId, List<SongDTO> toInsertSongs) throws PersistenceException {
        List<SongDTO> oldList = getSongs(playlistId);
        List<SongDTO> newList = new ArrayList<>();
        newList.addAll(oldList);
        newList.addAll(toInsertSongs);
        try {
            deleteSongs(playlistId);

            jdbcTemplate.batchUpdate(INSERT_SONGS, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    SongDTO song = newList.get(i);
                    ps.setInt(1, playlistId);
                    ps.setInt(2, song.getId());
                    ps.setInt(3, i);
                }

                @Override
                public int getBatchSize() {
                    return newList.size();
                }
            });
        } catch (DataAccessException e) {
            logger.error("failed to insert songs");
            throw new PersistenceException("failed to insert songs", e);
        }
    }

    @Override
    public List<SongDTO> getAllPlaylistSongs() throws PersistenceException {
        List<SongDTO> allSongs;
        try {
            allSongs = jdbcTemplate.query(GET_ALL_PLAYLIST_SONGS, RowMappers.getSongRowMapper());
            if(allSongs.size() <= 0) {
                return Collections.emptyList();
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting songs from all playlists");
            throw new PersistenceException("ResultSet error while songs from all playlists", e);
        } catch (DataAccessException e) {
            logger.error("failed to read all songs from playlist");
            throw new PersistenceException("failed to read all songs from playlist", e);
        }
        return allSongs;
    }

    @Override
    public void updateSongsInPlaylist(int playlistId, List<SongDTO> songs) throws PersistenceException {
        try {
            deleteSongs(playlistId);

            jdbcTemplate.batchUpdate(INSERT_SONGS, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    SongDTO song = songs.get(i);
                    ps.setInt(1, playlistId);
                    ps.setInt(2, song.getId());
                    ps.setInt(3, i);
                }

                @Override
                public int getBatchSize() {
                    return songs.size();
                }
            });
        } catch (DataAccessException e) {
            logger.error("failed to upadate songs in playlist");
            throw new PersistenceException("failed to upadate songs in playlist", e);
        }
    }
}

