package lab3.exception;

/** Oddział o tej nazwie jest już w sieci. */
public class DuplicateBranchException extends StoreException {
    public DuplicateBranchException(String message) {
        super(message);
    }
}
