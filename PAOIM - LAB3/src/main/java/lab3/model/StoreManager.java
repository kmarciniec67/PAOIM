package lab3.model;

import lab3.exception.BranchFullException;
import lab3.exception.BranchNotFoundException;
import lab3.exception.DuplicateBranchException;
import lab3.exception.DuplicateLaptopException;
import lab3.exception.InvalidDataException;
import lab3.exception.LaptopNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sieć oddziałów. Kluczem mapy jest nazwa oddziału.
 * {@link LinkedHashMap} zachowuje kolejność dodania przy podglądzie po nazwie,
 * a lista {@link #branches} jest tą samą referencją, którą sortuje interfejs.
 */
public class StoreManager {
    private final List<Branch> branches = new ArrayList<>();
    private final List<Branch> branchView = Collections.unmodifiableList(branches);
    private final Map<String, Branch> byName = new LinkedHashMap<>();

    public Branch addBranch(String name, int capacity) throws InvalidDataException, DuplicateBranchException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nie dodano oddziału: nazwa jest pusta.");
        }
        String branchName = name.trim();
        if (byName.containsKey(branchName)) {
            throw new DuplicateBranchException("Oddział \"" + branchName + "\" już istnieje.");
        }
        if (capacity <= 0) {
            throw new InvalidDataException("Nie dodano oddziału \"" + branchName + "\": pojemność musi być dodatnia.");
        }
        Branch branch = new Branch(branchName, capacity);
        byName.put(branchName, branch);
        branches.add(branch);
        return branch;
    }

    public Branch removeBranch(String name) throws InvalidDataException, BranchNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nie usunięto oddziału: brak nazwy.");
        }
        String branchName = name.trim();
        Branch removed = byName.remove(branchName);
        if (removed == null) {
            throw new BranchNotFoundException("Nie znaleziono oddziału \"" + branchName + "\".");
        }
        branches.remove(removed);
        return removed;
    }

    public List<Branch> findEmpty() {
        List<Branch> empty = new ArrayList<>();
        for (Branch branch : branches) {
            if (branch.isEmpty()) {
                empty.add(branch);
            }
        }
        return empty;
    }

    /** Nazwa oddziału i procentowe zapełnienie. */
    public void summary() {
        System.out.println("=== Sieć sklepów (" + branches.size() + " oddziałów) ===");
        if (branches.isEmpty()) {
            System.out.println("Brak oddziałów.");
            return;
        }
        for (Branch branch : branches) {
            System.out.printf("Oddział: %s — zapełnienie %d/%d (%.1f%%)%n",
                    branch.getBranchName(),
                    branch.size(),
                    branch.getMaxCapacity(),
                    branch.fillRatio() * 100.0);
        }
    }

    /**
     * Przenosi laptop między oddziałami. Najpierw sprawdza miejsce i duplikat w celu,
     * dopiero potem zdejmuje laptop ze źródła — nieudany transfer nic nie usuwa.
     */
    public void transferLaptop(Laptop laptop, String fromBranch, String toBranch)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException,
            DuplicateLaptopException, BranchFullException {
        if (laptop == null) {
            throw new InvalidDataException("Nie przeniesiono laptopa: brak obiektu.");
        }
        Branch source = requireBranch(fromBranch, "źródłowego");
        Branch destination = requireBranch(toBranch, "docelowego");
        if (source == destination) {
            throw new InvalidDataException("Nie przeniesiono laptopa: źródło i cel to ten sam oddział.");
        }
        if (!source.contains(laptop)) {
            throw new LaptopNotFoundException("Nie przeniesiono laptopa: nie ma go w oddziale \""
                    + source.getBranchName() + "\".");
        }
        if (destination.contains(laptop)) {
            throw new DuplicateLaptopException("Nie przeniesiono laptopa: w \"" + destination.getBranchName()
                    + "\" jest już egzemplarz o tym samym modelu, marce i wieku.");
        }
        if (destination.isFull()) {
            throw new BranchFullException("Nie przeniesiono laptopa: oddział \"" + destination.getBranchName()
                    + "\" jest pełny (" + destination.size() + "/" + destination.getMaxCapacity() + ").");
        }
        Laptop moved = source.take(laptop);
        destination.attach(moved);
    }

    public double totalValue() {
        double sum = 0;
        for (Branch branch : branches) {
            sum += branch.totalValue();
        }
        return sum;
    }

    public Branch getBranch(String name) {
        if (name == null) {
            return null;
        }
        return byName.get(name.trim());
    }

    /**
     * Ta sama lista, którą ogląda tabela oddziałów.
     * {@link #sortByCurrentLoad()} sortuje ją w miejscu.
     */
    public List<Branch> getBranches() {
        return branchView;
    }

    /** Malejąco po aktualnym zapełnieniu, a przy remisie alfabetycznie po nazwie. */
    public void sortByCurrentLoad() {
        branches.sort(Comparator.comparingDouble(Branch::fillRatio).reversed()
                .thenComparing(Branch::getBranchName));
    }

    private Branch requireBranch(String name, String role) throws InvalidDataException, BranchNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nie przeniesiono laptopa: brak nazwy oddziału " + role + ".");
        }
        Branch branch = byName.get(name.trim());
        if (branch == null) {
            throw new BranchNotFoundException("Nie przeniesiono laptopa: brak oddziału \"" + name.trim() + "\".");
        }
        return branch;
    }
}
