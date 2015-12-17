package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.SongDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lena Lenz.
 */
@Repository
public class JdbcSongDao extends JdbcBaseDao implements SongDAO {

    private static final Logger logger = Logger.getLogger(JdbcSongDao.class);

    private static final int MAX_LENGTH_FILEPATH = 300;
    private static final int MAX_LENGTH_TITLE = 200;
    private static final int MAX_LENGTH_GENRE = 100;
    private static final int MAX_LENGTH_LYRICS = 8000;

    private static final String INSERT = "INSERT INTO Song(id, filepath, name, length, artist_id, album_id, genre, rating, lyrics) VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SET_RATING = "UPDATE Song SET rating=? WHERE id=?";
    private static final String UPDATE_SET_LYRICS = "UPDATE Song SET lyrics=? WHERE id=?";
    private static final String UPDATE = "UPDATE Song SET filepath=?, name=?, length=?, artist_id=?, album_id=?, genre=?, rating=?, lyrics=? WHERE id=?";
    private static final String DELETE = "DELETE FROM Song WHERE id=?";

    private static final String GET_ALL = "SELECT * FROM Song";
    private static final String FIND_BY_ID = "SELECT * FROM Song WHERE id=?";
    private static final String FIND_BY_NAME = "SELECT * FROM Song WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";

    private static final String COUNT = "SELECT COUNT(*) FROM Song WHERE id=?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM Song";
    private static final String COUNT_NAME = "SELECT COUNT(*) FROM Song WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";


    @Autowired
    JdbcArtistDao jdbcArtistDao;

    @Autowired
    JdbcAlbumDao jdbcAlbumDao;


