package at.apf.stexlife.api.exception;

import at.apf.stexlife.api.DataType;

import java.util.stream.Stream;

public class InvalidTypeException extends StexLifeException {

    public InvalidTypeException(DataType received, DataType expected) {
        super("Expected " + expected.name() + ", but got " + received.name());
    }

    public InvalidTypeException(DataType received, DataType[] expected) {
        super("Expected one of " + Stream.of(expected).map(e -> e.name()).reduce((acc, str) -> acc + ", " + str)
                + ", but got " + received.name());
    }

}
