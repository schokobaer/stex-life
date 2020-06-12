package at.apf.stexlife.runtime.exception;

public class NameNotFoundException extends StexLifeException {

    public NameNotFoundException(String message) {
        super(message);
    }

    public NameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameNotFoundException(Throwable cause) {
        super(cause);
    }
}
