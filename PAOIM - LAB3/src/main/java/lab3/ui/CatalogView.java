package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.facade.StoreFacade;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Wspólny widok dwóch tabel: oddziały i laptopy wybranego oddziału,
 * razem z polem filter i listą condition.
 */
final class CatalogView {
    final GenericTable<Branch> branches;
    final GenericTable<Laptop> laptops;
    final JTextField filterField;
    final JComboBox<Object> conditionBox;
    private final StoreFacade facade;
    private Branch selected;

    CatalogView(StoreFacade facade) throws InvalidDataException {
        this.facade = facade;
        branches = GenericTable.branches(facade.getBranches());
        laptops = GenericTable.laptops(new ArrayList<Laptop>());
        filterField = new JTextField(18);
        filterField.setToolTipText("Nazwa modelu lub marki. Enter filtruje laptopy wybranego oddziału.");
        conditionBox = new JComboBox<>();
        conditionBox.addItem("Wszystkie");
        for (LaptopCondition condition : LaptopCondition.values()) {
            conditionBox.addItem(condition);
        }
        conditionBox.setToolTipText("Zostaw tylko laptopy w wybranym stanie.");
        branches.onSelection(new java.util.function.Consumer<Branch>() {
            @Override
            public void accept(Branch branch) {
                selected = branch;
                applyFilter();
            }
        });
        filterField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                applyFilter();
            }
        });
        conditionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                applyFilter();
            }
        });
        selectFirstBranch();
    }

    Branch getSelectedBranch() {
        return selected;
    }

    Laptop getSelectedLaptop() {
        return laptops.getSelected();
    }

    void selectFirstBranch() {
        if (branches.getRowCount() > 0) {
            branches.selectRow(0);
        }
    }

    void selectBranch(String name) {
        for (int i = 0; i < branches.getRowCount(); i++) {
            if (name.equals(branches.getRow(i).getBranchName())) {
                selected = branches.getRow(i);
                branches.selectRow(i);
                applyFilter();
                return;
            }
        }
    }

    void reloadBranchesKeepingSelection() {
        String preferredName = selected == null ? null : selected.getBranchName();
        branches.refresh();
        if (branches.getRowCount() == 0) {
            selected = null;
            applyFilter();
            return;
        }
        int index = 0;
        if (preferredName != null) {
            for (int i = 0; i < branches.getRowCount(); i++) {
                if (preferredName.equals(branches.getRow(i).getBranchName())) {
                    index = i;
                    break;
                }
            }
        }
        branches.selectRow(index);
    }

    void applyFilter() {
        laptops.setRows(facade.filterLaptops(selected, filterField.getText(), selectedCondition()));
    }

    private LaptopCondition selectedCondition() {
        Object item = conditionBox.getSelectedItem();
        if (item instanceof LaptopCondition) {
            return (LaptopCondition) item;
        }
        return null;
    }
}
