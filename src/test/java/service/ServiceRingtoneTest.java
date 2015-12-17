package service;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;

/**
 * Created by Tako
 */

public class ServiceRingtoneTest extends AbstractServiceRingtoneTest{


    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException {

        stream = new String("Save ring tone test").getBytes();

        ringtoneNotOk = mock(Ringtone.class, "null");

        File fileSource = new File(sourceMP3);
        File fileToSave = new File(createdRingtone);

        Song song = new Song(1,"Bon Jovi",fileSource);
        ringtoneOK = new Ringtone(song,10,40);
        ringtoneOK.setPath(fileToSave);


        //Checksumme calculate for the existing Ringtone
        FileInputStream in = new FileInputStream(sourceRingtone);
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] block = new byte[4096];
        int length;
        while ((length = in.read(block)) > 0) {
            digester.update(block, 0, length);
        }
        byte [] digestBytes = digester.digest();

        for (int i=0; i < digestBytes.length; i++) {
            resultForSourceRingtone += Integer.toString( ( digestBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
    }

    @After
    public void tearDown() {
        File file = new File(createdRingtone);
        if(file.exists()) {
            file.delete();
        }
    }
}

