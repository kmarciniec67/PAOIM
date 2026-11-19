package lab3.model;

import lab3.exception.BranchFullException;
import lab3.exception.DuplicateLaptopException;
import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.exception.LaptopNotFoundException;
import lab3.support.OutputCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Branch")
public class BranchTest {
    private Branch branch;

    @BeforeEach
    void setUp() throws InvalidDataException {
        branch = new Branch("  Warszawa Centrum  ", 2);
    }

    @Test
    void constructorTrimsNameAndRejectsBlankOrNonPositiveCapacity() {
        assertEquals("Warszawa Centrum", branch.getBranchName());
        assertEquals(2, branch.getMaxCapacity());
        assertThrows(InvalidDataException.class, () -> new Branch("  ", 2));
        assertThrows(InvalidDataException.class, () -> new Branch(null, 2));
        assertThrows(InvalidDataException.class, () -> new Branch("A", 0));
        assertThrows(InvalidDataException.class, () -> new Branch("A", -3));
    }

    @Test
    void addLaptopStoresItemAndRejectsNullDuplicateAndOverflow() throws Exception {
        Laptop first = laptop("Alfa", 100);
        branch.addLaptop(first);
        assertEquals(1, branch.size());
        assertEquals(1, branch.getLaptopCount());
        assertTrue(branch.contains(first));
        assertFalse(branch.contains(null));
        assertFalse(branch.isEmpty());
        assertFalse(branch.isFull());
        assertEquals(0.5, branch.fillRatio(), 0.001);
        assertEquals("50.0%", branch.getFillLabel());
        assertSame(first, branch.getLaptops().get(0));

        assertThrows(InvalidDataException.class, () -> branch.addLaptop(null));
        Laptop duplicate = new Laptop("Alfa", "Lenovo", LaptopCondition.DAMAGED, 2019, 1);
        DuplicateLaptopException duplicateError = assertThrows(DuplicateLaptopException.class,
                () -> branch.addLaptop(duplicate));
        assertTrue(duplicateError.getMessage().contains("już istnieje"));
        assertEquals(1, branch.size());

        branch.addLaptop(laptop("Beta", 50));
        assertTrue(branch.isFull());
        assertEquals(1.0, branch.fillRatio(), 0.001);
        BranchFullException full = assertThrows(BranchFullException.class, () -> branch.addLaptop(laptop("Gamma", 10)));
        assertTrue(full.getMessage().contains("pełny"));
        assertEquals(2, branch.size());
    }

    @Test
    void removeAndSellReturnStoredInstanceOrReportMissing() throws Exception {
        Laptop stored = laptop("Alfa", 100);
        branch.addLaptop(stored);
        Laptop copy = new Laptop("Alfa", "Lenovo", LaptopCondition.DAMAGED, 2019, 5);
        assertSame(stored, branch.removeLaptop(copy));
        assertTrue(branch.isEmpty());
        LaptopNotFoundException missing = assertThrows(LaptopNotFoundException.class, () -> branch.removeLaptop(copy));
        assertTrue(missing.getMessage().contains("Brak laptopa"));
        assertThrows(InvalidDataException.class, () -> branch.removeLaptop(null));

        branch.addLaptop(stored);
        assertSame(stored, branch.sellLaptop(stored));
        assertThrows(LaptopNotFoundException.class, () -> branch.sellLaptop(stored));
        assertThrows(InvalidDataException.class, () -> branch.sellLaptop(null));
        assertEquals(0, branch.totalValue(), 0.001);
    }

