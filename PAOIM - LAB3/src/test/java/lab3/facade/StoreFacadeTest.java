package lab3.facade;

import lab3.data.DataGenerator;
import lab3.exception.BranchNotFoundException;
import lab3.exception.DuplicateBranchException;
import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.exception.LaptopNotFoundException;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;
import lab3.model.StoreManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StoreFacade")
public class StoreFacadeTest {
    private StoreFacade facade;
    private Branch warszawa;

    @BeforeEach
    void setUp() throws Exception {
        facade = new StoreFacade(DataGenerator.createSample());
        warszawa = facade.getBranches().get(0);
    }

    @Test
    void rejectsMissingManager() {
        assertThrows(InvalidDataException.class, () -> new StoreFacade(null));
    }

    @Test
    void addAndRemoveBranch() throws Exception {
        int before = facade.getBranches().size();
        Branch created = facade.addBranch("  Szczecin  ", 2);
        assertEquals("Szczecin", created.getBranchName());
        assertEquals(before + 1, facade.getBranches().size());
        assertThrows(DuplicateBranchException.class, () -> facade.addBranch("Szczecin", 2));
        facade.removeBranch(created);
        assertEquals(before, facade.getBranches().size());
        assertThrows(BranchNotFoundException.class, () -> facade.removeBranch(created));
        assertThrows(InvalidDataException.class, () -> facade.removeBranch(null));
        Branch outsider = new Branch("Obcy", 1);
        assertThrows(BranchNotFoundException.class, () -> facade.removeBranch(outsider));
    }

    @Test
    void addAndRemoveLaptop() throws Exception {
        Branch wroclaw = branch("Wrocław Rynek");
        assertEquals(0, wroclaw.size());
        Laptop laptop = new Laptop("TestBook", "Acer", LaptopCondition.WORKING, 2021, 999);
        assertThrows(InvalidDataException.class, () -> facade.addLaptop(wroclaw, null));
        assertThrows(InvalidDataException.class, () -> facade.addLaptop(null, laptop));
        facade.addLaptop(wroclaw, laptop);
        assertEquals(1, wroclaw.size());
        facade.removeLaptop(wroclaw, laptop);
        assertEquals(0, wroclaw.size());
        assertThrows(LaptopNotFoundException.class, () -> facade.removeLaptop(wroclaw, laptop));
    }

    @Test
    void sortKeepsTheSameListAndOrdersByFill() {
        List<Branch> branches = facade.getBranches();
        facade.sortBranchesByCurrentLoad();
        assertSame(branches, facade.getBranches());
        assertEquals("Kraków Kazimierz", branches.get(0).getBranchName());
        assertEquals("Warszawa Centrum", branches.get(1).getBranchName());
        assertEquals("Gdańsk Wrzeszcz", branches.get(2).getBranchName());
        assertEquals("Poznań Rynek", branches.get(3).getBranchName());
        assertEquals("Wrocław Rynek", branches.get(4).getBranchName());
        assertTrue(branches.get(0).fillRatio() >= branches.get(1).fillRatio());
    }

    @Test
    void filterByTextAndCondition() throws Exception {
        assertTrue(facade.filterLaptops(null, "Dell", null).isEmpty());
        assertEquals(warszawa.size(), facade.filterLaptops(warszawa, null, null).size());
        assertEquals(warszawa.size(), facade.filterLaptops(warszawa, "   ", null).size());

        List<Laptop> dell = facade.filterLaptops(warszawa, "  dElL ", null);
        assertEquals(1, dell.size());
        assertEquals("Dell", dell.get(0).getBrand());
        assertSame(warszawa.getLaptops().get(1), dell.get(0));

        List<Laptop> think = facade.filterLaptops(warszawa, "thinkpad", null);
        assertEquals(1, think.size());
        assertTrue(think.get(0).getModel().contains("ThinkPad"));

        List<Laptop> damaged = facade.filterLaptops(warszawa, null, LaptopCondition.DAMAGED);
        assertEquals(1, damaged.size());
        assertEquals(LaptopCondition.DAMAGED, damaged.get(0).getCondition());

        assertTrue(facade.filterLaptops(warszawa, "Dell", LaptopCondition.DAMAGED).isEmpty());
        assertEquals(1, facade.filterLaptops(warszawa, "HP", LaptopCondition.DAMAGED).size());
        assertTrue(facade.filterLaptops(warszawa, "nie-ma", null).isEmpty());
    }

