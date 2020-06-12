package at.apf.stexlife.api.exception;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;

import java.util.HashMap;
import java.util.Map;

public class StexLifeException extends RuntimeException {

    private Map<String, DataUnit> ex;

    public StexLifeException(Map<String, DataUnit> e) {
        ex = e;
    }

    public StexLifeException(String message) {
        super(message);
    }

    public StexLifeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StexLifeException(Throwable cause) {
        super(cause);
    }

    public DataUnit getException() {
        if (ex == null) {
            ex = new HashMap<>();
        }
        if (!ex.containsKey("msg")) {
            ex.put("msg", new DataUnit(getMessage(), DataType.STRING));
        }
        if (!ex.containsKey("type")) {
            ex.put("type", new DataUnit(this.getClass().getName(), DataType.STRING));
        }
        return new DataUnit(ex, DataType.OBJECT);
    }

}
