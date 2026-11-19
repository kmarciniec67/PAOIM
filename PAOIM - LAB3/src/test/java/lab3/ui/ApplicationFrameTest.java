package lab3.ui;

import lab3.data.DataGenerator;
import lab3.facade.StoreFacade;
import lab3.model.LaptopCondition;
import lab3.support.Edt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Aplikacja")
public class ApplicationFrameTest {
    @Test
    void loginSwitchesBetweenAdminAndClient() throws Exception {
        StoreFacade facade = new StoreFacade(DataGenerator.createSample());
        Edt.run(() -> {
            ApplicationFrame frame = new ApplicationFrame(facade);
            assertEquals("login", frame.visibleCard());
            assertNotNull(findButton(frame.adminPanel, "add branch"));
            assertNotNull(findButton(frame.adminPanel, "remove branch"));
            assertNotNull(findButton(frame.adminPanel, "add new laptop"));
            assertNotNull(findButton(frame.adminPanel, "remove laptop"));
            assertNotNull(findButton(frame.adminPanel, "sort branches by current load"));
            assertNull(findButton(frame.adminPanel, "reserve"));
            assertNotNull(findButton(frame.clientPanel, "reserve"));
            assertNotNull(findButton(frame.clientPanel, "request contact"));
            assertNull(findButton(frame.clientPanel, "add branch"));
            assertNull(findButton(frame.clientPanel, "remove laptop"));

            frame.loginPanel.username.setText("admin");
            frame.loginPanel.password.setText("admin");
            frame.loginPanel.loginButton.doClick();
            assertEquals("admin", frame.visibleCard());
            findButton(frame.adminPanel, "Wyloguj").doClick();
            assertEquals("login", frame.visibleCard());
            assertEquals("", new String(frame.loginPanel.password.getPassword()));

            frame.loginPanel.username.setText("klient");
            frame.loginPanel.password.setText("klient");
            frame.loginPanel.loginButton.doClick();
            assertEquals("client", frame.visibleCard());
            frame.clientPanel.catalog.laptops.selectRow(0);
            frame.clientPanel.reserveButton.doClick();
            assertEquals(LaptopCondition.RESERVED,
                    frame.clientPanel.catalog.getSelectedBranch().getLaptops().get(0).getCondition());
            findButton(frame.clientPanel, "Wyloguj").doClick();
            assertEquals("login", frame.visibleCard());
            frame.dispose();
        });
    }

    private static JButton findButton(Component root, String text) {
        if (root instanceof JButton) {
            JButton button = (JButton) root;
            if (text.equals(button.getText())) {
                return button;
            }
        }
        if (root instanceof Container) {
            Component[] children = ((Container) root).getComponents();
            for (Component child : children) {
                JButton found = findButton(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
