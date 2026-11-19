package lab1;

import java.util.Locale;

/**
 * Wspólny typ każdej funkcji jednej zmiennej.
 */
public abstract class Function {
  protected static final double EPSILON = 1e-9;

  public abstract double calculateValue(double x);

  public abstract double[] calculateZeros();

  static String formatNumber(double value) {
    if (Math.abs(value) < EPSILON) {
      value = 0.0;
    }
    return String.format(Locale.US, "%.2f", value);
  }

  protected final String formatZeros() {
    double[] zeros = calculateZeros();
    if (zeros.length == 0) {
      return "brak";
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < zeros.length; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(formatNumber(zeros[i]));
    }
    return builder.toString();
  }

  protected static String formatLeadingTerm(double coefficient, String suffix) {
    String number = formatNumber(Math.abs(coefficient));
    if (coefficient < 0.0) {
      return "-" + number + suffix;
    }
    return number + suffix;
  }

  protected static void appendSignedTerm(
      StringBuilder formula, double coefficient, String suffix) {
    if (Math.abs(coefficient) < EPSILON) {
      return;
    }
    if (coefficient < 0.0) {
      formula.append(" - ");
    } else {
      formula.append(" + ");
    }
    formula.append(formatNumber(Math.abs(coefficient)));
    formula.append(suffix);
  }
}
