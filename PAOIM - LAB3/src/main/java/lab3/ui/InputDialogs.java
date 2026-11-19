package lab3.ui;

import lab3.exception.InvalidDataException;
import lab3.exception.InvalidPriceException;
import lab3.model.Laptop;
import lab3.model.LaptopCondition;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.Year;

/** Okna dialogowe Swing. Błędy modelu pokazuje {@link JOptionPane}. */
final class InputDialogs {
    private InputDialogs() {
    }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    static boolean confirm(Component parent, String message) {
        int answer = JOptionPane.showConfirmDialog(parent, message, "Potwierdzenie",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }

    static String[] branch(Component parent) {
        JTextField name = new JTextField();
        JTextField capacity = new JTextField();
        JPanel form = form();
        form.add(new JLabel("Nazwa oddziału"));
        form.add(name);
        form.add(new JLabel("Pojemność"));
        form.add(capacity);
        if (!accepted(parent, form, "add branch")) {
            return null;
        }
        return new String[]{name.getText(), capacity.getText()};
    }

    static Laptop laptop(Component parent) {
        JTextField model = new JTextField();
        JTextField brand = new JTextField();
        JComboBox<LaptopCondition> condition = new JComboBox<>(LaptopCondition.values());
        JTextField year = new JTextField(String.valueOf(Year.now().getValue()));
        JTextField price = new JTextField();
        JTextField ram = new JTextField("8");
        JTextField processor = new JTextField();
        JTextField battery = new JTextField("100");
        JPanel form = form();
        form.add(new JLabel("Model"));
        form.add(model);
        form.add(new JLabel("Marka"));
        form.add(brand);
        form.add(new JLabel("Stan"));
        form.add(condition);
        form.add(new JLabel("Rok produkcji"));
        form.add(year);
        form.add(new JLabel("Cena (zł)"));
        form.add(price);
        form.add(new JLabel("RAM (GB)"));
        form.add(ram);
        form.add(new JLabel("Procesor"));
        form.add(processor);
        form.add(new JLabel("Bateria (%)"));
        form.add(battery);
        if (!accepted(parent, form, "add new laptop")) {
            return null;
        }
        try {
            return new Laptop(
                    model.getText(),
                    brand.getText(),
                    (LaptopCondition) condition.getSelectedItem(),
                    Parsers.integer(year.getText(), "Rok produkcji"),
                    Parsers.decimal(price.getText(), "Cena"),
                    Parsers.integer(ram.getText(), "RAM"),
                    processor.getText(),
                    Parsers.integer(battery.getText(), "Bateria"));
        } catch (InvalidPriceException | InvalidDataException ex) {
            error(parent, ex.getMessage());
            return null;
        }
    }

    static LaptopCondition condition(Component parent, LaptopCondition current) {
        JComboBox<LaptopCondition> box = new JComboBox<>(LaptopCondition.values());
        box.setSelectedItem(current);
        if (!accepted(parent, box, "edit condition")) {
            return null;
        }
        return (LaptopCondition) box.getSelectedItem();
    }

    static String price(Component parent, double current) {
        Object value = JOptionPane.showInputDialog(parent, "Nowa cena (zł)", "edit price",
                JOptionPane.QUESTION_MESSAGE, null, null, current);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    static String[] contact(Component parent) {
        JTextField name = new JTextField();
        JTextArea message = new JTextArea(4, 24);
        JPanel form = form();
        form.add(new JLabel("Imię i nazwisko"));
        form.add(name);
        form.add(new JLabel("Wiadomość"));
        form.add(message);
        if (!accepted(parent, form, "request contact")) {
            return null;
        }
        return new String[]{name.getText(), message.getText()};
    }

    private static JPanel form() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        return form;
    }

    private static boolean accepted(Component parent, JComponent form, String title) {
        int result = JOptionPane.showConfirmDialog(parent, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }
}
