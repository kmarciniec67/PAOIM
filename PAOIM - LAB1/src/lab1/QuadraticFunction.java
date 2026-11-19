package lab1;

/**
 * Funkcja kwadratowa f(x) = ax^2 + bx + c. Współczynnik a nie może być zerem,
 * bo wtedy nie jest to funkcja kwadratowa.
 */
public class QuadraticFunction extends Function implements Printable {
  private final double a;
  private final double b;
  private final double c;

  public QuadraticFunction(double a, double b, double c) {
    if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(c)) {
      throw new IllegalArgumentException("Współczynniki muszą być liczbami skończonymi");
    }
    if (Math.abs(a) < EPSILON) {
      throw new IllegalArgumentException("Współczynnik a nie może być równy 0");
    }
    this.a = a;
    this.b = b;
    this.c = c;
  }

  @Override
  public double calculateValue(double x) {
    return a * x * x + b * x + c;
  }

  public double calculateDelta() {
    return b * b - 4.0 * a * c;
  }

  public double[] calculateVertex() {
    double p = -b / (2.0 * a);
    double q = calculateValue(p);
    return new double[] {p, q};
  }

  @Override
  public double[] calculateZeros() {
    double delta = calculateDelta();
    if (delta < -EPSILON) {
      return new double[0];
    }
    if (delta <= EPSILON) {
      return new double[] {-b / (2.0 * a)};
    }
    double root = Math.sqrt(delta);
    double x1 = (-b - root) / (2.0 * a);
    double x2 = (-b + root) / (2.0 * a);
    if (x1 > x2) {
      double swap = x1;
      x1 = x2;
      x2 = swap;
    }
    return new double[] {x1, x2};
  }

  @Override
  public void print() {
    double[] vertex = calculateVertex();
    System.out.println("--- f(x) = " + formatFormula());
    System.out.println(
        "Współczynniki: a = " + formatNumber(a)
            + ", b = " + formatNumber(b)
            + ", c = " + formatNumber(c));
    System.out.println(
        "Delta: " + formatNumber(calculateDelta())
            + ", wierzchołek: ("
            + formatNumber(vertex[0])
            + ", "
            + formatNumber(vertex[1])
            + ")");
    System.out.println("Miejsca zerowe: " + formatZeros());
    if (calculateZeros().length < 2) {
      System.out.println("Dodatkowa tabela wartości na [5, 10], krok 0.5:");
      new FunctionOnInterval(this, 5.0, 10.0, 0.5).print();
    }
  }

  private String formatFormula() {
    StringBuilder formula = new StringBuilder();
    formula.append(formatLeadingTerm(a, "x^2"));
    appendSignedTerm(formula, b, "x");
    appendSignedTerm(formula, c, "");
    return formula.toString();
  }
}
