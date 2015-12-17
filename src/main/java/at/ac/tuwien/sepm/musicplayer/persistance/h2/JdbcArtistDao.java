package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

/**
 * Created by Salmir Delalic.
 */
@Repository
public class JdbcArtistDao extends JdbcBaseDao implements ArtistDAO {

    private static final Logger log = LogManager.getLogger(JdbcArtistDao.class);

    private static final int MAX_LENGTH_NAME = 200;
    private static final int MAX_LENGTH_BIOGRAPHY = 8000;
    private static final int MAX_LENGTH_IMAGE = 200;

    private static final String INSERT = "INSERT INTO Artist(id, name, biography, image) VALUES (DEFAULT,?,?,?)";
    private static final String DELETE = "DELETE FROM Artist WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM Artist WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT * FROM Artist WHERE name = ?";
    private static final String GET_ALL = "SELECT * FROM Artist";
    private static final String COUNT_ID = "SELECT COUNT(*) FROM Artist WHERE id = ?";
    private static final String COUNT_NAME = "SELECT COUNT(*) FROM Artist WHERE name = ?";
    private static final String COUNT_ALL = "SELECT COUNT (*) FROM Artist";
    private static final String UPDATE_SET_IMAGE = "UPDATE Artist SET image=? WHERE id=?";

    private static final String UPDATE_SET_BIOGRAPHY = "UPDATE Artist SET biography=? WHERE id=?";
    private static final String UPDATE = "UPDATE Artist SET name=?, biography=?, image=? WHERE id=?";

    private static final String FIND_BY_NAME_LIKE = "SELECT * FROM Artist WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";
    private static final String COUNT_NAME_LIKE = "SELECT COUNT(*) FROM Artist WHERE lcase(name) LIKE concat('%', lcase( ? ), '%')";



