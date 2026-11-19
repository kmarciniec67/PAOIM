package lab1;

public class LinearFunction extends Function implements Printable {
  private final double a;
  private final double b;

  public LinearFunction(double a, double b) {
    if (!Double.isFinite(a) || !Double.isFinite(b)) {
      throw new IllegalArgumentException("Współczynniki muszą być liczbami skończonymi");
    }
    if (Math.abs(a) < EPSILON) {
      throw new IllegalArgumentException("Współczynnik a nie może być równy 0");
    }
    this.a = a;
    this.b = b;
  }

  @Override
  public double calculateValue(double x) {
    return a * x + b;
  }

  @Override
  public double[] calculateZeros() {
    return new double[] {-b / a};
  }

  public boolean isIncreasing() {
    return a > 0.0;
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
    formula.append(formatLeadingTerm(a, "x"));
    appendSignedTerm(formula, b, "");
    return formula.toString();
  }
}
