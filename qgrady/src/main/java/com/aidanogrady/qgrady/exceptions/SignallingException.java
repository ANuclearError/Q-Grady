package com.aidanogrady.qgrady.exceptions;

/**
 * Signals that the set-up of the non-local box specified in the source file
 * is not non-signalling, meaning that it is not valid for file generation.
 *
 * @author Aidan O'Grady
 * @since 0.5.1
 */
public class SignallingException extends Exception {
    public SignallingException(String msg) {
        super(msg);
    }
}
