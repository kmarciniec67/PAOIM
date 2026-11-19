package lab3.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Wyjątki sprawdzane")
public class ExceptionTest {
    @Test
    void domainErrorsAreCheckedExceptions() {
        assertChecked(BranchFullException.class, new BranchFullException("pełny"), "pełny");
        assertChecked(LaptopNotFoundException.class, new LaptopNotFoundException("brak"), "brak");
        assertChecked(DuplicateLaptopException.class, new DuplicateLaptopException("duplikat"), "duplikat");
        assertChecked(InvalidPriceException.class, new InvalidPriceException("cena"), "cena");
        assertChecked(InvalidDataException.class, new InvalidDataException("dane"), "dane");
        assertChecked(BranchNotFoundException.class, new BranchNotFoundException("oddział"), "oddział");
        assertChecked(DuplicateBranchException.class, new DuplicateBranchException("oddział"), "oddział");
    }

    private static void assertChecked(Class<? extends StoreException> type, StoreException exception, String expectedMessage) {
        assertTrue(Exception.class.isAssignableFrom(type));
        assertFalse(RuntimeException.class.isAssignableFrom(type));
        assertTrue(StoreException.class.isAssignableFrom(type));
        assertEquals(type, exception.getClass());
        assertEquals(expectedMessage, exception.getMessage());
    }
}
