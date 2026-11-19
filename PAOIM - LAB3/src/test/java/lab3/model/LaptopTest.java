package lab3.model;

import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.support.OutputCapture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Laptop")
public class LaptopTest {
    @Test
    void shortConstructorUsesDefaultsAndUniqueSerial() throws Exception {
        Laptop first = new Laptop("ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2018, 1199);
        Laptop second = new Laptop("Latitude 7490", "Dell", LaptopCondition.IN_TESTING, 2019, 0);
        assertEquals(8, first.getRamGb());
        assertEquals("niepodany", first.getProcessor());
        assertEquals(100, first.getBatteryHealth());
        assertTrue(first.getSerialNumber().startsWith("SN-"));
        assertNotEquals(first.getSerialNumber(), second.getSerialNumber());
        assertEquals(0, second.getPrice(), 0.001);
    }

    @Test
    void fullConstructorKeepsAllFields() throws Exception {
        Laptop laptop = laptop();
        assertEquals("XPS 13", laptop.getModel());
        assertEquals("Dell", laptop.getBrand());
        assertEquals(LaptopCondition.WORKING, laptop.getCondition());
        assertEquals(2020, laptop.getYear());
        assertEquals(2199, laptop.getPrice(), 0.001);
        assertEquals("2199.00 zł", laptop.getPriceLabel());
        assertEquals(16, laptop.getRamGb());
        assertEquals("Intel Core i7", laptop.getProcessor());
        assertEquals(88, laptop.getBatteryHealth());
        assertEquals("SN-XPS", laptop.getSerialNumber());
        assertEquals(Year.now().getValue() - 2020, laptop.age());
    }

    @Test
    void constructorTrimsText() throws Exception {
        Laptop laptop = new Laptop("  XPS 13 ", " Dell ", LaptopCondition.DAMAGED, 2020, 10,
                8, " i7 ", 50, " SN ");
        assertEquals("XPS 13", laptop.getModel());
        assertEquals("Dell", laptop.getBrand());
        assertEquals("i7", laptop.getProcessor());
        assertEquals("SN", laptop.getSerialNumber());
    }

    @Test
    void constructorRejectsInvalidText() {
        assertThrows(InvalidDataException.class,
                () -> new Laptop(" ", "Dell", LaptopCondition.WORKING, 2020, 10));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", null, LaptopCondition.WORKING, 2020, 10));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, 10, 8, " ", 10, "SN"));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, 10, 8, "i7", 10, null));
    }

    @Test
    void constructorRejectsNullConditionBadYearRamAndBattery() {
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", null, 2020, 10));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 1979, 10));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, Year.now().getValue() + 1, 10));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, 10, 0, "i7", 10, "SN"));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, 10, 8, "i7", -1, "SN"));
        assertThrows(InvalidDataException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, 10, 8, "i7", 101, "SN"));
    }

    @Test
    void constructorAcceptsBoundaryYearRamAndBattery() throws Exception {
        int current = Year.now().getValue();
        Laptop old = new Laptop("Old", "HP", LaptopCondition.DAMAGED, 1980, 1, 1, "cpu", 0, "A");
        Laptop fresh = new Laptop("New", "HP", LaptopCondition.WORKING, current, 1, 32, "cpu", 100, "B");
        assertEquals(1980, old.getYear());
        assertEquals(current, fresh.getYear());
        assertEquals(0, fresh.age());
    }

