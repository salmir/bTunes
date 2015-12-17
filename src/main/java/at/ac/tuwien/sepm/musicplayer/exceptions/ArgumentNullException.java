package at.ac.tuwien.sepm.musicplayer.exceptions;

/**
 * Created by Lena Lenz.
 */
public class ArgumentNullException extends IllegalArgumentException {

    /**
     * Instantiates a new runtime-exception
     *
     * @param argumentName Name of the missing argument
     */
    public ArgumentNullException(String argumentName) {
        super(String.format("%s is null", argumentName));
    }
}
