package at.apf.stexlife.api;

import at.apf.stexlife.api.exception.InvalidTypeException;

import java.util.stream.Stream;

public enum DataType {

    UNDEFINED,
    NULL,
    INT,
    FLOAT,
    BOOL,
    STRING,
    ARRAY,
    OBJECT,
    FUNCTION,
    LIMITED; //For plugins. Can be anything but the stex programmer cant use it in any ways, just as argument for plugin functions

    public static void expecting(DataUnit received, DataType expected) {
        if (received.getType() != expected) {
            throw new InvalidTypeException(received.getType(), expected);
        }
    }

    public static void expecting(DataUnit received, DataType... expected) {
        if (!Stream.of(expected).anyMatch(e -> e == received.getType())) {
            throw new InvalidTypeException(received.getType(), expected);
        }
    }
}
