package lab3.ui;

import lab3.exception.InvalidDataException;

final class Parsers {
    private Parsers() {
    }

    static int integer(String text, String field) throws InvalidDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidDataException(field + " musi być liczbą całkowitą.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidDataException(field + " musi być liczbą całkowitą.");
        }
    }

    static double decimal(String text, String field) throws InvalidDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidDataException(field + " musi być liczbą.");
        }
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new InvalidDataException(field + " musi być liczbą.");
        }
    }
}
