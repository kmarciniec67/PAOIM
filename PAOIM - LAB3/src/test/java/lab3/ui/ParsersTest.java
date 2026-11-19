package lab3.ui;

import lab3.exception.InvalidDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Parsers")
public class ParsersTest {
    @Test
    void parsesIntegersAndDecimals() throws InvalidDataException {
        assertEquals(12, Parsers.integer(" 12 ", "Pojemność"));
        assertEquals(10.5, Parsers.decimal("10.5", "Cena"), 0.001);
        assertEquals(10.5, Parsers.decimal(" 10,5 ", "Cena"), 0.001);
    }

    @Test
    void rejectsBlankAndNonNumericText() {
        assertThrows(InvalidDataException.class, () -> Parsers.integer(null, "Rok"));
        assertThrows(InvalidDataException.class, () -> Parsers.integer("  ", "Rok"));
        assertThrows(InvalidDataException.class, () -> Parsers.integer("dwa", "Rok"));
        assertThrows(InvalidDataException.class, () -> Parsers.decimal(null, "Cena"));
        assertThrows(InvalidDataException.class, () -> Parsers.decimal("", "Cena"));
        assertThrows(InvalidDataException.class, () -> Parsers.decimal("sto", "Cena"));
    }
}
