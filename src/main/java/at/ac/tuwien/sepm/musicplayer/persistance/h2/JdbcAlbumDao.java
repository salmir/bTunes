package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexandra Mai.
 */
@Repository
public class JdbcAlbumDao extends JdbcBaseDao implements AlbumDAO {

    private static Logger logger = Logger.getLogger(JdbcSongDao.class);

    private static final int MAX_LENGTH_COVER = 300;
    private static final int MAX_LENGTH_NAME = 200;

    private static final String INSERT = "INSERT INTO Album(id, name, release_year, artist_id, cover) VALUES(DEFAULT, ?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM Album WHERE id=?";

    private static final String GET_ALL = "SELECT * FROM Album";
    private static final String FIND_BY_ID = "SELECT * FROM Album WHERE id=?";
    private static final String FIND_BY_NAME = "SELECT * FROM Album WHERE name = ? AND artist_id=?";
    private static final String COUNT_ID = "SELECT COUNT(*) FROM Album WHERE id = ?";
    private static final String COUNT_NAME = "SELECT COUNT(*) FROM Album WHERE name = ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM Album";
    private static final String UPDATE = "UPDATE Album SET name=?, release_year=?, artist_id=?, cover=? WHERE id=?";
    private static final String UPDATE_SET_COVER = "UPDATE Album SET cover = ? WHERE id = ?";
    private static final String FIND_BY_NAME_LIKE = "SELECT * FROM Album WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";
    private static final String COUNT_NAME_LIKE = "SELECT COUNT(*) FROM Album WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";

    @Autowired
    JdbcArtistDao jdbcArtistDao;

    @Autowired
    JdbcSongDao jdbcSongDao;

    @Override
    public void persistImage(int id, File file) throws PersistenceException{
        //check file-size and format, and put it as path into the library
        try {
            if(file == null) {
                logger.error("file null");
                throw new PersistenceException("file null");
            }
            if(file.getPath().length() > MAX_LENGTH_COVER) {
                logger.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
                throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
            }

            jdbcTemplate.update(UPDATE_SET_COVER, file.getPath(), id);

        } catch(DataAccessException e) {
            logger.error("failed to persist cover!");
            throw new PersistenceException("failed to persist cover!", e);
        }
        logger.debug("set cover for album with id=" + id + ".");
    }

    @Override
    public void persist(AlbumDTO toPersist) throws PersistenceException {
        // check for errors
        if (toPersist == null) {
            logger.error("ablum is null");
            throw new PersistenceException("album is null");
        }
        if (toPersist.getCover() != null && toPersist.getCover().length() > MAX_LENGTH_COVER) {
            logger.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
            throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
        }
        if (toPersist.getName() != null && toPersist.getName().length() > MAX_LENGTH_NAME) {
            logger.error(ErrorMessages.errorAlbumTitleSize(MAX_LENGTH_NAME));
            throw new PersistenceException(ErrorMessages.errorTitleSize(MAX_LENGTH_NAME));
        }

        int newId ;
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
            int artistId = artistDTO.getId();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT,
                        new String[]{"id"});

