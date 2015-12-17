package service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.service.RingtoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.ValidationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Alexandra on 22.06.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")

public abstract class AbstractServiceRingtoneTest {
    @Autowired
    protected RingtoneService ringToneService;
    protected byte[] stream;
    protected Ringtone ringtoneOK;
    protected Ringtone ringtoneNotOk;
    protected final String sourceMP3 = "src/main/resources/bonjovi.mp3";
    protected final String createdRingtone = "src/main/resources/bonjovi_created.mp3";
    protected final String sourceRingtone = "src/main/resources/bonjovi_ringtone.mp3";
    protected String resultForCreatedRingtone = "";
    protected String resultForSourceRingtone = "";

    @Test
    public void createRingtoneWithValidParameterShouldbeCreated() throws ServiceException, IOException, NoSuchAlgorithmException, ValidationException {

        assertNull(ringtoneOK.getFrames());

        ringToneService.createRingtone(ringtoneOK);

        assertNotNull(ringtoneOK.getFrames());
    }

    @Test
    public void ringtoneByteShouldBeSameAfterCreaitingWithSameParameter() throws ServiceException, IOException, NoSuchAlgorithmException, ValidationException {

        ringToneService.createRingtone(ringtoneOK);
        byte[] ringtone1 = ringtoneOK.getFrames();

        ringtoneOK.setFrames(null);
        ringToneService.createRingtone(ringtoneOK);
        byte[] ringtone2 = ringtoneOK.getFrames();

        assertTrue(Arrays.equals(ringtone1, ringtone2));
    }

    @Test
    public void checksumSavedFileWithCreatedFileShouldOk() throws ServiceException, ValidationException, NoSuchAlgorithmException, IOException {

        ringToneService.createRingtone(ringtoneOK);

        FileInputStream in = new FileInputStream(createdRingtone);
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] block = new byte[4096];
        int length;
        while ((length = in.read(block)) > 0) {
            digester.update(block, 0, length);
        }
        byte [] digestBytes = digester.digest();

        for (int i=0; i < digestBytes.length; i++) {
            resultForCreatedRingtone += Integer.toString( ( digestBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
        }

        assertEquals(resultForCreatedRingtone,resultForSourceRingtone);
    }
    @Test(expected = ValidationException.class)
    public void createRingtoneWithInvalidParameterShouldFail() throws ServiceException, ValidationException {
        ringToneService.createRingtone(ringtoneNotOk);
    }

    /**
     * handle Stream with null
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void handleStreamIsNull() throws ServiceException {
        ringToneService.save(null, "./Users");
    }

    /**
     * handle path with null catch NullpointerException
     * @throws ServiceException
     */
    @Test(expected = NullPointerException.class)
    public void handleValidStream() throws ServiceException {
        ringToneService.save(stream, null);

    }

}