    @Test
    void reserveOnlyWorkingLaptop() throws Exception {
        Laptop working = warszawa.getLaptops().get(0);
        assertEquals(LaptopCondition.WORKING, working.getCondition());
        facade.reserveLaptop(warszawa, working);
        assertEquals(LaptopCondition.RESERVED, working.getCondition());
        assertThrows(InvalidDataException.class, () -> facade.reserveLaptop(warszawa, working));

        Laptop damaged = facade.filterLaptops(warszawa, null, LaptopCondition.DAMAGED).get(0);
        assertThrows(InvalidDataException.class, () -> facade.reserveLaptop(warszawa, damaged));
        assertThrows(InvalidDataException.class, () -> facade.reserveLaptop(warszawa, null));
        assertThrows(InvalidDataException.class, () -> facade.reserveLaptop(null, working));
        Laptop missing = new Laptop("Brak", "HP", LaptopCondition.WORKING, 2020, 10);
        assertThrows(LaptopNotFoundException.class, () -> facade.reserveLaptop(warszawa, missing));
    }

    @Test
    void changeConditionAndPrice() throws Exception {
        Laptop laptop = warszawa.getLaptops().get(0);
        facade.changeCondition(warszawa, laptop, LaptopCondition.IN_TESTING);
        assertEquals(LaptopCondition.IN_TESTING, laptop.getCondition());
        facade.changePrice(warszawa, laptop, 3210);
        assertEquals(3210, laptop.getPrice(), 0.001);
        assertThrows(InvalidPriceException.class, () -> facade.changePrice(warszawa, laptop, -1));
        assertEquals(3210, laptop.getPrice(), 0.001);
        assertThrows(LaptopNotFoundException.class,
                () -> facade.changePrice(warszawa, new Laptop("Brak", "HP", LaptopCondition.WORKING, 2020, 10), 5));
    }

    @Test
    void requestContactStoresSnapshotForAdmin() throws Exception {
        Laptop laptop = warszawa.getLaptops().get(0);
        assertTrue(facade.getContactRequests().isEmpty());
        facade.requestContact(warszawa, laptop, "  Anna Nowak ", "  Proszę o telefon ");
        assertEquals(1, facade.getContactRequests().size());
        ContactRequest request = facade.getContactRequests().get(0);
        assertEquals("Anna Nowak", request.getClientName());
        assertEquals("Warszawa Centrum", request.getBranchName());
        assertTrue(request.getLaptopLabel().contains("ThinkPad T480"));
        assertTrue(request.getLaptopLabel().contains(laptop.getSerialNumber()));
        assertEquals("Proszę o telefon", request.getMessage());
        assertThrows(InvalidDataException.class, () -> facade.requestContact(warszawa, laptop, " ", "hej"));
        assertThrows(InvalidDataException.class, () -> facade.requestContact(warszawa, laptop, "Anna", " "));
        assertThrows(InvalidDataException.class, () -> facade.requestContact(warszawa, null, "Anna", "hej"));
        assertThrows(InvalidDataException.class, () -> facade.requestContact(null, laptop, "Anna", "hej"));
        assertThrows(UnsupportedOperationException.class, () -> facade.getContactRequests().add(request));
    }

    private Branch branch(String name) {
        for (Branch candidate : facade.getBranches()) {
            if (candidate.getBranchName().equals(name)) {
                return candidate;
            }
        }
        throw new AssertionError(name);
    }
}
