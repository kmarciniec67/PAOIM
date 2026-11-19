package lab3.exception;

/** W oddziale jest już laptop o tym samym modelu, marce i roku produkcji. */
public class DuplicateLaptopException extends StoreException {
    public DuplicateLaptopException(String message) {
        super(message);
    }
}
