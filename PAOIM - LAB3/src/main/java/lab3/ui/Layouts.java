package lab3.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

final class Layouts {
    private Layouts() {
    }

    static GridBagConstraints cell(int x, int y, int width, double weightx, double weighty, int fill) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.fill = fill;
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }
}
