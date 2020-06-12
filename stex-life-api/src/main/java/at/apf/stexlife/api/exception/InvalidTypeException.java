package at.apf.stexlife.api.exception;

import at.apf.stexlife.api.DataType;

public class InvalidTypeException extends StexLifeException {

    public InvalidTypeException(DataType received, DataType expected) {
        super("Expected " + expected.name() + ", but got " + received.name());
    }

}
