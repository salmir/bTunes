package DAO;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.AlbumDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.AlbumDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcAlbumDao;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Created by Alexandra on 06.05.2015.
 */
public class DAOAlbumTest extends AbstractDAOAlbumTest{

    @BeforeClass
    public static void setUpClass() throws SQLException{
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException {
        // valid abum
        validAlbum = new AlbumDTO();
        validAlbum.setName("DaoAlbumTest");
        validAlbum.setReleaseYear(2000);
        validAlbum.setArtistName("ArtistTest");
        validAlbum.setCover(StringUtils.leftPad("", 300, '*'));

        // invalid album
        invalidAlbum = new AlbumDTO();
        invalidAlbum.setName(StringUtils.leftPad("", 201, '*'));
        invalidAlbum.setCover(StringUtils.leftPad("", 301, '*'));
        invalidAlbum.setReleaseYear(2000);
        invalidAlbum.setArtistName(null);


    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Album WHERE name=?", "DaoAlbumTest");
    }




}
