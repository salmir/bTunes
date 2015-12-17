package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.persistance.RingtoneDAO;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tako
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
@TransactionConfiguration(defaultRollback=true)
public abstract class AbstractDAORingtoneTest {

    @Autowired
    protected RingtoneDAO ringtoneDAO;

    protected Ringtone ringtoneOK;
    protected Ringtone ringtoneNotOk;
    protected final String sourceMP3 = "src/main/resources/bonjovi.mp3";
    protected final String createdRingtone = "src/main/resources/bonjovi_created.mp3";


    @Test
    public void fileShouldBeSavedTrue() throws PersistenceException, IOException {
        byte[] mp3FromRingtone = ringtoneOK.getFrames();
        ringtoneDAO.save(ringtoneOK);

        File savedFile = new File(createdRingtone);
        FileInputStream input = new FileInputStream(savedFile);
        byte[] mp3FromFileSystem = IOUtils.toByteArray(input) ;

        assertTrue(Arrays.equals(mp3FromRingtone,mp3FromFileSystem));
    }

    @Test(expected = PersistenceException.class)
    public void ringtonecrateWithInvalidRingtoneShouldFail()throws PersistenceException{
        ringtoneDAO.save(ringtoneNotOk);
    }

    @Test
    public void shouldNotExists(){

        File file = new File(createdRingtone);
        assertFalse(file.exists());
    }

    @Test
    public void fileExistsTestShouldOk()throws PersistenceException{

        ringtoneDAO.save(ringtoneOK);

        File file = new File(createdRingtone);
        assertTrue(file.exists());
    }

}
