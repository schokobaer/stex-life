package at.apf.stexlife.exception;

import at.apf.stexlife.api.exception.StexLifeException;

public class UncaughtExceptionException extends RuntimeException {

    public UncaughtExceptionException(StexLifeException e) {
        super(e);
    }
}
