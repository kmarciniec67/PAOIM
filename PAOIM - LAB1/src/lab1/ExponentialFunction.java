package lab1;

/**
 * Funkcja wykładnicza f(x) = a^x + b.
 * Podstawa a musi być dodatnia i różna od 1.
 */
public class ExponentialFunction extends Function implements Printable {
  private final double a;
  private final double b;

  public ExponentialFunction(double a, double b) {
    if (!Double.isFinite(a) || !Double.isFinite(b)) {
      throw new IllegalArgumentException("Współczynniki muszą być liczbami skończonymi");
    }
    if (a <= 0.0 || Math.abs(a - 1.0) < EPSILON) {
      throw new IllegalArgumentException("Podstawa a musi być dodatnia i różna od 1");
    }
    this.a = a;
    this.b = b;
  }

  @Override
  public double calculateValue(double x) {
    return Math.pow(a, x) + b;
  }

  /**
   * a^x + b = 0 daje a^x = -b.
   * a^x jest zawsze dodatnie, więc rozwiązanie istnieje tylko gdy -b > 0.
   */
  @Override
  public double[] calculateZeros() {
    if (b >= -EPSILON) {
      return new double[0];
    }
    double x = Math.log(-b) / Math.log(a);
    return new double[] {x};
  }

  public boolean isIncreasing() {
    return a > 1.0;
  }

  @Override
  public void print() {
    System.out.println("--- f(x) = " + formatFormula());
    System.out.println("Współczynniki: a = " + formatNumber(a) + ", b = " + formatNumber(b));
    System.out.println("Miejsca zerowe: " + formatZeros());
    System.out.println("Monotoniczność: " + (isIncreasing() ? "rosnąca" : "malejąca"));
  }

  private String formatFormula() {
    StringBuilder formula = new StringBuilder();
    formula.append(formatLeadingTerm(a, "^x"));
    appendSignedTerm(formula, b, "");
    return formula.toString();
  }
}
