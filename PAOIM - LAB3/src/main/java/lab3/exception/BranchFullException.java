package lab3.exception;

/** Oddział nie przyjmie kolejnego laptopa, bo osiągnięto pojemność. */
public class BranchFullException extends StoreException {
    public BranchFullException(String message) {
        super(message);
    }
}
