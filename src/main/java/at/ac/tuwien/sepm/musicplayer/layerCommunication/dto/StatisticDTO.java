package at.ac.tuwien.sepm.musicplayer.layerCommunication.dto;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Statistic;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;

import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Created by Alexandra on 10.05.2015.
 */
public class StatisticDTO extends DTO<Statistic>{

    private Integer id;
    private Date dateSongPlayed;
    private Integer songPlayedId;

    public StatisticDTO(int songId, Date date) {
        dateSongPlayed = date;
        songPlayedId = songId;
    }

    public StatisticDTO() {};

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return songPlayedId + " " + dateSongPlayed;
    }

    public Integer getSongPlayedId() {
        return songPlayedId;
    }

    public void setSongPlayedId(Integer songPlayedId) {
        this.songPlayedId = songPlayedId;
    }

    public Date getDateSongPlayed() {
        return dateSongPlayed;
    }

    public void setDateSongPlayed(Date dateSongPlayed) {
        this.dateSongPlayed = dateSongPlayed;
    }

    @Override
    public Statistic createNew() throws FileNotFoundException {
        return null;
    }
    @Override
    public EntityType<Statistic> getEntityType() {
        return Type.STATISTIC;
    }
}
