package at.ac.tuwien.sepm.musicplayer.service.impl;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;
import at.ac.tuwien.sepm.musicplayer.persistance.RingtoneDAO;
import at.ac.tuwien.sepm.musicplayer.service.RingtoneService;
import com.google.code.mp3fenge.Mp3Fenge;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tako
 */
@Service
public class RingtoneServiceImpl implements RingtoneService {

    private byte[] frames;
    private static final Logger logger = Logger.getLogger(AlbumServiceImpl.class);

    @Autowired
    private RingtoneDAO ringtoneDAO;

    public RingtoneServiceImpl() {

    }

    @Override
    public void save(byte[] stream, String path) throws ServiceException {
        if(stream == null) {
            logger.error("stream must not be null!");
            throw new ServiceException("stream is null");
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(stream);
            fos.close();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            throw new ServiceException(e.getMessage());
        } catch (IOException e) {
            //e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }


    @Override
    public void createRingtone(Ringtone ringtone) throws ServiceException, ValidationException {
        if(ringtone.getSong() != null && ringtone.getVon() <= ringtone.getBis()) {
            Mp3Fenge ringtoneHelper = new Mp3Fenge(ringtone.getSong().getFile());
            frames = ringtoneHelper.getDataByTime(ringtone.getVon(), ringtone.getBis());

            if (frames != null && frames.length >= 1) {
                ringtone.setFrames(frames);
            }

            //save();
            try{
                ringtoneDAO.save(ringtone);
            }catch (PersistenceException e){
                throw new ServiceException(e);
            }

        }else{
            throw new ValidationException("Ringtone has Invalid paramter");
        }

        //ringtoneHelper.generateNewMp3ByTime(ringtone.getPath(), ringtone.getVon(), ringtone.getBis());
    }

}

