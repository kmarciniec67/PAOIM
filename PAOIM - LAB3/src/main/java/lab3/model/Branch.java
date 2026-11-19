package lab3.model;

import lab3.exception.BranchFullException;
import lab3.exception.DuplicateLaptopException;
import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.exception.LaptopNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Oddział (punkt sprzedaży) przechowujący laptopy do odsprzedaży.
 * Lista jest zadeklarowana jako {@link List}, a konkretną implementacją jest {@link ArrayList}:
 * oddziały są niewielkie, często czytamy je po indeksie i sortujemy kopie.
 */
public class Branch {
    private final String branchName;
    private final List<Laptop> laptopList;
    private final int maxCapacity;

    public Branch(String branchName, int maxCapacity) throws InvalidDataException {
        if (branchName == null || branchName.trim().isEmpty()) {
            throw new InvalidDataException("Nazwa oddziału jest wymagana.");
        }
        if (maxCapacity <= 0) {
            throw new InvalidDataException("Pojemność oddziału musi być dodatnia.");
        }
        this.branchName = branchName.trim();
        this.maxCapacity = maxCapacity;
        this.laptopList = new ArrayList<>();
    }

    /**
     * Dodaje laptop. Ten sam model, marka i rok są duplikatem.
     * Przekroczenie pojemności zgłasza {@link BranchFullException}.
     */
    public void addLaptop(Laptop laptop)
            throws DuplicateLaptopException, BranchFullException, InvalidDataException {
        if (laptop == null) {
            throw new InvalidDataException("Nie można dodać laptopa do oddziału \"" + branchName + "\": brak obiektu.");
        }
        if (laptopList.contains(laptop)) {
            throw new DuplicateLaptopException("Laptop już istnieje w oddziale \"" + branchName
                    + "\" (ten sam model, marka i wiek): " + laptop.getBrand() + " " + laptop.getModel()
                    + ", rok " + laptop.getYear() + ".");
        }
        if (laptopList.size() >= maxCapacity) {
            throw new BranchFullException("Oddział \"" + branchName + "\" jest pełny ("
                    + laptopList.size() + "/" + maxCapacity + "). Nie dodano: "
                    + laptop.getBrand() + " " + laptop.getModel() + ".");
        }
        laptopList.add(laptop);
    }

    /** Usuwa laptop z oddziału (utylizacja albo zwrot do dostawcy). */
    public Laptop removeLaptop(Laptop laptop) throws LaptopNotFoundException, InvalidDataException {
        Laptop removed = take(laptop);
        if (removed == null) {
            throw new LaptopNotFoundException(missingLaptopMessage());
        }
        return removed;
    }

    /** Usuwa laptop w wyniku sprzedaży. */
    public Laptop sellLaptop(Laptop laptop) throws LaptopNotFoundException, InvalidDataException {
        Laptop sold = take(laptop);
        if (sold == null) {
            throw new LaptopNotFoundException("Nie sprzedano — w oddziale \"" + branchName + "\" nie ma takiego laptopa.");
        }
        return sold;
    }

    public void changeCondition(Laptop laptop, LaptopCondition condition)
            throws LaptopNotFoundException, InvalidDataException {
        if (condition == null) {
            throw new InvalidDataException("Nie zmieniono stanu: brak nowej wartości.");
        }
        Laptop stored = find(laptop);
        if (stored == null) {
            throw new LaptopNotFoundException(missingLaptopMessage());
        }
        stored.setCondition(condition);
    }

    public void changePrice(Laptop laptop, double price)
            throws LaptopNotFoundException, InvalidPriceException, InvalidDataException {
        Laptop stored = find(laptop);
        if (stored == null) {
            throw new LaptopNotFoundException(missingLaptopMessage());
        }
        stored.setPrice(price);
    }

    public void changeYear(Laptop laptop, int year)
            throws LaptopNotFoundException, InvalidDataException, DuplicateLaptopException {
        int index = indexOf(laptop);
        if (index < 0) {
            throw new LaptopNotFoundException(missingLaptopMessage());
        }
        Laptop stored = laptopList.get(index);
        if (stored.getYear() == year) {
            return;
        }
        Laptop.checkYear(year);
        for (int i = 0; i < laptopList.size(); i++) {
            if (i == index) {
                continue;
            }
            Laptop other = laptopList.get(i);
            if (other.getModel().equals(stored.getModel())
                    && other.getBrand().equals(stored.getBrand())
                    && other.getYear() == year) {
                throw new DuplicateLaptopException("Nie zmieniono roku " + stored.getBrand() + " " + stored.getModel()
                        + ": w oddziale \"" + branchName + "\" jest już taki model, marka i wiek (rok " + year + ").");
            }
        }
        stored.setYear(year);
    }