    @Test
    void constructorAndSetterRejectBadPrice() throws Exception {
        assertThrows(InvalidPriceException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, -0.01));
        assertThrows(InvalidPriceException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, Double.NaN));
        assertThrows(InvalidPriceException.class,
                () -> new Laptop("XPS", "Dell", LaptopCondition.WORKING, 2020, Double.POSITIVE_INFINITY));
        Laptop laptop = laptop();
        assertThrows(InvalidPriceException.class, () -> laptop.setPrice(-1));
        assertThrows(InvalidPriceException.class, () -> laptop.setPrice(Double.NaN));
        assertEquals(2199, laptop.getPrice(), 0.001);
        laptop.setPrice(1500);
        assertEquals(1500, laptop.getPrice(), 0.001);
        assertEquals("1500.00 zł", laptop.getPriceLabel());
    }

    @Test
    void setConditionRejectsNullAndStoresValue() throws Exception {
        Laptop laptop = laptop();
        assertThrows(InvalidDataException.class, () -> laptop.setCondition(null));
        laptop.setCondition(LaptopCondition.RESERVED);
        assertEquals(LaptopCondition.RESERVED, laptop.getCondition());
    }

    @Test
    void setYearValidatesRange() throws Exception {
        Laptop laptop = laptop();
        assertThrows(InvalidDataException.class, () -> laptop.setYear(1970));
        assertEquals(2020, laptop.getYear());
        laptop.setYear(1980);
        assertEquals(1980, laptop.getYear());
        Laptop.checkYear(Year.now().getValue());
        assertThrows(InvalidDataException.class, () -> Laptop.checkYear(1979));
    }

    @Test
    void compareToUsesModelThenBrandThenAge() throws Exception {
        Laptop older = new Laptop("ThinkPad", "Lenovo", LaptopCondition.WORKING, 2018, 100, 8, "i5", 80, "A");
        Laptop younger = new Laptop("ThinkPad", "Lenovo", LaptopCondition.WORKING, 2020, 100, 8, "i5", 80, "B");
        Laptop otherBrand = new Laptop("ThinkPad", "Other", LaptopCondition.WORKING, 2018, 100, 8, "i5", 80, "C");
        Laptop otherModel = new Laptop("Latitude", "Dell", LaptopCondition.WORKING, 2018, 100, 8, "i5", 80, "D");
        assertTrue(older.compareTo(younger) > 0);
        assertTrue(younger.compareTo(older) < 0);
        assertTrue(older.compareTo(otherBrand) < 0);
        assertTrue(otherModel.compareTo(older) < 0);
        assertEquals(0, older.compareTo(new Laptop("ThinkPad", "Lenovo", LaptopCondition.DAMAGED, 2018, 5, 8, "i5", 10, "E")));
    }

    @Test
    void equalsAndHashCodeIgnorePriceConditionAndSerial() throws Exception {
        Laptop first = laptop();
        Laptop same = new Laptop("XPS 13", "Dell", LaptopCondition.DAMAGED, 2020, 1, 8, "inny", 10, "INNY");
        Laptop differentYear = new Laptop("XPS 13", "Dell", LaptopCondition.WORKING, 2019, 2199, 16, "Intel Core i7", 88, "SN-XPS");
        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertEquals(first, first);
        assertNotEquals(first, differentYear);
        assertNotEquals(first, null);
        assertNotEquals(first, "XPS 13");
        assertFalse(first.equals(new Laptop("Inny", "Dell", LaptopCondition.WORKING, 2020, 1)));
    }

    @Test
    void toStringAndPrintDescribeLaptop() throws Exception {
        Laptop laptop = laptop();
        assertTrue(laptop.toString().contains("Dell"));
        assertTrue(laptop.toString().contains("XPS 13"));
        assertTrue(laptop.toString().contains("Sprawny"));
        String printed = OutputCapture.stdout(laptop::print);
        assertTrue(printed.contains("Laptop: Dell XPS 13"));
        assertTrue(printed.contains("SN-XPS"));
        assertTrue(printed.contains("Intel Core i7"));
    }

    @Test
    void modelKeyIsFoundByModelOnly() {
        Laptop key = Laptop.modelKey("Latitude 7490");
        assertEquals("Latitude 7490", key.getModel());
        assertEquals("", key.getBrand());
    }

    private static Laptop laptop() throws InvalidPriceException, InvalidDataException {
        return new Laptop("XPS 13", "Dell", LaptopCondition.WORKING, 2020, 2199,
                16, "Intel Core i7", 88, "SN-XPS");
    }
}