                preparedStatement.setString(1, toPersist.getName());
                preparedStatement.setInt(2, toPersist.getReleaseYear());
                preparedStatement.setInt(3, artistId);
                preparedStatement.setString(4, toPersist.getCover());
                return preparedStatement;
            }, keyHolder);

            newId = keyHolder.getKey().intValue();

            toPersist.setId(newId);
            toPersist.setArtistId(artistId);
            toPersist.setArtistName(jdbcArtistDao.read(artistId).getName());
        } catch(DataAccessException e) {
            logger.error("failed to persist album");
            throw new PersistenceException("failed to persist album", e);
        }
        logger.debug("new album with id="+ newId +" stored.");
    }

    @Override
    public AlbumDTO read(int id) throws PersistenceException {
        AlbumDTO album;
        try {
            album = jdbcTemplate.queryForObject(FIND_BY_ID, RowMappers.getAlbumRowMapper(), id);
            if (album == null) {
                logger.debug("no album with this id stored!");
                throw new PersistenceException("no album with id = "+ id);
            }
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting album");
            throw new PersistenceException("ResultSet error while counting album", e);
        } catch(DataAccessException e) {
            logger.error("failed to read album with id="+ id);
            throw new PersistenceException("failed to read album with id="+ id, e);
        }
        return album;
    }

    @Override
    public AlbumDTO read(String name, int artistID) throws PersistenceException {
        List<AlbumDTO> album;
        try {
            album = jdbcTemplate.query(FIND_BY_NAME, RowMappers.getAlbumRowMapper(), name, artistID);

            if(album.size() <= 0) {
                logger.debug("no album with this id stored!");
                //throw new PersistenceException("no album with name="+ name);
                return null;
            }
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting album");
            throw new PersistenceException("ResultSet error while counting album", e);
        } catch(DataAccessException e) {
            logger.error("failed to read album with id="+ name);
            throw new PersistenceException("failed to read album with id="+ name, e);
        }
        if(album.isEmpty())
            return null;
        return album.get(0);
    }

    @Override
    public List<AlbumDTO> findByName(String name) throws PersistenceException {
        List<AlbumDTO> albums;
        try {
            albums = jdbcTemplate.query(FIND_BY_NAME_LIKE, RowMappers.getAlbumRowMapper(), name);

            if(albums.size() <= 0) {
                logger.debug("no album with name like "+ name);
                return Collections.emptyList();
            }
        }  catch(Exception e) {
            logger.error("failed to read album with name = "+ name);
            throw new PersistenceException("failed to read album with name = "+ name, e);
        }

        return albums;
    }

    @Override
    public List<AlbumDTO> readAll() throws PersistenceException {
        List<AlbumDTO> album = new ArrayList<>();
        try {
            album = jdbcTemplate.query(GET_ALL, RowMappers.getAlbumRowMapper());

            if(album.size() <= 0) {
                // no songs stored yet
                return Collections.emptyList();
            }
            for (AlbumDTO a : album) {
                a.setArtistName(jdbcArtistDao.read(a.getArtistId()).getName());
                //a.setSongName(jdbcSongDao.read(a.getSongId()).getName());
            }
        } catch(EmptyResultDataAccessException e) {
            logger.error("ResultSet error while counting albm");
            throw new PersistenceException("ResultSet error while counting album", e);
        } catch(DataAccessException e) {
            logger.error("failed to read album");
            throw new PersistenceException("failed to read album", e);
        }

        return album;
    }

    @Override
    public void remove(int id) throws PersistenceException {
        try {
            if(jdbcTemplate.update(DELETE, id) <= 0) {
                logger.debug("no album with this id stored!");
                throw new PersistenceException("no Artist with id = " +id);
            }
        } catch(DataAccessException e) {
            logger.error("failed to remove album with id="+ id);
            throw new PersistenceException("failed to remove album with id="+ id, e);
        }
        logger.debug("removed album with id="+ id + ".");
    }

    @Override
    public void update(AlbumDTO toUpdate) throws PersistenceException {
        try {
            // check for errors
            if (toUpdate == null) {
                logger.error("album is null");
                throw new PersistenceException("album is null");
            }
            if (toUpdate.getCover() != null && toUpdate.getCover().length() > MAX_LENGTH_COVER) {
                logger.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
                throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_COVER));
            }
            if (toUpdate.getName() != null && toUpdate.getName().length() > MAX_LENGTH_NAME) {
                logger.error(ErrorMessages.errorTitleSize(MAX_LENGTH_NAME));
                throw new PersistenceException(ErrorMessages.errorTitleSize(MAX_LENGTH_NAME));
            }

            ArtistDTO artistDTO = null;
            // check whether artist id or his name got stored in toUpdate
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

            int artistId = artistDTO.getId();

            jdbcTemplate.update(UPDATE, toUpdate.getName(), toUpdate.getReleaseYear(), artistId, toUpdate.getCover(), toUpdate.getId());

            toUpdate.setArtistId(artistId);
            toUpdate.setArtistName(jdbcArtistDao.read(artistId).getName());

        } catch(DataAccessException e) {
            logger.error("failed to update album");
            throw new PersistenceException("failed to update album", e);
        }

        logger.debug("updated album with id=" + toUpdate.getId() + ".");
    }
}
