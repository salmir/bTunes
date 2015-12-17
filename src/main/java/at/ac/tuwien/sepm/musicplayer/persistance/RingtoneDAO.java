package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import com.google.code.mp3fenge.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by Tako on
 */

@Repository
public class RingtoneDAO {


    public void save(Ringtone ringtone) throws PersistenceException {

        if(ringtone.getFrames() != null && ringtone.getFrames().length >= 1) {
            ArrayList mp3datas = new ArrayList();
            mp3datas.add(ringtone.getFrames());
            FileUtil.generateFile(ringtone.getPath(), mp3datas);
        }else {
            throw new PersistenceException("Invalid Parameter");
        }

    }
}