    @Test
    void changeConditionPriceAndYear() throws Exception {
        Laptop first = laptop("Alfa", 100);
        Laptop second = new Laptop("Alfa", "Lenovo", LaptopCondition.WORKING, 2021, 80);
        branch.addLaptop(first);
        branch.addLaptop(second);

        branch.changeCondition(new Laptop("Alfa", "Lenovo", LaptopCondition.DAMAGED, 2019, 1), LaptopCondition.IN_REPAIR);
        assertEquals(LaptopCondition.IN_REPAIR, first.getCondition());
        assertThrows(InvalidDataException.class, () -> branch.changeCondition(first, null));
        assertThrows(LaptopNotFoundException.class,
                () -> branch.changeCondition(laptop("Nie ma", 1), LaptopCondition.WORKING));
        assertThrows(InvalidDataException.class, () -> branch.changeCondition(null, LaptopCondition.WORKING));

        branch.changePrice(first, 250);
        assertEquals(250, first.getPrice(), 0.001);
        assertThrows(InvalidPriceException.class, () -> branch.changePrice(first, -5));
        assertEquals(250, first.getPrice(), 0.001);
        assertThrows(LaptopNotFoundException.class, () -> branch.changePrice(laptop("Nie ma", 1), 10));
        assertThrows(InvalidDataException.class, () -> branch.changePrice(null, 10));

        branch.changeYear(first, 2019);
        assertEquals(2019, first.getYear());
        assertThrows(DuplicateLaptopException.class, () -> branch.changeYear(second, 2019));
        assertEquals(2021, second.getYear());
        assertThrows(InvalidDataException.class, () -> branch.changeYear(first, 1970));
        assertEquals(2019, first.getYear());
        branch.changeYear(first, 2019);
        assertThrows(LaptopNotFoundException.class, () -> branch.changeYear(laptop("Nie ma", 1), 2020));
        assertThrows(InvalidDataException.class, () -> branch.changeYear(null, 2020));
    }

    @Test
    void countSortSearchSummaryAndMaxDoNotReorderTheShelf() throws Exception {
        assertEquals(0, branch.countByCondition(null));
        assertNull(branch.max());
        assertEquals(0, branch.totalValue(), 0.001);
        assertNull(branch.search("Alfa"));
        assertNull(branch.search(null));
        assertTrue(branch.searchPartial(null).isEmpty());
        assertTrue(branch.searchPartial("").isEmpty());
        String emptySummary = OutputCapture.stdout(branch::summary);
        assertTrue(emptySummary.contains("Brak laptopów."));

        Laptop zeta = new Laptop("Zeta", "Dell", LaptopCondition.DAMAGED, 2018, 300);
        Laptop alfa = new Laptop("Alfa", "Lenovo", LaptopCondition.WORKING, 2019, 100);
        branch.addLaptop(zeta);
        branch.addLaptop(alfa);
        assertEquals(1, branch.countByCondition(LaptopCondition.WORKING));
        assertEquals(1, branch.countByCondition(LaptopCondition.DAMAGED));
        assertEquals(0, branch.countByCondition(LaptopCondition.IN_REPAIR));
        assertEquals(400, branch.totalValue(), 0.001);
        assertSame(zeta, branch.max());

        List<Laptop> byName = branch.sortByName();
        List<Laptop> byPrice = branch.sortByPrice();
        assertEquals("Alfa", byName.get(0).getModel());
        assertEquals("Zeta", byName.get(1).getModel());
        assertEquals(100, byPrice.get(0).getPrice(), 0.001);
        assertEquals(300, byPrice.get(1).getPrice(), 0.001);
        assertEquals("Zeta", branch.getLaptops().get(0).getModel());
        assertEquals("Alfa", branch.getLaptops().get(1).getModel());

        assertSame(alfa, branch.search("Alfa"));
        assertNull(branch.search("Brak"));
        assertEquals(1, branch.searchPartial("Len").size());
        assertEquals(1, branch.searchPartial("Zet").size());
        assertTrue(branch.searchPartial("xyz").isEmpty());

        Laptop sameModel = new Laptop("Alfa", "HP", LaptopCondition.WORKING, 2020, 10);
        Branch wider = new Branch("Szukaj", 3);
        wider.addLaptop(alfa);
        wider.addLaptop(sameModel);
        assertEquals("Alfa", wider.search("Alfa").getModel());

        String summary = OutputCapture.stdout(branch::summary);
        assertTrue(summary.contains("Warszawa Centrum"));
        assertTrue(summary.contains("Zeta"));
        assertTrue(summary.contains("400"));
    }

    private static Laptop laptop(String model, double price) throws InvalidPriceException, InvalidDataException {
        return new Laptop(model, "Lenovo", LaptopCondition.WORKING, 2019, price);
    }
}
