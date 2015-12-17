package DAO;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import org.junit.After;
import org.junit.Before;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;

/**
 * Created by Tako
 */
public class DAORingtoneTest extends AbstractDAORingtoneTest {

    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException, ServiceException, ValidationException {

        ringtoneNotOk = mock(Ringtone.class, "null");

        File fileSource = new File(sourceMP3);
        File fileToSave = new File(createdRingtone);

        Song song = new Song(1,"Bon Jovi",fileSource);
        byte [] mp3 = fileSource.getPath().getBytes();
        ringtoneOK = new Ringtone(song,10,40);
        ringtoneOK.setPath(fileToSave);
        ringtoneOK.setFrames(mp3);


    }

    @After
    public void tearDown() {
        File file = new File(createdRingtone);
        if(file.exists()) {
            file.delete();
        }
    }


}
