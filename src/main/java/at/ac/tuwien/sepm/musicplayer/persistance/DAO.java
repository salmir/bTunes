package at.ac.tuwien.sepm.musicplayer.persistance;

import at.ac.tuwien.sepm.musicplayer.exceptions.PersistenceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.DaoEntity;

import java.util.List;

/**
 * Interface that includes the main CRUD - methods for each implementation
 *
 * Created by Lena Lenz.
 */
public interface DAO<A extends DTO<? extends DaoEntity>> {

    /**
     * persists object and assigns a new ID to it
     * this method should throw an exception if toPersist is invalid or has already an ID
     * todo: give the range of valid values!
     *
     * @param toPersist entity which should get persisted
     * @throws PersistenceException if persistence not available or fails
     */
    void persist(A toPersist) throws PersistenceException;

    /**
     * reads the object with the specified ID.
     *
     * @param id of entity which should get persisted
     * @return loaded entity from Persistence with specified ID
     * @throws PersistenceException if persistence not available or there is no object with this ID
     */
    A read(int id) throws PersistenceException;

    /**
     * reads all objects available in the persistence
     *
     * @return a list of A-entities
     * @throws PersistenceException if persistence not available
     */
    List<A> readAll() throws PersistenceException;


    /**
     * deletes the object with the specified ID
     *
     * @param id of entity which should get removed
     * @throws PersistenceException if persistence not available or if it fails
     */
    void remove(int id) throws PersistenceException;

    /**
     * updates a object with the attributes of toUpdate in the implemented persistence
     *
     * @param toUpdate entity with new values of its attributes
     * @throws PersistenceException if persistence not available or if it fails
     */
    void update(A toUpdate) throws PersistenceException;
}
