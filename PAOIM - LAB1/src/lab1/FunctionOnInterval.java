package lab1;

import java.util.ArrayList;

/**
 * Przebieg dowolnej funkcji na przedziale [początek, koniec] z zadanym krokiem.
 * Korzysta wyłącznie z calculateValue — nie sprawdza, jaka to konkretnie funkcja.
 */
public class FunctionOnInterval implements Printable {
  private static final double EPSILON = 1e-9;
  private static final int MAX_POINTS = 100000;

  private final Function function;
  private final double start;
  private final double end;
  private final double step;
  private final double[] arguments;
  private final double[] values;

  public FunctionOnInterval(Function function, double start, double end, double step) {
    if (function == null) {
      throw new IllegalArgumentException("Funkcja nie może być null");
    }
    if (!Double.isFinite(start) || !Double.isFinite(end) || !Double.isFinite(step)) {
      throw new IllegalArgumentException("Parametry przedziału muszą być liczbami skończonymi");
    }
    if (start >= end) {
      throw new IllegalArgumentException("Początek przedziału musi być mniejszy od końca");
    }
    double length = end - start;
    if (step <= 0.0 || step > length + EPSILON) {
      throw new IllegalArgumentException(
          "Krok musi spełniać warunek 0 < krok <= długość przedziału");
    }
    this.function = function;
    this.start = start;
    this.end = end;
    this.step = step;
    this.arguments = buildArguments();
    this.values = buildValues();
  }

  public double[] getArguments() {
    return copy(arguments);
  }

  public double[] getValues() {
    return copy(values);
  }

  public double getMin() {
    double min = values[0];
    for (int i = 1; i < values.length; i++) {
      if (values[i] < min) {
        min = values[i];
      }
    }
    return min;
  }

  public double getMax() {
    double max = values[0];
    for (int i = 1; i < values.length; i++) {
      if (values[i] > max) {
        max = values[i];
      }
    }
    return max;
  }

  public double calculateArea() {
    double area = 0.0;
    for (int i = 0; i < arguments.length - 1; i++) {
      double width = arguments[i + 1] - arguments[i];
      area += (values[i] + values[i + 1]) / 2.0 * width;
    }
    return area;
  }

  @Override
  public void print() {
    System.out.println(
        "--- Przebieg na [" + Function.formatNumber(start) + ", "
            + Function.formatNumber(end) + "], krok " + Function.formatNumber(step));
    System.out.println("x: " + join(arguments));
    System.out.println("f(x): " + join(values));
    System.out.println(
        "Min: " + Function.formatNumber(getMin()) + ", max: " + Function.formatNumber(getMax()));
    System.out.println("Pole (metoda trapezów): " + Function.formatNumber(calculateArea()));
  }

  private double[] buildArguments() {
    ArrayList<Double> points = new ArrayList<Double>();
    points.add(start);
    int i = 1;
    while (true) {
      double x = start + i * step;
      if (x > end - EPSILON) {
        break;
      }
      points.add(x);
      i++;
      if (i > MAX_POINTS) {
        throw new IllegalArgumentException(
            "Krok jest zbyt mały — tabela miałaby ponad 100000 punktów");
      }
    }
    double last = points.get(points.size() - 1);
    if (Math.abs(last - end) > EPSILON) {
      points.add(end);
    }
    double[] result = new double[points.size()];
    for (int index = 0; index < points.size(); index++) {
      result[index] = points.get(index);
    }
    return result;
  }

  private double[] buildValues() {
    double[] result = new double[arguments.length];
    for (int i = 0; i < arguments.length; i++) {
      result[i] = function.calculateValue(arguments[i]);
    }
    return result;
  }

  private static double[] copy(double[] source) {
    double[] result = new double[source.length];
    for (int i = 0; i < source.length; i++) {
      result[i] = source[i];
    }
    return result;
  }

  private static String join(double[] numbers) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < numbers.length; i++) {
      if (i > 0) {
        builder.append(' ');
      }
      builder.append(Function.formatNumber(numbers[i]));
    }
    return builder.toString();
  }
}
