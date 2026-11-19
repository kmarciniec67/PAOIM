package lab3.data;

import lab3.exception.StoreException;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;
import lab3.model.StoreManager;

/**
 * Dane startowe do sprawdzenia interfejsu: kilka oddziałów i kilkanaście laptopów.
 * Aplikacja korzysta z jednej instancji (Singleton), testy wołają {@link #createSample()}.
 */
public final class DataGenerator {
    private static DataGenerator instance;
    private final StoreManager manager;

    private DataGenerator(StoreManager manager) {
        this.manager = manager;
    }

    public static synchronized DataGenerator getInstance() throws StoreException {
        if (instance == null) {
            instance = new DataGenerator(createSample());
        }
        return instance;
    }

    public StoreManager getManager() {
        return manager;
    }

    public static StoreManager createSample() throws StoreException {
        StoreManager manager = new StoreManager();
        Branch warszawa = manager.addBranch("Warszawa Centrum", 6);
        Branch krakow = manager.addBranch("Kraków Kazimierz", 4);
        Branch gdansk = manager.addBranch("Gdańsk Wrzeszcz", 5);
        Branch poznan = manager.addBranch("Poznań Rynek", 3);
        manager.addBranch("Wrocław Rynek", 3);

        add(warszawa, laptop("ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2018, 1199.0,
                16, "Intel Core i5-8350U", 87, "PF-T480-2018"));
        add(warszawa, laptop("Latitude 7490", "Dell", LaptopCondition.IN_TESTING, 2019, 999.0,
                8, "Intel Core i5-8365U", 92, "PF-L7490-2019"));
        add(warszawa, laptop("MacBook Pro 13", "Apple", LaptopCondition.WORKING, 2017, 2499.0,
                8, "Intel Core i5-7360U", 78, "PF-MBP13-2017"));
        add(warszawa, laptop("EliteBook 840 G5", "HP", LaptopCondition.DAMAGED, 2018, 799.0,
                16, "Intel Core i5-8250U", 64, "PF-EB840-2018"));
        add(warszawa, laptop("VivoBook 15", "Asus", LaptopCondition.WORKING, 2022, 1599.0,
                16, "Intel Core i5-1235U", 100, "PF-VB15-2022"));

        add(krakow, laptop("ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2020, 1399.0,
                16, "Intel Core i5-10210U", 95, "PF-T480-2020"));
        add(krakow, laptop("ZenBook 14", "Asus", LaptopCondition.IN_REPAIR, 2021, 1899.0,
                16, "AMD Ryzen 5 5500U", 90, "PF-ZU14-2021"));
        add(krakow, laptop("XPS 13", "Dell", LaptopCondition.IN_TESTING, 2020, 2199.0,
                16, "Intel Core i7-1165G7", 88, "PF-XPS13-2020"));
        add(krakow, laptop("Chromebook 14", "Acer", LaptopCondition.WORKING, 2019, 499.0,
                4, "Intel Celeron N4100", 80, "PF-CB14-2019"));

        add(gdansk, laptop("MacBook Air", "Apple", LaptopCondition.DAMAGED, 2019, 1799.0,
                8, "Intel Core i5-8210Y", 70, "PF-MBA-2019"));
        add(gdansk, laptop("ProBook 450", "HP", LaptopCondition.WORKING, 2021, 1099.0,
                16, "Intel Core i5-1135G7", 91, "PF-PB450-2021"));
        add(gdansk, laptop("IdeaPad 5", "Lenovo", LaptopCondition.IN_REPAIR, 2022, 1399.0,
                16, "AMD Ryzen 5 5625U", 84, "PF-IP5-2022"));
        add(gdansk, laptop("Inspiron 15", "Dell", LaptopCondition.WORKING, 2018, 899.0,
                8, "Intel Core i5-8265U", 76, "PF-IN15-2018"));

        add(poznan, laptop("Aspire 5", "Acer", LaptopCondition.DAMAGED, 2020, 999.0,
                8, "AMD Ryzen 3 4300U", 60, "PF-AS5-2020"));
        add(poznan, laptop("Modern 14", "MSI", LaptopCondition.RESERVED, 2023, 2299.0,
                16, "Intel Core i7-1355U", 98, "PF-MD14-2023"));
        return manager;
    }

    private static void add(Branch branch, Laptop laptop) throws StoreException {
        branch.addLaptop(laptop);
    }

    private static Laptop laptop(String model, String brand, LaptopCondition condition, int year, double price,
                                 int ramGb, String processor, int batteryHealth, String serialNumber)
            throws StoreException {
        return new Laptop(model, brand, condition, year, price, ramGb, processor, batteryHealth, serialNumber);
    }
}
