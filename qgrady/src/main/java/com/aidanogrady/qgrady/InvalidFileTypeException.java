package com.aidanogrady.qgrady;

/**
 * The InvalidFileTypeException is for the system is attempting to use a file or
 * directory that does exist, but is not what is desired for the system. To
 * quote Roy Walker "It's good but it's not right."
 *
 * Such examples of usage would be where the system is requiring a .txt file but
 * is given a directory or .gif instead.
 *
 * @author Aidan O'Grady
 * @since 0.1
 */
public class InvalidFileTypeException extends Exception {
    public InvalidFileTypeException(String msg) {
        super(msg);
    }
}
