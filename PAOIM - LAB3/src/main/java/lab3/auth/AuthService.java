package lab3.auth;

import lab3.exception.InvalidDataException;

/** Rozróżnia klienta sklepu i pracownika. Dane logowania są stałe, bo to laboratorium. */
public final class AuthService {
    public enum Role {
        ADMIN,
        CLIENT
    }

    private AuthService() {
    }

    public static Role authenticate(String username, String password) throws InvalidDataException {
        if (username == null || password == null) {
            throw new InvalidDataException("Podaj login i hasło.");
        }
        String user = username.trim();
        if (user.equals("admin") && password.equals("admin")) {
            return Role.ADMIN;
        }
        if (user.equals("klient") && password.equals("klient")) {
            return Role.CLIENT;
        }
        throw new InvalidDataException("Nieprawidłowy login lub hasło.");
    }
}
