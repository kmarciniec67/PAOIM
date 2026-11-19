package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.exception.StoreException;
import lab3.facade.StoreFacade;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;

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
import java.util.ArrayList;

/** Panel pracownika: przyjęcia, usuwanie, edycja stanu i ceny, prośby o kontakt. */
public class AdminPanel extends JPanel {
    private final StoreFacade facade;
    final CatalogView catalog;
    final GenericTable<lab3.facade.ContactRequest> requests;
    final JButton sortButton;
    final JButton addBranchButton;
    final JButton removeBranchButton;
    final JButton addLaptopButton;
    final JButton removeLaptopButton;
    final JButton editConditionButton;
    final JButton editPriceButton;
    private final JLabel status = new JLabel(" ");

    public AdminPanel(StoreFacade facade, final Navigator navigator) throws InvalidDataException {
        this.facade = facade;
        catalog = new CatalogView(facade);
        requests = GenericTable.requests(new ArrayList<>());
        sortButton = button("sort branches by current load", "Sortuje oddziały malejąco po zapełnieniu.");
        addBranchButton = button("add branch", "Dodaj oddział do sieci.");
        removeBranchButton = button("remove branch", "Usuń zaznaczony oddział.");
        addLaptopButton = button("add new laptop", "Przyjmij laptop do zaznaczonego oddziału.");
        removeLaptopButton = button("remove laptop", "Usuń zaznaczony laptop z oddziału.");
        editConditionButton = button("edit condition", "Zmień stan zaznaczonego laptopa.");
        editPriceButton = button("edit price", "Zmień cenę zaznaczonego laptopa.");

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(header("Panel administratora", navigator), Layouts.cell(0, 0, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        add(split(), Layouts.cell(0, 1, 1, 1, 1, GridBagConstraints.BOTH));
        add(filters(), Layouts.cell(0, 2, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        add(requestsPanel(), Layouts.cell(0, 3, 1, 1, 0.45, GridBagConstraints.BOTH));
        add(status, Layouts.cell(0, 4, 1, 1, 0, GridBagConstraints.HORIZONTAL));

        addBranchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String[] values = InputDialogs.branch(AdminPanel.this);
                if (values == null) {
                    return;
                }
                try {
                    addBranch(values[0], Parsers.integer(values[1], "Pojemność"));
                } catch (InvalidDataException ex) {
                    InputDialogs.error(AdminPanel.this, ex.getMessage());
                }
            }
        });
        removeBranchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Branch branch = catalog.getSelectedBranch();
                if (branch == null) {
                    InputDialogs.error(AdminPanel.this, "Wybierz oddział.");
                    return;
                }
                if (InputDialogs.confirm(AdminPanel.this, "Usunąć oddział \"" + branch.getBranchName() + "\"?")) {
                    removeSelectedBranch();
                }
            }
        });
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sortBranches();
            }
        });
        addLaptopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (catalog.getSelectedBranch() == null) {
                    InputDialogs.error(AdminPanel.this, "Wybierz oddział.");
                    return;
                }
                Laptop laptop = InputDialogs.laptop(AdminPanel.this);
                if (laptop != null) {
                    addLaptop(laptop);
                }
            }
        });
        removeLaptopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (catalog.getSelectedLaptop() == null) {
                    InputDialogs.error(AdminPanel.this, "Wybierz laptop.");
                    return;
                }
                if (InputDialogs.confirm(AdminPanel.this, "Usunąć zaznaczony laptop?")) {
                    removeSelectedLaptop();
                }
            }
        });
        editConditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Laptop laptop = catalog.getSelectedLaptop();
                if (laptop == null) {
                    InputDialogs.error(AdminPanel.this, "Wybierz laptop.");
                    return;
                }
                LaptopCondition condition = InputDialogs.condition(AdminPanel.this, laptop.getCondition());
                if (condition != null) {
                    changeSelectedCondition(condition);
                }
            }
        });
        editPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Laptop laptop = catalog.getSelectedLaptop();
                if (laptop == null) {
                    InputDialogs.error(AdminPanel.this, "Wybierz laptop.");
                    return;
                }
                String value = InputDialogs.price(AdminPanel.this, laptop.getPrice());
                if (value == null) {
                    return;
                }
                try {
                    changeSelectedPrice(Parsers.decimal(value, "Cena"));
                } catch (InvalidDataException ex) {
                    InputDialogs.error(AdminPanel.this, ex.getMessage());
                }
            }
        });
        refreshRequests();
    }

    void refreshAll() {
        catalog.reloadBranchesKeepingSelection();
        refreshRequests();
    }

    void addBranch(String name, int capacity) {
        try {
            Branch created = facade.addBranch(name, capacity);
            catalog.reloadBranchesKeepingSelection();
            catalog.selectBranch(created.getBranchName());
            status.setText("Dodano oddział " + created.getBranchName() + ".");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void removeSelectedBranch() {
        Branch branch = catalog.getSelectedBranch();
        if (branch == null) {
            InputDialogs.error(this, "Wybierz oddział.");
            return;
        }
        try {
            facade.removeBranch(branch);
            catalog.reloadBranchesKeepingSelection();
            status.setText("Usunięto oddział " + branch.getBranchName() + ".");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void addLaptop(Laptop laptop) {
        Branch branch = catalog.getSelectedBranch();
        if (branch == null) {
            InputDialogs.error(this, "Wybierz oddział.");
            return;
        }
        try {
            facade.addLaptop(branch, laptop);
            catalog.reloadBranchesKeepingSelection();
            status.setText("Dodano laptop " + laptop.getBrand() + " " + laptop.getModel() + ".");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void removeSelectedLaptop() {
        Branch branch = catalog.getSelectedBranch();
        Laptop laptop = catalog.getSelectedLaptop();
        if (branch == null || laptop == null) {
            InputDialogs.error(this, "Wybierz laptop.");
            return;
        }
        try {
            facade.removeLaptop(branch, laptop);
            catalog.reloadBranchesKeepingSelection();
            status.setText("Usunięto laptop " + laptop.getBrand() + " " + laptop.getModel() + ".");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void changeSelectedCondition(LaptopCondition condition) {
        Branch branch = catalog.getSelectedBranch();
        Laptop laptop = catalog.getSelectedLaptop();
        if (branch == null || laptop == null) {
            InputDialogs.error(this, "Wybierz laptop.");
            return;
        }
        try {
            facade.changeCondition(branch, laptop, condition);
            catalog.reloadBranchesKeepingSelection();
            status.setText("Zmieniono stan na " + condition + ".");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void changeSelectedPrice(double price) {
        Branch branch = catalog.getSelectedBranch();
        Laptop laptop = catalog.getSelectedLaptop();
        if (branch == null || laptop == null) {
            InputDialogs.error(this, "Wybierz laptop.");
            return;
        }
        try {
            facade.changePrice(branch, laptop, price);
            catalog.reloadBranchesKeepingSelection();
            status.setText("Zmieniono cenę.");
        } catch (StoreException ex) {
            InputDialogs.error(this, ex.getMessage());
        }
    }

    void sortBranches() {
        facade.sortBranchesByCurrentLoad();
        catalog.reloadBranchesKeepingSelection();
        status.setText("Posortowano oddziały według aktualnego zapełnienia.");
    }

    private void refreshRequests() {
        requests.setRows(new ArrayList<>(facade.getContactRequests()));
    }

    private JPanel header(String title, final Navigator navigator) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
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
        JPanel left = column("Oddziały", catalog.branches, addBranchButton, removeBranchButton, sortButton);
        JPanel right = column("Laptopy", catalog.laptops, addLaptopButton, removeLaptopButton,
                editConditionButton, editPriceButton);
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

    private JPanel requestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Prośby o kontakt"));
        panel.add(requests, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel column(String title, GenericTable<?> table, JButton... buttons) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(table, BorderLayout.CENTER);
        JPanel bar = new JPanel(new GridLayout(buttons.length, 1, 4, 4));
        for (JButton button : buttons) {
            bar.add(button);
        }
        panel.add(bar, BorderLayout.SOUTH);
        return panel;
    }

    private static JButton button(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
}
