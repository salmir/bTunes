package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;

import java.util.LinkedList;

/**
 * Created by Lena Lenz.
 */
public class SongList extends LinkedList<Song> {

    private LinkedList<Song> songs;

    public SongList(){
        songs = null;
    }

    public SongList(LinkedList<Song> song){
        songs = songs;
    }

    public String toString(){
        return songs.toString();
    }

    public LinkedList<Song> getSongs(){ return songs;}

    public void setSongs(LinkedList<Song> songs){ this.songs = songs;}

    public void addSong (Song s){
        songs.add(s);
    }

    public void removeSong(Song s){
        songs.remove(s);
    }
}
