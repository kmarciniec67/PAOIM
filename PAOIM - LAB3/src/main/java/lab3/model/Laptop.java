package lab3.model;

import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;

import java.time.Year;
import java.util.Locale;
import java.util.Objects;

/**
 * Laptop przyjęty do odsprzedaży.
 * <p>
 * Porządek naturalny ({@link Comparable}) i równość biznesowa obejmują model, markę i wiek
 * (wynikający z roku produkcji). Dwa egzemplarze o tym samym modelu, marce i roku są traktowane
 * jako ten sam laptop przy dodawaniu do oddziału.
 */
public class Laptop implements Comparable<Laptop> {
    private static int nextSerial = 1;

    private final String model;
    private final String brand;
    private LaptopCondition condition;
    private int year;
    private double price;
    private final int ramGb;
    private final String processor;
    private final int batteryHealth;
    private final String serialNumber;

    /**
     * Klucz używany wyłącznie przy wyszukiwaniu binarnym po modelu.
     * Nie przechodzi walidacji pełnego egzemplarza.
     */
    private Laptop(String model) {
        this.model = model;
        this.brand = "";
        this.condition = LaptopCondition.WORKING;
        this.year = Year.now().getValue();
        this.price = 0;
        this.ramGb = 0;
        this.processor = "";
        this.batteryHealth = 0;
        this.serialNumber = "";
    }

    public Laptop(String model, String brand, LaptopCondition condition, int year, double price)
            throws InvalidPriceException, InvalidDataException {
        this(model, brand, condition, year, price, 8, "niepodany", 100, nextSerial());
    }

    public Laptop(String model, String brand, LaptopCondition condition, int year, double price,
                  int ramGb, String processor, int batteryHealth)
            throws InvalidPriceException, InvalidDataException {
        this(model, brand, condition, year, price, ramGb, processor, batteryHealth, nextSerial());
    }

    public Laptop(String model, String brand, LaptopCondition condition, int year, double price,
                  int ramGb, String processor, int batteryHealth, String serialNumber)
            throws InvalidPriceException, InvalidDataException {
        this.model = requireText(model, "Model");
        this.brand = requireText(brand, "Marka");
        if (condition == null) {
            throw new InvalidDataException("Stan laptopa jest wymagany.");
        }
        checkYear(year);
        validatePrice(price);
        if (ramGb <= 0) {
            throw new InvalidDataException("Pamięć RAM musi być dodatnia.");
        }
        this.processor = requireText(processor, "Procesor");
        if (batteryHealth < 0 || batteryHealth > 100) {
            throw new InvalidDataException("Kondycja baterii musi być w zakresie 0-100%.");
        }
        this.serialNumber = requireText(serialNumber, "Numer seryjny");
        this.condition = condition;
        this.year = year;
        this.price = price;
        this.ramGb = ramGb;
        this.batteryHealth = batteryHealth;
    }

    static Laptop modelKey(String model) {
        return new Laptop(model);
    }

    public void print() {
        System.out.println("Laptop: " + brand + " " + model);
        System.out.println("  Numer seryjny: " + serialNumber);
        System.out.println("  Stan: " + condition);
        System.out.println("  Rok produkcji: " + year + " (wiek: " + age() + " lat)");
        System.out.printf("  Cena: %.2f zł%n", price);
        System.out.println("  RAM: " + ramGb + " GB");
        System.out.println("  Procesor: " + processor);
        System.out.println("  Kondycja baterii: " + batteryHealth + "%");
    }

    /**
     * Porównanie według modelu, marki, a następnie wieku (starszy laptop jest „większy”).
     */
    @Override
    public int compareTo(Laptop other) {
        int byModel = model.compareTo(other.model);
        if (byModel != 0) {
            return byModel;
        }
        int byBrand = brand.compareTo(other.brand);
        if (byBrand != 0) {
            return byBrand;
        }
        return Integer.compare(age(), other.age());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Laptop)) {
            return false;
        }
        Laptop other = (Laptop) obj;
        return year == other.year && model.equals(other.model) && brand.equals(other.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, brand, year);
    }

    @Override
    public String toString() {
        return String.format("%s %s, rok %d (wiek %d), %s, %.2f zł, nr %s",
                brand, model, year, age(), condition, price, serialNumber);
    }

    public int age() {
        return Year.now().getValue() - year;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public LaptopCondition getCondition() {
        return condition;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceLabel() {
        return String.format(Locale.US, "%.2f zł", price);
    }

    public int getRamGb() {
        return ramGb;
    }

    public String getProcessor() {
        return processor;
    }

    public int getBatteryHealth() {
        return batteryHealth;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setCondition(LaptopCondition condition) throws InvalidDataException {
        if (condition == null) {
            throw new InvalidDataException("Stan laptopa jest wymagany.");
        }
        this.condition = condition;
    }

    public void setPrice(double price) throws InvalidPriceException {
        validatePrice(price);
        this.price = price;
    }

    void setYear(int year) throws InvalidDataException {
        checkYear(year);
        this.year = year;
    }

    static void checkYear(int year) throws InvalidDataException {
        int current = Year.now().getValue();
        if (year < 1980 || year > current) {
            throw new InvalidDataException(
                    "Nieprawidłowy rok produkcji: " + year + " (dozwolone 1980-" + current + ").");
        }
    }

    private static void validatePrice(double price) throws InvalidPriceException {
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new InvalidPriceException("Cena ma nieprawidłową wartość.");
        }
        if (price < 0) {
            throw new InvalidPriceException("Cena nie może być ujemna.");
        }
    }

    private static String requireText(String value, String field) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(field + " nie może być pusty.");
        }
        return value.trim();
    }

    private static synchronized String nextSerial() {
        return String.format("SN-%05d", nextSerial++);
    }
}
