package lab3.ui;

import lab3.auth.AuthService;
import lab3.exception.InvalidDataException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Moduł logowania. Administrator i klient dostają inne panele. */
public class LoginPanel extends JPanel {
    final JTextField username = new JTextField(16);
    final JPasswordField password = new JPasswordField(16);
    final JButton loginButton = new JButton("Zaloguj");

    public LoginPanel(final Navigator navigator) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Sieć sklepów z laptopami");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel hint = new JLabel("Administrator: admin / admin     Klient: klient / klient");

        add(title, Layouts.cell(0, 0, 2, 1, 0, GridBagConstraints.NONE));
        add(new JLabel("Login"), Layouts.cell(0, 1, 1, 0, 0, GridBagConstraints.NONE));
        add(username, Layouts.cell(1, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL));
        add(new JLabel("Hasło"), Layouts.cell(0, 2, 1, 0, 0, GridBagConstraints.NONE));
        add(password, Layouts.cell(1, 2, 1, 0, 0, GridBagConstraints.HORIZONTAL));
        add(loginButton, Layouts.cell(1, 3, 1, 0, 0, GridBagConstraints.NONE));
        add(hint, Layouts.cell(0, 4, 2, 0, 0, GridBagConstraints.NONE));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                login(navigator);
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (SwingUtilities.getRootPane(this) != null) {
            SwingUtilities.getRootPane(this).setDefaultButton(loginButton);
        }
    }

    void clearPassword() {
        password.setText("");
    }

    void login(Navigator navigator) {
        try {
            AuthService.Role role = AuthService.authenticate(username.getText(), new String(password.getPassword()));
            password.setText("");
            if (role == AuthService.Role.ADMIN) {
                navigator.showAdmin();
            } else {
                navigator.showClient();
            }
        } catch (InvalidDataException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }
}
