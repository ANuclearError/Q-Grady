package com.aidanogrady.qgrady.exceptions;

/**
 * The InvalidRowException signals that the user has incorrectly formatted a row
 * of their probability distribution in the NLB set-up. This could mean that the
 * user has a row without the correct number of values inside of it, or that the
 * row of probabilities does not sum to one.
 *
 * @author Aidan O'Grady
 * @since 0.3.1
 */
public class InvalidRowException extends Exception {
    public InvalidRowException(String msg) {
        super(msg);
    }

}