    @Override
    public void persistImage(int id, File file) throws PersistenceException {

        try {
            if(file == null) {
                log.error("file null");
                throw new PersistenceException("file null");
            }
            if(file.getPath().length() > MAX_LENGTH_IMAGE) {
                log.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_IMAGE));
                throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_IMAGE));
            }

            jdbcTemplate.update(UPDATE_SET_IMAGE, file.getPath(), id);

        } catch(DataAccessException e) {
            log.error("failed to persist image!");
            throw new PersistenceException("failed to persist image!", e);
        }
        log.debug("set image for artist with id=" + id + ".");
    }

    @Override
    public void persistBiography(int id, String biography) throws PersistenceException {

        try {
            if(biography == null) {
                log.error("biography null");
                throw new PersistenceException("biography null");
            }
            if(biography.length() > MAX_LENGTH_BIOGRAPHY) {
                log.error(ErrorMessages.errorLyricsSize(MAX_LENGTH_BIOGRAPHY));
                throw new PersistenceException(ErrorMessages.errorLyricsSize(MAX_LENGTH_BIOGRAPHY));
            }
            jdbcTemplate.update(UPDATE_SET_BIOGRAPHY, biography, id);
        } catch(DataAccessException e) {
            log.error("failed to persist biography!");
            throw new PersistenceException("failed to persist biography!", e);
        }

        log.debug("set biography for artist with id=" + id + ".");
    }

    @Override
    public void persist(ArtistDTO toPersist) throws PersistenceException {
        if (toPersist == null) {
            log.error("artist is null");
            throw new PersistenceException("artist is null");
        }
        if (toPersist.getName() != null && toPersist.getName().length() > MAX_LENGTH_NAME) {
            log.error(ErrorMessages.errorArtistNameSize(MAX_LENGTH_NAME));
            throw new PersistenceException(ErrorMessages.errorArtistNameSize(MAX_LENGTH_NAME));
        }
        if (toPersist.getBiography() != null && toPersist.getBiography().length() > MAX_LENGTH_BIOGRAPHY) {
            log.error(ErrorMessages.errorBiographySize(MAX_LENGTH_BIOGRAPHY));
            throw new PersistenceException(ErrorMessages.errorBiographySize(MAX_LENGTH_BIOGRAPHY));
        }
        if (toPersist.getImage() != null && toPersist.getImage().length() > MAX_LENGTH_IMAGE) {
            log.error(ErrorMessages.errorBiographySize(MAX_LENGTH_IMAGE));
            throw new PersistenceException(ErrorMessages.errorBiographySize(MAX_LENGTH_IMAGE));
        }

        int newId;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT,
                        new String[]{"id"});

                preparedStatement.setString(1, toPersist.getName());
                preparedStatement.setString(2, toPersist.getBiography());
                preparedStatement.setString(3, toPersist.getImage());
                return preparedStatement;
            }, keyHolder);

            newId = keyHolder.getKey().intValue();
            toPersist.setId(newId);

        } catch(DataAccessException e) {
            log.error("failed to persist artist!");
            throw new PersistenceException("failed to persist artist!", e);
        }
        log.debug("new artist with id=" + newId + " stored.");
    }

    @Override
    public ArtistDTO read(int id) throws PersistenceException {
        ArtistDTO artist;
        try {
            artist = jdbcTemplate.queryForObject(FIND_BY_ID, RowMappers.getArtistRowMapper(), id);
            if(artist == null) {
                log.debug("no artist with this id stored!");
                throw new PersistenceException("No Artist with id = "+id);
            }
        } catch(EmptyResultDataAccessException e) {
            log.error("ResultSet error while counting artist");
            throw new PersistenceException("ResultSet error while counting artist", e);
        } catch(DataAccessException e) {
            log.error("failed to read artist with id="+ id);
            throw new PersistenceException("failed to read artist with id="+ id, e);
        }
        return artist;
    }

    @Override
    public ArtistDTO read(String name) throws PersistenceException {
        List<ArtistDTO> artist;
        try {
            artist = jdbcTemplate.query(FIND_BY_NAME, RowMappers.getArtistRowMapper(), name);
            if(artist.size() <= 0) {
                log.debug("no artist with this id stored!");
                //throw new PersistenceException("No Artist with name=" + name);
                return null;
            }
        } catch(EmptyResultDataAccessException e) {
            log.error("ResultSet error while counting artist");
            throw new PersistenceException("ResultSet error while counting artist", e);
        } catch(DataAccessException e) {
            log.error("failed to read artist with id="+ name);
            throw new PersistenceException("failed to read artist with id="+ name, e);
        }
        if(artist.isEmpty())
            return null;
        return artist.get(0);
    }

    @Override
    public List<ArtistDTO> findByName(String name) throws PersistenceException {
        List<ArtistDTO> artists;
        try {
            artists = jdbcTemplate.query(FIND_BY_NAME_LIKE, RowMappers.getArtistRowMapper(), name);
            if(artists.size() <= 0) {
                log.debug("no artist with name like "+ name);
                return Collections.emptyList();
            }
        }  catch(Exception e) {
            log.error("failed to read artist with name = "+ name);
            throw new PersistenceException("failed to read artist with name = "+ name, e);
        }

        return artists;
    }

    @Override
    public List<ArtistDTO> readAll() throws PersistenceException {
        List<ArtistDTO> artists;

        try {
            artists = jdbcTemplate.query(GET_ALL, RowMappers.getArtistRowMapper());
            if(artists.size() <= 0) {
                log.debug("no artists stored yet!");
                return Collections.emptyList();
            }
        } catch(EmptyResultDataAccessException e) {
            log.error("ResultSet error while counting artists");
            throw new PersistenceException("ResultSet error while counting artists", e);
        } catch(DataAccessException e) {
            log.error("failed to read artists");
            throw new PersistenceException("failed to read artists", e);
        }
        return artists;
    }

    @Override
    public void remove(int id) throws PersistenceException {
        try {
            if(jdbcTemplate.update(DELETE, id) <= 0) {
                log.debug("no artist with this id stored!");
                throw new PersistenceException("no Artist with id = " +id);
            }
        } catch (DataAccessException e) {
            log.error("failed to remove artist with id="+ id);
            throw new PersistenceException("failed to remove artist with id="+ id, e);
        }
        log.debug("removed artist with id="+ id + ".");
    }

    @Override
    public void update(ArtistDTO toUpdate) throws PersistenceException {
        // update method not really necessary for song - use persistRating / persistLyrics instead
        try {
            // check for errors
            if (toUpdate == null) {
                log.error("artist is null");
                throw new PersistenceException("artist is null");
            }
            if (toUpdate.getImage() != null && toUpdate.getImage().length() > MAX_LENGTH_IMAGE) {
                log.error(ErrorMessages.errorFilePathSize(MAX_LENGTH_IMAGE));
                throw new PersistenceException(ErrorMessages.errorFilePathSize(MAX_LENGTH_IMAGE));
            }
            if (toUpdate.getName() != null && toUpdate.getName().length() > MAX_LENGTH_NAME) {
                log.error(ErrorMessages.errorTitleSize(MAX_LENGTH_NAME));
                throw new PersistenceException(ErrorMessages.errorTitleSize(MAX_LENGTH_NAME));
            }
            if (toUpdate.getBiography() != null && toUpdate.getBiography().length() > MAX_LENGTH_BIOGRAPHY) {
                log.error(ErrorMessages.errorGenreSize(MAX_LENGTH_BIOGRAPHY));
                throw new PersistenceException(ErrorMessages.errorGenreSize(MAX_LENGTH_BIOGRAPHY));
            }


            jdbcTemplate.update(UPDATE, toUpdate.getName(), toUpdate.getBiography(), toUpdate.getImage(), toUpdate.getId());

        } catch(DataAccessException e) {
            log.error("failed to update artist");
            throw new PersistenceException("failed to update artist", e);
        }

        log.debug("updated artist with id=" + toUpdate.getId() + ".");
    }
}
