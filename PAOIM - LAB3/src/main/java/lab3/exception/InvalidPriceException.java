package lab3.exception;

/** Cena jest ujemna albo nie jest skończoną liczbą. */
public class InvalidPriceException extends StoreException {
    public InvalidPriceException(String message) {
        super(message);
    }
}
