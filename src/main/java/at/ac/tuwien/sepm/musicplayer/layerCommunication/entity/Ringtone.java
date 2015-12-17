package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;

import java.io.File;

/**
 * Created by Tako
 */
public class Ringtone {

    private Song song;
    private int von;
    private int bis;
    private String name;
    private File path;
    private byte[] frames;

    public String getName() { return name; }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ringtone() {
    }

    public Ringtone(Song song, int von, int bis) {
        this.song = song;
        this.von = von;
        this.bis = bis;
    }

    public Ringtone(File path) {this.path = path;}

    public Song getSong() {return song;}

    public void setSong(Song song) {this.song = song;}

    public int getVon() {return von;}

    public void setVon(int von) {this.von = von;}

    public int getBis() {return bis;}

    public void setBis(int bis) {this.bis = bis;}

    public byte[] getFrames() { return frames; }

    public void setFrames(byte[] frames) { this.frames = frames;}
}


