package lab3.auth;

import lab3.exception.InvalidDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AuthService")
public class AuthServiceTest {
    @Test
    void acceptsAdminAndClient() throws InvalidDataException {
        assertEquals(AuthService.Role.ADMIN, AuthService.authenticate("admin", "admin"));
        assertEquals(AuthService.Role.ADMIN, AuthService.authenticate("  admin  ", "admin"));
        assertEquals(AuthService.Role.CLIENT, AuthService.authenticate("klient", "klient"));
    }

    @Test
    void rejectsMissingOrWrongCredentials() {
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate(null, "admin"));
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate("admin", null));
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate("admin", "zle"));
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate("nikt", "admin"));
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate("admin", "admin "));
        assertThrows(InvalidDataException.class, () -> AuthService.authenticate("", ""));
    }
}
