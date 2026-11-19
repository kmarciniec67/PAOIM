package lab3.ui;

import lab3.data.DataGenerator;
import lab3.facade.StoreFacade;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;
import lab3.support.Edt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Panel administratora")
public class AdminPanelTest {
    private StoreFacade facade;
    private AdminPanel panel;

    @BeforeEach
    void setUp() throws Exception {
        facade = new StoreFacade(DataGenerator.createSample());
        Edt.run(() -> panel = new AdminPanel(facade, idle()));
    }

    @Test
    void showsLaptopsOfTheSelectedBranchAndSortsByLoad() throws Exception {
        Edt.run(() -> {
            assertEquals("Warszawa Centrum", panel.catalog.getSelectedBranch().getBranchName());
            assertEquals(5, panel.catalog.laptops.getRowCount());
            panel.sortButton.doClick();
            assertEquals("Kraków Kazimierz", facade.getBranches().get(0).getBranchName());
            assertEquals("Kraków Kazimierz", panel.catalog.branches.getRow(0).getBranchName());
            assertEquals("Warszawa Centrum", panel.catalog.getSelectedBranch().getBranchName());
        });
    }

    @Test
    void filterAndConditionLimitTheLaptopTable() throws Exception {
        Edt.run(() -> {
            panel.catalog.filterField.setText("Dell");
            panel.catalog.filterField.postActionEvent();
            assertEquals(1, panel.catalog.laptops.getRowCount());
            assertEquals("Dell", panel.catalog.laptops.getRow(0).getBrand());

            panel.catalog.filterField.setText("");
            panel.catalog.conditionBox.setSelectedItem(LaptopCondition.DAMAGED);
            assertEquals(1, panel.catalog.laptops.getRowCount());
            assertEquals(LaptopCondition.DAMAGED, panel.catalog.laptops.getRow(0).getCondition());
        });
    }

    @Test
    void addsAndRemovesBranchAndLaptopAndEditsPriceAndCondition() throws Exception {
        Edt.run(() -> {
            int branches = facade.getBranches().size();
            panel.addBranch("Szczecin", 2);
            assertEquals(branches + 1, facade.getBranches().size());
            assertEquals("Szczecin", panel.catalog.getSelectedBranch().getBranchName());
            panel.removeSelectedBranch();
            assertEquals(branches, facade.getBranches().size());

            panel.catalog.selectBranch("Wrocław Rynek");
            Laptop laptop = new Laptop("TestBook", "Acer", LaptopCondition.WORKING, 2021, 999);
            panel.addLaptop(laptop);
            assertEquals(1, panel.catalog.getSelectedBranch().size());
            panel.catalog.laptops.selectRow(0);
            assertNotNull(panel.catalog.getSelectedLaptop());
            panel.removeSelectedLaptop();
            assertEquals(0, panel.catalog.getSelectedBranch().size());

            panel.catalog.selectBranch("Warszawa Centrum");
            panel.catalog.laptops.selectRow(0);
            Laptop warsawLaptop = panel.catalog.getSelectedLaptop();
            panel.changeSelectedPrice(1300);
            assertEquals(1300, warsawLaptop.getPrice(), 0.001);
            panel.catalog.laptops.selectRow(0);
            panel.changeSelectedCondition(LaptopCondition.IN_REPAIR);
            assertEquals(LaptopCondition.IN_REPAIR, warsawLaptop.getCondition());
        });
    }

    private static Navigator idle() {
        return new Navigator() {
            @Override
            public void showAdmin() {
            }

            @Override
            public void showClient() {
            }

            @Override
            public void showLogin() {
            }
        };
    }
}
