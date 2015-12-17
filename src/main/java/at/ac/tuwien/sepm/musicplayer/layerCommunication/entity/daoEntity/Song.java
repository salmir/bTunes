package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.AbstractInfoOwner;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.SongDTO;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.EntityType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.OwnerType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.util.Formatter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Lena Lenz.
 */
public class Song extends AbstractInfoOwner implements DaoEntity {
    private int id;
    private String name;
    private File file;
    private long length;
    private String genre;
    private int rating = -1;
    private String lyrics;
    private int releaseyear = -1;


    public Song(int id, String name, File file) throws FileNotFoundException {
        this.id = id;
        this.name = name;
        this.file = file;
        if(!file.exists()){
            throw new FileNotFoundException("file not found: "+file.getAbsolutePath());
        }
    }

    public Song(SongDTO songDTO) throws FileNotFoundException {
        this(songDTO.getId(), songDTO.getName(), new File(songDTO.getPath()));
        this.length = songDTO.getLength();
        this.genre = songDTO.getGenre();
        this.rating = songDTO.getRating();
        this.lyrics = songDTO.getLyrics();
        this.releaseyear = songDTO.getReleaseYear();
    }

    /**
     * Creates a dummy Song that is only needed for Statistic
     * @param dummyEntry Is a String that represents the dummy entry for already deleted songs in
     *                   the statistic
     */
    public Song(String dummyEntry) {
        this.id = -1;
        this.name = dummyEntry;
        this.file = null;
        this.length = -1;
        this.genre = null;
        this.lyrics = null;
    }

    @Override
    public OwnerType<Song> getOwnerType() {
        return Type.SONG;
    }
    @Override
    public EntityType<Song> getEntityType() {
        return Type.SONG;
    }

    @Override
    public String getIdentifier() {
        return this.file.getAbsolutePath();
    }

    public boolean exists(){
        return file.exists();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getReleaseyear() {
        return releaseyear;
    }

    public void setReleaseyear(int releaseyear) {
        this.releaseyear = releaseyear;
    }

    public void removeFromAllInfos() {
        for(InfoType infoType : Type.INFO_TYPES){
            if(has(infoType)){
                get(infoType).removeOwner(this);
            }
        }
    }

    public void removeAllInfos() {
        for(InfoType infoType : Type.INFO_TYPES){
            if(has(infoType)){
                remove(infoType);
            }
        }
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", file=" + file +
                ", length=" + length +
                Formatter.TO_STRING_FORMATTER.formatInfos(this)+
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                ", lyrics='" + lyrics + '\'' +
                '}';
    }
}