    @Override
    public void persist(SongDTO toPersist) throws PersistenceException {
        // check for errors
        if (toPersist == null) {
            logger.error("song is null");
            throw new PersistenceException("song is null");
        }
        if (toPersist.getPath() != null && toPersist.getPath().length() > MAX_LENGTH_FILEPATH) {
            logger.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_FILEPATH));
            throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_FILEPATH));
        }
        if (toPersist.getName() != null && toPersist.getName().length() > MAX_LENGTH_TITLE) {
            logger.error(ErrorMessages.errorTitleSize(MAX_LENGTH_TITLE));
            throw new PersistenceException(ErrorMessages.errorTitleSize(MAX_LENGTH_TITLE));
        }
        if (toPersist.getGenre() != null && toPersist.getGenre().length() > MAX_LENGTH_GENRE) {
            logger.error(ErrorMessages.errorGenreSize(MAX_LENGTH_GENRE));
            throw new PersistenceException(ErrorMessages.errorGenreSize(MAX_LENGTH_GENRE));
        }
        if (toPersist.getLyrics() != null && toPersist.getLyrics().length() > MAX_LENGTH_LYRICS) {
            logger.error(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
            throw new PersistenceException(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
        }

        int newId;
        try {
            ArtistDTO artistDTO = null;
            // check whether artist id or his name got stored in toPersist
            if(toPersist.getArtistId() != -1) {
                artistDTO = jdbcArtistDao.read(toPersist.getArtistId());
            }
            else if(toPersist.getArtistName() != null) {
                artistDTO = jdbcArtistDao.read(toPersist.getArtistName());
            }
            if(artistDTO == null) {
                logger.error("artist to song does not exist in database");
                throw new PersistenceException("artist does not exist in database");
            }

            AlbumDTO albumDTO = null;
            // check whether album id or its name got stored in toPersist
            if(toPersist.getAlbumId() != -1) {
                albumDTO = jdbcAlbumDao.read(toPersist.getAlbumId());
            }
            else if(toPersist.getAlbum() != null) {
                albumDTO = jdbcAlbumDao.read(toPersist.getAlbum(), artistDTO.getId());
            }
            if(albumDTO == null) {
                logger.error("album to song does not exist in database");
                throw new PersistenceException("album does not exist in database");
            }
            int artistId = artistDTO.getId();
            int albumId = albumDTO.getId();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT,
                        new String[]{"id"});

                preparedStatement.setString(1, toPersist.getPath());
                preparedStatement.setString(2, toPersist.getName());
                preparedStatement.setLong(3, toPersist.getLength());
                preparedStatement.setInt(4, artistId);
                preparedStatement.setInt(5, albumId);
                preparedStatement.setString(6, toPersist.getGenre());
                preparedStatement.setInt(7, toPersist.getRating());
                preparedStatement.setString(8, toPersist.getLyrics());
                return preparedStatement;
            }, keyHolder);

              newId = keyHolder.getKey().intValue();

            toPersist.setId(newId);
            toPersist.setArtistId(artistId);
            toPersist.setAlbumId(albumId);
            toPersist.setArtistName(jdbcArtistDao.read(artistId).getName());
            toPersist.setAlbumName(jdbcAlbumDao.read(albumId).getName());
            //toPersist.setReleaseYear(jdbcAlbumDao.read(albumId).getReleaseYear());
        } catch(DataAccessException e) {
            logger.error("failed to persist song", e);
            throw new PersistenceException("failed to persist song", e);
        }
        logger.debug("new song with id=" + newId + " stored.");
    }

    @Override
    public void persistRating(int id, int rating) throws PersistenceException {
        if(rating < 1 || rating > 5) {
            logger.error("invalid rating!");
            throw new PersistenceException("invalid rating!");
        }
        try {
            jdbcTemplate.update(UPDATE_SET_RATING, rating, id);
        } catch(DataAccessException e) {
            logger.error("failed to persist rating");
            throw new PersistenceException("failed to persist rating", e);
        }
        logger.debug("set rating=" + rating + " for song with id=" + id + ".");
    }

    @Override
    public void persistLyrics(int id, String lyrics) throws PersistenceException{
        try {
            if(lyrics == null) {
                logger.error("lyrics null");
                throw new PersistenceException("lyrics null");
            }
            if(lyrics.length() > MAX_LENGTH_LYRICS) {
                logger.error(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
                throw new PersistenceException(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
            }

            jdbcTemplate.update(UPDATE_SET_LYRICS, lyrics, id);
        } catch(DataAccessException e) {
            logger.error("failed to persist lyrics");
            throw new PersistenceException("failed to persist lyrics", e);
        }

        logger.debug("set lyrics for song with id=" + id + ".");
    }

    @Override
    public List<SongDTO> findByName(String name) throws PersistenceException {
        List<SongDTO> filteredSongs;
        try {
            filteredSongs = jdbcTemplate.query(FIND_BY_NAME, RowMappers.getSongRowMapper(), name);
            if(filteredSongs.size() == 0) {
                logger.debug("no song with name like " + name);
                return Collections.emptyList();
            }

            for(SongDTO song : filteredSongs) {
                song.setArtistName(jdbcArtistDao.read(song.getArtistId()).getName());
                song.setAlbumName(jdbcAlbumDao.read(song.getAlbumId()).getName());
                song.setReleaseYear(jdbcAlbumDao.read(song.getAlbumId()).getReleaseYear());
            }
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting song");
            throw new PersistenceException("ResultSet error while counting song", e);
        } catch(DataAccessException e) {
            logger.error("failed to read song with name="+ name);
            throw new PersistenceException("failed to read song with name="+ name, e);
        }

        return filteredSongs;
    }

    @Override
    public SongDTO read(int id) throws PersistenceException {
        SongDTO song = new SongDTO();
        try {
            song = jdbcTemplate.queryForObject(FIND_BY_ID, RowMappers.getSongRowMapper(), id);
            if(song == null) {
                throw new PersistenceException("No Song with id = "+ id);
            }

            song.setArtistName(jdbcArtistDao.read(song.getArtistId()).getName());
            song.setAlbumName(jdbcAlbumDao.read(song.getAlbumId()).getName());
            song.setReleaseYear(jdbcAlbumDao.read(song.getAlbumId()).getReleaseYear());
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting song");
            throw new PersistenceException("ResultSet error while counting song", e);
        } catch(DataAccessException e) {
            logger.error("failed to read song with id="+ id);
            throw new PersistenceException("failed to read song with id="+ id, e);
        }
        return song;
    }


    @Override
    public List<SongDTO> readAll() throws PersistenceException {
        List<SongDTO> songs = new ArrayList<>();
        try {
            if (jdbcTemplate.queryForObject(COUNT_ALL, RowMappers.getCountRowMapper()) == 0) {
                // no songs stored yet
                return Collections.emptyList();
            }

            songs = jdbcTemplate.query(GET_ALL, RowMappers.getSongRowMapper());

            for (SongDTO s : songs) {
                s.setArtistName(jdbcArtistDao.read(s.getArtistId()).getName());
                s.setAlbumName(jdbcAlbumDao.read(s.getAlbumId()).getName());
            }
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting songs");
            throw new PersistenceException("ResultSet error while counting songs", e);
        } catch(DataAccessException e) {
            logger.error("failed to read songs");
            throw new PersistenceException("failed to read songs", e);
        }

        return songs;
    }

    @Override
    public void remove(int id) throws PersistenceException {
        try {
            if(jdbcTemplate.queryForObject(COUNT, RowMappers.getCountRowMapper(), id) == 0) {
                logger.debug("no song with this id stored!");
                throw new PersistenceException("no song with id = " +id);
            }
            jdbcTemplate.update(DELETE, id);
        } catch(DataAccessException e) {
            logger.error("failed to remove song with id="+ id);
            throw new PersistenceException("failed to remove song with id="+ id, e);
        }
        logger.debug("removed song with id=" + id + ".");
    }

    @Override
    public void update(SongDTO toUpdate) throws PersistenceException {
        // update method not really necessary for song - use persistRating / persistLyrics instead
        try {
            // check for errors
            if (toUpdate == null) {
                logger.error("song is null");
                throw new PersistenceException("song is null");
            }
            if (toUpdate.getPath() != null && toUpdate.getPath().length() > MAX_LENGTH_FILEPATH) {
                logger.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_FILEPATH));
                throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_FILEPATH));
            }
            if (toUpdate.getName() != null && toUpdate.getName().length() > MAX_LENGTH_TITLE) {
                logger.error(ErrorMessages.errorTitleSize(MAX_LENGTH_TITLE));
                throw new PersistenceException(ErrorMessages.errorTitleSize(MAX_LENGTH_TITLE));
            }
            if (toUpdate.getGenre() != null && toUpdate.getGenre().length() > MAX_LENGTH_GENRE) {
                logger.error(ErrorMessages.errorGenreSize(MAX_LENGTH_GENRE));
                throw new PersistenceException(ErrorMessages.errorGenreSize(MAX_LENGTH_GENRE));
            }
            if(toUpdate.getLyrics() != null && toUpdate.getLyrics().length() > MAX_LENGTH_LYRICS) {
                logger.error(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
                throw new PersistenceException(ErrorMessages.errorLyricsSize(MAX_LENGTH_LYRICS));
            }

            ArtistDTO artistDTO = null;
            // check whether artist id or his name got stored in toPersist
            if(toUpdate.getArtistId() != -1) {
                artistDTO = jdbcArtistDao.read(toUpdate.getArtistId());
            }
            else if(toUpdate.getArtistName() != null) {
                artistDTO = jdbcArtistDao.read(toUpdate.getArtistName());
            }
            if(artistDTO == null) {
                logger.error("artist to song does not exist in database");
                throw new PersistenceException("artist does not exist in database");
            }
            AlbumDTO albumDTO = null;
            // check whether album id or its name got stored in toPersist
            if(toUpdate.getAlbumId() != -1) {
                albumDTO = jdbcAlbumDao.read(toUpdate.getAlbumId());
            }
            else if(toUpdate.getAlbum() != null) {
                albumDTO = jdbcAlbumDao.read(toUpdate.getAlbum(), artistDTO.getId());
            }
            if(albumDTO == null) {
                logger.error("album to song does not exist in database");
                throw new PersistenceException("album does not exist in database");
            }

            int artistId = artistDTO.getId();
            int albumId = albumDTO.getId();

            jdbcTemplate.update(UPDATE, toUpdate.getPath(), toUpdate.getName(), toUpdate.getLength(), artistId, albumId, toUpdate.getGenre(), toUpdate.getRating(), toUpdate.getLyrics(), toUpdate.getId());

            toUpdate.setArtistId(artistId);
            toUpdate.setAlbumId(albumId);
            toUpdate.setArtistName(jdbcArtistDao.read(artistId).getName());
            toUpdate.setAlbumName(jdbcAlbumDao.read(albumId).getName());
            toUpdate.setReleaseYear(jdbcAlbumDao.read(albumId).getReleaseYear());
        } catch(DataAccessException e) {
            logger.error("failed to update song");
            throw new PersistenceException("failed to update song", e);
        }

        logger.debug("updated song with id=" + toUpdate.getId() + ".");
    }
}
