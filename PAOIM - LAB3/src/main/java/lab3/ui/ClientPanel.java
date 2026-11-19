package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.exception.StoreException;
import lab3.facade.StoreFacade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Panel klienta: przeglądanie, filtr, sortowanie zapełnienia, rezerwacja i prośba o kontakt. */
public class ClientPanel extends JPanel {
    private final StoreFacade facade;
    final CatalogView catalog;
    final JButton sortButton;
    final JButton reserveButton;
    final JButton contactButton;
    private final JLabel status = new JLabel(" ");

    public ClientPanel(StoreFacade facade, final Navigator navigator) throws InvalidDataException {
        this.facade = facade;
        catalog = new CatalogView(facade);
        sortButton = button("sort branches by current load", "Sortuje oddziały malejąco po zapełnieniu.");
        reserveButton = button("reserve", "Rezerwuje zaznaczony sprawny laptop.");
        contactButton = button("request contact", "Zostawia prośbę o kontakt widoczną dla administratora.");

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(header(navigator), Layouts.cell(0, 0, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        add(split(), Layouts.cell(0, 1, 1, 1, 1, GridBagConstraints.BOTH));
        add(filters(), Layouts.cell(0, 2, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        add(status, Layouts.cell(0, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL));

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sortBranches();
            }
        });
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                reserveSelected();
            }
        });
        contactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (catalog.getSelectedLaptop() == null) {
                    InputDialogs.error(ClientPanel.this, "Wybierz laptop.");
                    return;
                }
                String[] values = InputDialogs.contact(ClientPanel.this);
                if (values != null) {
                    requestContact(values[0], values[1]);
                }
            }
        });
    }

    void refreshAll() {
        catalog.reloadBranchesKeepingSelection();
    }

    void sortBranches() {
        facade.sortBranchesByCurrentLoad();
        catalog.reloadBranchesKeepingSelection();
        status.setText("Posortowano oddziały według aktualnego zapełnienia.");
    }

    void reserveSelected() {
        if (catalog.getSelectedBranch() == null || catalog.getSelectedLaptop() == null) {
            InputDialogs.error(this, "Wybierz laptop do rezerwacji.");
            return;
        }
        try {
            facade.reserveLaptop(catalog.getSelectedBranch(), catalog.getSelectedLaptop());
            catalog.reloadBranchesKeepingSelection();
            status.setText("Zarezerwowano laptop.");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void requestContact(String clientName, String message) {
        try {
            facade.requestContact(catalog.getSelectedBranch(), catalog.getSelectedLaptop(), clientName, message);
            status.setText("Zapisano prośbę o kontakt.");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    private JPanel header(final Navigator navigator) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Panel klienta");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        JButton logout = new JButton("Wyloguj");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                navigator.showLogin();
            }
        });
        panel.add(label, BorderLayout.WEST);
        panel.add(logout, BorderLayout.EAST);
        return panel;
    }

    private JSplitPane split() {
        JPanel left = new JPanel(new BorderLayout(4, 4));
        left.setBorder(BorderFactory.createTitledBorder("Oddziały"));
        left.add(catalog.branches, BorderLayout.CENTER);
        JPanel leftButtons = new JPanel(new GridLayout(1, 1, 4, 4));
        leftButtons.add(sortButton);
        left.add(leftButtons, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(4, 4));
        right.setBorder(BorderFactory.createTitledBorder("Laptopy"));
        right.add(catalog.laptops, BorderLayout.CENTER);
        JPanel rightButtons = new JPanel(new GridLayout(1, 2, 4, 4));
        rightButtons.add(reserveButton);
        rightButtons.add(contactButton);
        right.add(rightButtons, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.38);
        return split;
    }

    private JPanel filters() {
        JPanel filters = new JPanel(new GridBagLayout());
        filters.add(new JLabel("filter"), Layouts.cell(0, 0, 1, 0, 0, GridBagConstraints.NONE));
        filters.add(catalog.filterField, Layouts.cell(1, 0, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        filters.add(new JLabel("condition"), Layouts.cell(2, 0, 1, 0, 0, GridBagConstraints.NONE));
        filters.add(catalog.conditionBox, Layouts.cell(3, 0, 1, 0, 0, GridBagConstraints.NONE));
        return filters;
    }

    private static JButton button(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
}
