package at.apf.stexlife.runtime.exception;

public class NameAlreadyDeclaredException extends StexLifeException {

    public NameAlreadyDeclaredException(String message) {
        super(message);
    }

    public NameAlreadyDeclaredException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameAlreadyDeclaredException(Throwable cause) {
        super(cause);
    }
}
