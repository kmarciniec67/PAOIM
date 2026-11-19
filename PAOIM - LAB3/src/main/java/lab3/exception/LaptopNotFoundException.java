package lab3.exception;

/** W oddziale nie ma laptopa o wskazanej tożsamości (model, marka, rok). */
public class LaptopNotFoundException extends StoreException {
    public LaptopNotFoundException(String message) {
        super(message);
    }
}
