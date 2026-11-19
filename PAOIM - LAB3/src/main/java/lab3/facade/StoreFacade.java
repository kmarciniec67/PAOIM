package lab3.facade;

import lab3.exception.BranchFullException;
import lab3.exception.BranchNotFoundException;
import lab3.exception.DuplicateBranchException;
import lab3.exception.DuplicateLaptopException;
import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.exception.LaptopNotFoundException;
import lab3.model.Branch;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;
import lab3.model.StoreManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Fasada między interfejsem a modelem. UI woła tylko te operacje,
 * których potrzebuje panel administratora albo klienta.
 */
public class StoreFacade {
    private final StoreManager manager;
    private final List<ContactRequest> contactRequests = new ArrayList<>();

    public StoreFacade(StoreManager manager) throws InvalidDataException {
        if (manager == null) {
            throw new InvalidDataException("Menedżer sieci jest wymagany.");
        }
        this.manager = manager;
    }

    /** Ta sama referencja listy, którą sortuje {@link #sortBranchesByCurrentLoad()}. */
    public List<Branch> getBranches() {
        return manager.getBranches();
    }

    public Branch addBranch(String name, int capacity) throws InvalidDataException, DuplicateBranchException {
        return manager.addBranch(name, capacity);
    }

    public void removeBranch(Branch branch) throws InvalidDataException, BranchNotFoundException {
        Branch stored = requireBranch(branch);
        manager.removeBranch(stored.getBranchName());
    }

    public void addLaptop(Branch branch, Laptop laptop)
            throws InvalidDataException, BranchNotFoundException, DuplicateLaptopException, BranchFullException {
        if (laptop == null) {
            throw new InvalidDataException("Nie wskazano laptopa.");
        }
        requireBranch(branch).addLaptop(laptop);
    }

    public void removeLaptop(Branch branch, Laptop laptop)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException {
        requireBranch(branch).removeLaptop(laptop);
    }

    /**
     * Jedno wywołanie sortuje oddziały po aktualnym zapełnieniu.
     * Tabela trzyma wynik {@link #getBranches()}, więc nie kopiujemy kolekcji.
     */
    public void sortBranchesByCurrentLoad() {
        manager.sortByCurrentLoad();
    }

    public List<Laptop> filterLaptops(Branch branch, String text, LaptopCondition condition) {
        if (branch == null) {
            return new ArrayList<>();
        }
        String query = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        List<Laptop> result = new ArrayList<>();
        for (Laptop laptop : branch.getLaptops()) {
            boolean textMatches = query.isEmpty()
                    || laptop.getModel().toLowerCase(Locale.ROOT).contains(query)
                    || laptop.getBrand().toLowerCase(Locale.ROOT).contains(query);
            boolean conditionMatches = condition == null || laptop.getCondition() == condition;
            if (textMatches && conditionMatches) {
                result.add(laptop);
            }
        }
        return result;
    }

    public void reserveLaptop(Branch branch, Laptop laptop)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException {
        Branch stored = requireBranch(branch);
        Laptop found = requireLaptop(stored, laptop);
        if (found.getCondition() != LaptopCondition.WORKING) {
            throw new InvalidDataException("Rezerwacja jest możliwa tylko dla laptopów w stanie Sprawny.");
        }
        stored.changeCondition(found, LaptopCondition.RESERVED);
    }

    public void changeCondition(Branch branch, Laptop laptop, LaptopCondition condition)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException {
        requireBranch(branch).changeCondition(laptop, condition);
    }

    public void changePrice(Branch branch, Laptop laptop, double price)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException, InvalidPriceException {
        requireBranch(branch).changePrice(laptop, price);
    }

    public void requestContact(Branch branch, Laptop laptop, String clientName, String message)
            throws InvalidDataException, BranchNotFoundException, LaptopNotFoundException {
        Branch stored = requireBranch(branch);
        Laptop found = requireLaptop(stored, laptop);
        if (clientName == null || clientName.trim().isEmpty()) {
            throw new InvalidDataException("Podaj imię i nazwisko.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidDataException("Wpisz treść prośby o kontakt.");
        }
        contactRequests.add(new ContactRequest(
                clientName.trim(),
                stored.getBranchName(),
                found.getBrand() + " " + found.getModel() + " (" + found.getSerialNumber() + ")",
                message.trim()));
    }

    public List<ContactRequest> getContactRequests() {
        return Collections.unmodifiableList(contactRequests);
    }

    private Branch requireBranch(Branch branch) throws InvalidDataException, BranchNotFoundException {
        if (branch == null) {
            throw new InvalidDataException("Wybierz oddział.");
        }
        Branch stored = manager.getBranch(branch.getBranchName());
        if (stored == null) {
            throw new BranchNotFoundException("Nie znaleziono oddziału \"" + branch.getBranchName() + "\".");
        }
        return stored;
    }

    private Laptop requireLaptop(Branch branch, Laptop laptop)
            throws InvalidDataException, LaptopNotFoundException {
        if (laptop == null) {
            throw new InvalidDataException("Wybierz laptop.");
        }
        for (Laptop stored : branch.getLaptops()) {
            if (stored.equals(laptop)) {
                return stored;
            }
        }
        throw new LaptopNotFoundException("Brak laptopa w oddziale \"" + branch.getBranchName() + "\".");
    }
}
