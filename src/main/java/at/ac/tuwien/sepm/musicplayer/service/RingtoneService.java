package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Ringtone;

import javax.xml.bind.ValidationException;

/**
 * Created by Tako
 */
public interface RingtoneService {

    public void save(byte[] stream, String path) throws ServiceException;

    /**
     * saves the RingtoneShare Object in File System
     * @param ringtone to save
     * @throws ServiceException if not
     */
    public void createRingtone(Ringtone ringtone) throws ServiceException, ValidationException;

}
