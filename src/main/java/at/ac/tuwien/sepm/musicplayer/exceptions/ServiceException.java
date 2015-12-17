package at.ac.tuwien.sepm.musicplayer.exceptions;

import java.io.File;

/**
 * Created by Lena Lenz.
 */
public class ServiceException extends Exception {

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ServiceFileNotFoundException extends ServiceException{
        private File file;

        public ServiceFileNotFoundException(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}
