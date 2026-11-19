package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.facade.StoreFacade;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/** Trzy moduły: logowanie, panel administratora i panel klienta. */
public class ApplicationFrame extends JFrame implements Navigator {
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    final LoginPanel loginPanel;
    final AdminPanel adminPanel;
    final ClientPanel clientPanel;
    private String visibleCard = "login";

    public ApplicationFrame(StoreFacade facade) throws InvalidDataException {
        super("PAOIM Lab 3 — sieć sklepów z laptopami");
        loginPanel = new LoginPanel(this);
        adminPanel = new AdminPanel(facade, this);
        clientPanel = new ClientPanel(facade, this);
        root.add(loginPanel, "login");
        root.add(adminPanel, "admin");
        root.add(clientPanel, "client");
        setContentPane(root);
        cards.show(root, "login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        pack();
        setSize(1240, 800);
        setLocationRelativeTo(null);
    }

    @Override
    public void showAdmin() {
        visibleCard = "admin";
        adminPanel.refreshAll();
        cards.show(root, "admin");
    }

    @Override
    public void showClient() {
        visibleCard = "client";
        clientPanel.refreshAll();
        cards.show(root, "client");
    }

    @Override
    public void showLogin() {
        visibleCard = "login";
        loginPanel.clearPassword();
        cards.show(root, "login");
    }

    String visibleCard() {
        return visibleCard;
    }
}
