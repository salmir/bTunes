package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.DelegatingInfo;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.Info;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoDelegation;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

import java.util.Date;


public class Statistic  implements DaoEntity {


    private int id;


    private Date datePlayed;
    private Song songPlayed;

    public Statistic(int id, Song songPlayed) {
        this.id = id;
        this.songPlayed = songPlayed;
        this.datePlayed = new Date();
    }

    public Statistic(int id, Song songPlayed, Date datePlayed) {
        this.id = id;
        this.songPlayed = songPlayed;
        this.datePlayed = datePlayed;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EntityType<? extends DaoEntity> getEntityType() {
        return Type.STATISTIC;
    }


    @Override
    public String getName() {
        return null;
    }


    @Override
    public String getIdentifier() {
        return songPlayed.getIdentifier() + " " + datePlayed;
    }


    public Date getDatePlayed() {
        return datePlayed;
    }

    public void setDatePlayed(Date datePlayed) {
        this.datePlayed = datePlayed;
    }

    public Song getSongPlayed() {
        return songPlayed;
    }

    public void setSongPlayed(Song songPlayed) {
        this.songPlayed = songPlayed;
    }

}
