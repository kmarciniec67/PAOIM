package lab3.model;

import lab3.exception.BranchFullException;
import lab3.exception.BranchNotFoundException;
import lab3.exception.DuplicateBranchException;
import lab3.exception.DuplicateLaptopException;
import lab3.exception.InvalidDataException;
import lab3.exception.LaptopNotFoundException;
import lab3.support.OutputCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StoreManager")
public class StoreManagerTest {
    private StoreManager manager;

    @BeforeEach
    void setUp() {
        manager = new StoreManager();
    }

    @Test
    void addBranchTrimsAndRejectsBlankDuplicateAndBadCapacity() throws Exception {
        Branch branch = manager.addBranch("  Gdańsk  ", 2);
        assertEquals("Gdańsk", branch.getBranchName());
        assertSame(branch, manager.getBranch("Gdańsk"));
        assertSame(branch, manager.getBranch("  Gdańsk "));
        assertEquals(1, manager.getBranches().size());
        assertThrows(DuplicateBranchException.class, () -> manager.addBranch("Gdańsk", 9));
        assertEquals(1, manager.getBranches().size());
        assertThrows(InvalidDataException.class, () -> manager.addBranch("   ", 2));
        assertThrows(InvalidDataException.class, () -> manager.addBranch(null, 2));
        assertThrows(InvalidDataException.class, () -> manager.addBranch("Łódź", 0));
        assertNull(manager.getBranch("Łódź"));
        assertNull(manager.getBranch(null));
    }

    @Test
    void removeBranchAndFindEmpty() throws Exception {
        manager.addBranch("Pełny", 1);
        Branch empty = manager.addBranch("Pusty", 2);
        manager.getBranch("Pełny").addLaptop(laptop("A", 10));
        assertEquals(1, manager.findEmpty().size());
        assertSame(empty, manager.findEmpty().get(0));

        assertSame(empty, manager.removeBranch("  Pusty "));
        assertNull(manager.getBranch("Pusty"));
        assertTrue(manager.findEmpty().isEmpty());
        assertThrows(BranchNotFoundException.class, () -> manager.removeBranch("Pusty"));
        assertThrows(InvalidDataException.class, () -> manager.removeBranch(null));
        assertThrows(InvalidDataException.class, () -> manager.removeBranch("  "));
    }

    @Test
    void summaryAndTotalValue() throws Exception {
        String empty = OutputCapture.stdout(manager::summary);
        assertTrue(empty.contains("Brak oddziałów."));
        Branch first = manager.addBranch("A", 2);
        manager.addBranch("B", 2);
        first.addLaptop(laptop("A", 40));
        first.addLaptop(laptop("B", 60));
        assertEquals(100, manager.totalValue(), 0.001);
        String summary = OutputCapture.stdout(manager::summary);
        assertTrue(summary.contains("A"));
        assertTrue(summary.contains("100"));
        assertTrue(summary.contains("B"));
    }

    @Test
    void transferMovesTheSameInstanceAndKeepsSourceOnFailure() throws Exception {
        Branch source = manager.addBranch("Źródło", 2);
        Branch destination = manager.addBranch("Cel", 1);
        Laptop moving = laptop("A", 10);
        Laptop staying = laptop("B", 20);
        source.addLaptop(moving);
        source.addLaptop(staying);
        destination.addLaptop(laptop("C", 30));

        assertThrows(BranchFullException.class, () -> manager.transferLaptop(moving, "Źródło", "Cel"));
        assertTrue(source.contains(moving));
        destination.removeLaptop(destination.getLaptops().get(0));

        manager.transferLaptop(moving, "Źródło", "Cel");
        assertSame(moving, destination.getLaptops().get(0));
        assertEquals(1, source.size());

        source.addLaptop(new Laptop("A", "Lenovo", LaptopCondition.DAMAGED, 2019, 1));
        assertThrows(DuplicateLaptopException.class, () -> manager.transferLaptop(moving, "Cel", "Źródło"));
        assertTrue(destination.contains(moving));
        assertThrows(LaptopNotFoundException.class, () -> manager.transferLaptop(staying, "Cel", "Źródło"));
        assertThrows(InvalidDataException.class, () -> manager.transferLaptop(null, "Źródło", "Cel"));
        assertThrows(InvalidDataException.class, () -> manager.transferLaptop(staying, "Źródło", "Źródło"));
        assertThrows(BranchNotFoundException.class, () -> manager.transferLaptop(staying, "Brak", "Cel"));
        assertThrows(BranchNotFoundException.class, () -> manager.transferLaptop(staying, "Źródło", "Brak"));
        assertThrows(InvalidDataException.class, () -> manager.transferLaptop(staying, " ", "Cel"));
        assertThrows(InvalidDataException.class, () -> manager.transferLaptop(staying, "Źródło", null));
    }

    @Test
    void sortByCurrentLoadUsesTheSameList() throws Exception {
        List<Branch> view = manager.getBranches();
        Branch low = manager.addBranch("B-niski", 4);
        Branch high = manager.addBranch("A-wysoki", 2);
        Branch emptyA = manager.addBranch("A-pusty", 3);
        manager.addBranch("B-pusty", 3);
        low.addLaptop(laptop("A", 10));
        high.addLaptop(laptop("B", 10));
        high.addLaptop(laptop("C", 10));

        manager.sortByCurrentLoad();
        assertSame(view, manager.getBranches());
        assertEquals("A-wysoki", view.get(0).getBranchName());
        assertEquals("B-niski", view.get(1).getBranchName());
        assertEquals("A-pusty", view.get(2).getBranchName());
        assertEquals("B-pusty", view.get(3).getBranchName());
        assertTrue(view.get(0).fillRatio() >= view.get(1).fillRatio());
    }

    private static Laptop laptop(String model, double price) throws Exception {
        return new Laptop(model, "Lenovo", LaptopCondition.WORKING, 2019, price);
    }
}
