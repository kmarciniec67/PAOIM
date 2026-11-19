package lab3.ui;

import lab3.exception.InvalidDataException;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Model tabeli oparty na refleksji. Te same kolumny opisane nazwami getterów
 * obsługują oddziały, laptopy i prośby o kontakt.
 */
public class GenericTableModel<T> extends AbstractTableModel {
    private List<T> rows;
    private final String[] columnNames;
    private final Method[] getters;

    public GenericTableModel(Class<T> type, List<T> rows, String[] columnNames, String[] properties)
            throws InvalidDataException {
        if (type == null || columnNames == null || properties == null || columnNames.length != properties.length) {
            throw new InvalidDataException("Niepoprawna definicja kolumn tabeli.");
        }
        this.rows = rows == null ? new ArrayList<>() : rows;
        this.columnNames = columnNames.clone();
        this.getters = new Method[properties.length];
        for (int i = 0; i < properties.length; i++) {
            getters[i] = findGetter(type, properties[i]);
        }
    }

    public void setRows(List<T> rows) {
        this.rows = rows == null ? new ArrayList<>() : rows;
        fireTableDataChanged();
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public T getRow(int index) {
        return rows.get(index);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            return getters[columnIndex].invoke(rows.get(rowIndex));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return "";
        }
    }

    private static Method findGetter(Class<?> type, String property) throws InvalidDataException {
        if (property == null || property.isEmpty()) {
            throw new InvalidDataException("Nazwa właściwości jest pusta.");
        }
        String suffix = Character.toUpperCase(property.charAt(0)) + property.substring(1);
        try {
            return type.getMethod("get" + suffix);
        } catch (NoSuchMethodException notFound) {
            try {
                return type.getMethod(property);
            } catch (NoSuchMethodException again) {
                throw new InvalidDataException(
                        "Brak właściwości \"" + property + "\" w klasie " + type.getSimpleName() + ".");
            }
        }
    }
}
