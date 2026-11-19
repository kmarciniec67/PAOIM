package lab3.exception;

/** W sieci nie ma oddziału o podanej nazwie. */
public class BranchNotFoundException extends StoreException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
