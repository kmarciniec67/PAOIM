package lab3.exception;

/**
 * Bazowy wyjątek sprawdzany warstwy modelu sklepu.
 * Błędy biznesowe nie są zgłaszane jako {@link RuntimeException}.
 */
public class StoreException extends Exception {
    public StoreException(String message) {
        super(message);
    }
}
