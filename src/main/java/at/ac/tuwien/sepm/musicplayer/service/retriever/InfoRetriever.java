package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.DTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.DaoEntity;
import at.ac.tuwien.sepm.musicplayer.service.Service;

/**
 * Created by Lena Lenz.
 */
public interface InfoRetriever<A extends DaoEntity, B extends Service<? extends DTO<A>>> {

    /**
     *
     *
     * @param entity
     * @param service
     * @throws ServiceException
     */
    void retrieveInfo(A entity, B service) throws ServiceException;

    /**
     * get API key of bTunes-user on LastFM
     * @return API key of LastFM
     */
    default String getLastFMKey() {
        return "6107f246b80d4d0b9605077fb8763c7a"; //todo: retrieve key from user
    }
}
