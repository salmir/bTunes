package at.ac.tuwien.sepm.musicplayer.exceptions;

/**
 * Created by Lena Lenz.
 */
public class PersistenceException extends Exception {

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
