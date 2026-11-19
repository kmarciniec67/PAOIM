package lab3;

import lab3.data.DataGenerator;
import lab3.exception.StoreException;
import lab3.facade.StoreFacade;
import lab3.ui.ApplicationFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * PAOIM Lab 3 — interfejs graficzny sieci sklepów z laptopami.
 * Kacper Marciniec, GR4
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
                    // zostaje domyślny wygląd Swinga
                }
                try {
                    StoreFacade facade = new StoreFacade(DataGenerator.getInstance().getManager());
                    ApplicationFrame frame = new ApplicationFrame(facade);
                    frame.setVisible(true);
                } catch (StoreException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
