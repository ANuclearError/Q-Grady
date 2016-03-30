package com.aidanogrady.qgrady.exceptions;

/**
 * Exception for when variables are introduced to the system that cannot be
 * added. This can be the case when there are repeated variables or PRISM
 * keywords being used as variables.
 *
 * @author Aidan O'Grady
 * @since 0.7
 */
public class InvalidVariableException extends Exception {
    public InvalidVariableException(String msg) {
        super(msg);
    }
}
