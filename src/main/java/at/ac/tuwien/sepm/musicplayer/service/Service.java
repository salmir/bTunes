package at.ac.tuwien.sepm.musicplayer.service;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;

import java.util.List;

/**
 * Created by Lena Lenz.
 */
public interface Service<A extends DTO<? extends Entity>> {
    /**
     * persists object and assigns a new id to it
     * this method should throw an exception if toPersist already has an id
     * @param toPersist entity which should get persisted
     * @throws ServiceException if persistence not available or fails
     * @throws ValidationException if toPersist invalid
     */
    void persist(A toPersist) throws ValidationException, ServiceException;

    /**
     * reads the object with the specified id
     * @param id of entity which should get persisted
     * @return loaded entity from db with spedified id
     * @throws ServiceException if persistence not available
     * @throws ValidationException if id invalid
     */
    A read(int id) throws ValidationException, ServiceException;

    /**
     * reads all objects available in the DAO
     * @return a list of A-entities
     * @throws ServiceException if persistence not available
     */
    List<A> readAll() throws ServiceException;

    /**
     * deletes the object with the specified id
     * @param id of entity which should get removed
     * @throws ServiceException if persistence not available or if it fails
     * @throws ValidationException if id invalid
     */
    void remove(int id) throws ValidationException, ServiceException;

    /**
     * updates a object with the attributes of toUpdate in the implemented persistence
     *
     * @param toUpdate entity with new informations of attributes
     * @throws ServiceException if persistence not available or if it fails
     * @throws ValidationException if toUpdate invalid
     */
    void update(A toUpdate) throws ValidationException, ServiceException;
}