    public int countByCondition(LaptopCondition condition) {
        if (condition == null) {
            return 0;
        }
        int count = 0;
        for (Laptop laptop : laptopList) {
            if (laptop.getCondition() == condition) {
                count++;
            }
        }
        return count;
    }

    /** Nowa lista posortowana alfabetycznie po modelu. Kolejność w oddziale zostaje bez zmian. */
    public List<Laptop> sortByName() {
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Collections.sort(sorted, Comparator.comparing(Laptop::getModel));
        return sorted;
    }

    /** Nowa lista posortowana po cenie rosnąco. */
    public List<Laptop> sortByPrice() {
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Collections.sort(sorted, new Comparator<Laptop>() {
            @Override
            public int compare(Laptop o1, Laptop o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
        return sorted;
    }

    /**
     * Zwraca laptop o podanym modelu albo {@code null}.
     * Lista jest sortowana komparatorem po modelu, a wynik znajduje {@code Collections.binarySearch}.
     */
    public Laptop search(String model) {
        if (model == null || laptopList.isEmpty()) {
            return null;
        }
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Comparator<Laptop> byModel = Comparator.comparing(Laptop::getModel);
        Collections.sort(sorted, byModel);
        int index = Collections.binarySearch(sorted, Laptop.modelKey(model), byModel);
        if (index < 0) {
            return null;
        }
        return sorted.get(index);
    }

    /** Wszystkie laptopy, których model albo marka zawiera podany fragment ({@code String.contains}). */
    public List<Laptop> searchPartial(String text) {
        List<Laptop> found = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return found;
        }
        for (Laptop laptop : laptopList) {
            if (laptop.getModel().contains(text) || laptop.getBrand().contains(text)) {
                found.add(laptop);
            }
        }
        return found;
    }

    public void summary() {
        System.out.println("--- Oddział: " + branchName + " (" + laptopList.size() + "/" + maxCapacity + ") ---");
        if (laptopList.isEmpty()) {
            System.out.println("Brak laptopów.");
            return;
        }
        int index = 1;
        for (Laptop laptop : laptopList) {
            System.out.println(index + ". " + laptop);
            index++;
        }
        System.out.printf("Łączna wartość: %.2f zł%n", totalValue());
    }

    /** Najdroższy laptop w oddziale. Pusta półka zwraca {@code null}. */
    public Laptop max() {
        if (laptopList.isEmpty()) {
            return null;
        }
        return Collections.max(laptopList, new Comparator<Laptop>() {
            @Override
            public int compare(Laptop o1, Laptop o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
    }

    public double totalValue() {
        double sum = 0;
        for (Laptop laptop : laptopList) {
            sum += laptop.getPrice();
        }
        return sum;
    }

    public boolean contains(Laptop laptop) {
        return laptop != null && laptopList.contains(laptop);
    }

    public boolean isEmpty() {
        return laptopList.isEmpty();
    }

    public boolean isFull() {
        return laptopList.size() >= maxCapacity;
    }

    public int size() {
        return laptopList.size();
    }

    public String getBranchName() {
        return branchName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getLaptopCount() {
        return laptopList.size();
    }

    public double fillRatio() {
        return (double) laptopList.size() / maxCapacity;
    }

    public String getFillLabel() {
        return String.format(Locale.US, "%.1f%%", fillRatio() * 100.0);
    }

    public List<Laptop> getLaptops() {
        return Collections.unmodifiableList(laptopList);
    }

    /** Wyjmuje egzemplarz z półki i zwraca obiekt przechowywany w oddziale. */
    Laptop take(Laptop laptop) throws InvalidDataException {
        int index = indexOf(laptop);
        if (index < 0) {
            return null;
        }
        return laptopList.remove(index);
    }

    /** Wkłada laptop już sprawdzony przez wywołującego (używane przy przeniesieniu). */
    void attach(Laptop laptop) {
        laptopList.add(laptop);
    }

    private Laptop find(Laptop laptop) throws InvalidDataException {
        int index = indexOf(laptop);
        if (index < 0) {
            return null;
        }
        return laptopList.get(index);
    }

    private int indexOf(Laptop laptop) throws InvalidDataException {
        if (laptop == null) {
            throw new InvalidDataException("Nie wskazano laptopa.");
        }
        return laptopList.indexOf(laptop);
    }

    private String missingLaptopMessage() {
        return "Brak laptopa w oddziale \"" + branchName + "\".";
    }
}
