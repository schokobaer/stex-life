package at.apf.stexlife.runtime.exception;

import at.apf.stexlife.data.DataType;

public class InvalidTypeException extends StexLifeException {

    public InvalidTypeException(DataType received, DataType expected) {
        super("Expected " + expected.name() + ", but got " + received.name());
    }

}
