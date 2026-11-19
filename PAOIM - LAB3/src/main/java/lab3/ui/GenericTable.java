package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.facade.ContactRequest;
import lab3.model.Branch;
import lab3.model.Laptop;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;

/** Komponent tabeli używany i dla oddziałów, i dla laptopów. */
public class GenericTable<T> extends JScrollPane {
    private final GenericTableModel<T> model;
    private final JTable table;

    public GenericTable(Class<T> type, List<T> rows, String[] columnNames, String[] properties)
            throws InvalidDataException {
        model = new GenericTableModel<>(type, rows, columnNames, properties);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setRowHeight(22);
        setViewportView(table);
        setPreferredSize(new Dimension(460, 200));
    }

    public static GenericTable<Branch> branches(List<Branch> rows) throws InvalidDataException {
        return new GenericTable<>(
                Branch.class,
                rows,
                new String[]{"Nazwa", "Pojemność", "Liczba laptopów", "Zapełnienie"},
                new String[]{"branchName", "maxCapacity", "laptopCount", "fillLabel"});
    }

    public static GenericTable<Laptop> laptops(List<Laptop> rows) throws InvalidDataException {
        return new GenericTable<>(
                Laptop.class,
                rows,
                new String[]{"Marka", "Model", "Stan", "Rok", "Wiek", "Cena", "RAM (GB)", "Procesor", "Bateria (%)", "Numer seryjny"},
                new String[]{"brand", "model", "condition", "year", "age", "priceLabel", "ramGb", "processor", "batteryHealth", "serialNumber"});
    }

    public static GenericTable<ContactRequest> requests(List<ContactRequest> rows) throws InvalidDataException {
        GenericTable<ContactRequest> table = new GenericTable<>(
                ContactRequest.class,
                rows,
                new String[]{"Klient", "Oddział", "Laptop", "Wiadomość"},
                new String[]{"clientName", "branchName", "laptopLabel", "message"});
        table.setPreferredSize(new Dimension(460, 110));
        return table;
    }

    public JTable getTable() {
        return table;
    }

    public GenericTableModel<T> getModel() {
        return model;
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public T getRow(int index) {
        return model.getRow(index);
    }

    public T getSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }
        return model.getRow(row);
    }

    public void selectRow(int index) {
        if (index < 0 || index >= model.getRowCount()) {
            table.clearSelection();
            return;
        }
        table.setRowSelectionInterval(index, index);
    }

    public void setRows(List<T> rows) {
        model.setRows(rows);
    }

    public void refresh() {
        model.refresh();
    }

    public void onSelection(final Consumer<T> listener) {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    listener.accept(getSelected());
                }
            }
        });
    }
}
