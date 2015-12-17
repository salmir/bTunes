package at.ac.tuwien.sepm.musicplayer.persistance.h2;

/**
 * Class with unified error-messages for the persisting methods.
 *
 * Created by Lena Lenz.
 */
public class ErrorMessages {

    public static String errorFilePathSize(int i) {
        return "Too long file path! (max. length is "+ i +")";
    }

    public static String errorTitleSize(int i) {
        return "Too long song title! (max. length is "+ i +")";
    }

    public static String errorGenreSize(int i) {
        return "Too long genre! (max. length is "+ i +")";
    }

    public static String errorLyricsSize(int i) {
        return "Too long lyrics! (max length is "+ i +")";
    }

    public static String errorArtistNameSize(int i) {
        return "Too long artist name! (max. length is "+ i +")";
    }

    public static String errorBiographySize(int i) {
        return "Too long artist biography! (max. length is "+ i +")";
    }

    public static String errorAlbumTitleSize(int i) {
        return "Too long album title! (max. length is "+ i +")";
    }

    public static String errorPlaylistNameSize(int i) {
        return "Too long playlist name! (max. length is "+ i +")";
    }
}
