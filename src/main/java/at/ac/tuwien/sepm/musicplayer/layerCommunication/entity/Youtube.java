package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity;

/**
 * Created by Alexandra on 08.06.2015.
 */
public class Youtube {

    private int nummer;
    private String titel;
    private String songpath;
    private String imagepath;

    public Youtube(int nummer, String titel, String songpath, String imagepath){
        this.nummer = nummer;
        this.titel = titel;
        this.songpath = songpath;
        this.imagepath = imagepath;
    }

    public Youtube(int nummer, String titel){
        this.nummer = nummer;
        this.titel = titel;
        songpath = "";
        imagepath ="";

    }

    public Youtube(){
        this.nummer = 0;
        this.titel = "";
        this.songpath = "";
        this.imagepath = "";
    }

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getSongpath() {
        return songpath;
    }

    public void setSongpath(String songpath) {
        this.songpath = songpath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
