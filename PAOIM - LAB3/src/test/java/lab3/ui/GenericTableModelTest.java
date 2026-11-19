package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GenericTable")
public class GenericTableModelTest {
    @Test
    void readsBranchAndLaptopProperties() throws Exception {
        Branch branch = new Branch("Gdańsk", 2);
        Laptop laptop = new Laptop("XPS 13", "Dell", LaptopCondition.WORKING, 2020, 2199,
                16, "i7", 80, "SN-1");
        branch.addLaptop(laptop);
        GenericTableModel<Branch> branches = new GenericTableModel<>(
                Branch.class,
                List.of(branch),
                new String[]{"Nazwa", "Zapełnienie"},
                new String[]{"branchName", "fillLabel"});
        assertEquals(2, branches.getColumnCount());
        assertEquals(1, branches.getRowCount());
        assertEquals("Nazwa", branches.getColumnName(0));
        assertEquals("Gdańsk", branches.getValueAt(0, 0));
        assertEquals("50.0%", branches.getValueAt(0, 1));
        assertFalse(branches.isCellEditable(0, 0));
        assertEquals(branch, branches.getRow(0));

        GenericTableModel<Laptop> laptops = new GenericTableModel<>(
                Laptop.class,
                new ArrayList<>(List.of(laptop)),
                new String[]{"Model", "Wiek", "Cena"},
                new String[]{"model", "age", "priceLabel"});
        assertEquals("XPS 13", laptops.getValueAt(0, 0));
        assertEquals(laptop.age(), laptops.getValueAt(0, 1));
        assertEquals("2199.00 zł", laptops.getValueAt(0, 2));
        laptops.setRows(new ArrayList<>());
        assertEquals(0, laptops.getRowCount());
        laptops.setRows(null);
        assertEquals(0, laptops.getRowCount());
        laptops.refresh();
    }

    @Test
    void rejectsBrokenColumnDefinitions() {
        assertThrows(InvalidDataException.class,
                () -> new GenericTableModel<>(Branch.class, List.of(), new String[]{"X"}, new String[]{"brak"}));
        assertThrows(InvalidDataException.class,
                () -> new GenericTableModel<>(Branch.class, List.of(), new String[]{"X"}, new String[]{""}));
        assertThrows(InvalidDataException.class,
                () -> new GenericTableModel<Branch>(null, List.of(), new String[]{"X"}, new String[]{"branchName"}));
        assertThrows(InvalidDataException.class,
                () -> new GenericTableModel<>(Branch.class, List.of(), new String[]{"A", "B"}, new String[]{"branchName"}));
    }

    @Test
    void componentSelectsARow() throws Exception {
        Branch first = new Branch("A", 1);
        Branch second = new Branch("B", 1);
        AtomicReference<Branch> selected = new AtomicReference<>();
        GenericTable<Branch> table = GenericTable.branches(new ArrayList<>(List.of(first, second)));
        table.onSelection(selected::set);
        assertNull(table.getSelected());
        table.selectRow(1);
        assertEquals(second, table.getSelected());
        assertEquals(second, selected.get());
        table.selectRow(-1);
        assertNull(table.getSelected());
        table.setRows(List.of(first));
        assertEquals(1, table.getRowCount());
        assertEquals("A", table.getModel().getValueAt(0, 0));
        GenericTable<Laptop> laptops = GenericTable.laptops(new ArrayList<>());
        assertEquals(0, laptops.getRowCount());
        assertEquals(10, laptops.getModel().getColumnCount());
    }
}
