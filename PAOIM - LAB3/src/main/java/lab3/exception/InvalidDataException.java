package lab3.exception;

/** Dane wejściowe są puste albo poza dozwolonym zakresem. */
public class InvalidDataException extends StoreException {
    public InvalidDataException(String message) {
        super(message);
    }
}
