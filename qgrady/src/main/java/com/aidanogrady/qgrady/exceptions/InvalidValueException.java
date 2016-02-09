package com.aidanogrady.qgrady.exceptions;

/**
 * The InvalidValueException indicates that an invalid numeric value was found
 * inside the set-up Q'Grady file. Since the values are probabilities, there
 * can be no values between greater than 1 or less than 0.
 *
 * @author Aidan O'Grady
 * @since 0.1
 */
public class InvalidValueException extends Exception {
    public InvalidValueException(String msg) {
        super(msg);
    }
}
