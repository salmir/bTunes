package DAO;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.ArtistDTO;
import at.ac.tuwien.sepm.musicplayer.persistance.ArtistDAO;
import at.ac.tuwien.sepm.musicplayer.persistance.h2.JdbcArtistDao;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.apache.commons.lang.StringUtils;


import java.sql.SQLException;

/**
 * Created by Alexandra on 06.05.2015.
 */
public class DAOArtistTest extends AbstractDAOArtistTest{


    @BeforeClass
    public static void setUpClass() throws SQLException{
        // Manually insert sql-statements as in file 'insert_song.sql'
    }

    @Before
    public void setUp() throws SQLException {
        // valid artist
        validArtist = new ArtistDTO();
        validArtist.setName("DaoArtistTest");
        validArtist.setImage(StringUtils.leftPad("", 200, '*'));
        validArtist.setBiography(StringUtils.leftPad("", 8000, '*'));

        // invalid artist
        invalidArtist = new ArtistDTO();
        invalidArtist.setName(StringUtils.leftPad("", 201, '*'));
        invalidArtist.setImage(StringUtils.leftPad("", 201, '*'));
        invalidArtist.setBiography(StringUtils.leftPad("", 8001, '*'));
    }

    @After
    public void tearDown() throws SQLException{
        jdbcTemplate.update("DELETE FROM Artist WHERE name=?", "DaoArtistTest");
    }


}
