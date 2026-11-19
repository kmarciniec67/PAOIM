package lab1;

import java.util.Locale;
import java.util.Scanner;

/**
 * Konsola: menu, wczytywanie liczb i wypisanie gotowych obiektów.
 * Tu nie ma wzorów funkcji — tylko rozmowa z użytkownikiem.
 */
public class ConsoleUserInterface {
  private final Scanner scanner;

  public ConsoleUserInterface() {
    scanner = new Scanner(System.in);
    scanner.useLocale(Locale.US);
  }

  public void run() {
    while (true) {
      System.out.println("1 - Liniowa  2 - Kwadratowa  3 - Wykładnicza  0 - Wyjście");
      int choice = readInt("> ");
      switch (choice) {
        case 0:
          System.out.println("Koniec programu.");
          return;
        case 1:
          handleLinear();
          break;
        case 2:
          handleQuadratic();
          break;
        case 3:
          handleExponential();
          break;
        default:
          System.out.println("Wybierz 0, 1, 2 albo 3.");
          System.out.println();
          break;
      }
    }
  }

  private void handleLinear() {
    double a = readDouble("a: ");
    double b = readDouble("b: ");
    try {
      LinearFunction function = new LinearFunction(a, b);
      readIntervalAndPrint(function, function);
    } catch (IllegalArgumentException exception) {
      System.out.println("Błąd: " + exception.getMessage());
      System.out.println();
    }
  }

  private void handleQuadratic() {
    double a = readDouble("a: ");
    double b = readDouble("b: ");
    double c = readDouble("c: ");
    try {
      QuadraticFunction function = new QuadraticFunction(a, b, c);
      readIntervalAndPrint(function, function);
    } catch (IllegalArgumentException exception) {
      System.out.println("Błąd: " + exception.getMessage());
      System.out.println();
    }
  }

  private void handleExponential() {
    double a = readDouble("a: ");
    double b = readDouble("b: ");
    try {
      ExponentialFunction function = new ExponentialFunction(a, b);
      readIntervalAndPrint(function, function);
    } catch (IllegalArgumentException exception) {
      System.out.println("Błąd: " + exception.getMessage());
      System.out.println();
    }
  }

  private void readIntervalAndPrint(Printable description, Function function) {
    double start = readDouble("Początek przedziału: ");
    double end = readDouble("Koniec przedziału: ");
    double step = readDouble("Krok: ");
    FunctionOnInterval interval = new FunctionOnInterval(function, start, end, step);
    description.print();
    interval.print();
    System.out.println();
  }

  private int readInt(String prompt) {
    while (true) {
      System.out.print(prompt);
      if (!scanner.hasNext()) {
        return 0;
      }
      if (scanner.hasNextInt()) {
        return scanner.nextInt();
      }
      System.out.println("Podaj liczbę całkowitą.");
      scanner.next();
    }
  }

  private double readDouble(String prompt) {
    while (true) {
      System.out.print(prompt);
      if (!scanner.hasNext()) {
        throw new IllegalStateException("Brak danych wejściowych");
      }
      if (scanner.hasNextDouble()) {
        return scanner.nextDouble();
      }
      System.out.println("Podaj liczbę. Ułamek zapisuj kropką, np. 1.5.");
      scanner.next();
    }
  }
}
