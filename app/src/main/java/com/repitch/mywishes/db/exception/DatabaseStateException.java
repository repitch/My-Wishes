package com.repitch.mywishes.db.exception;

/**
 * @author i.demidov
 */
public class DatabaseStateException extends IllegalStateException {

    public DatabaseStateException(Throwable throwable) {
        super("Database illegal state while querying", throwable);
    }
}
