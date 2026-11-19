package lab3.support;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class OutputCapture {
    private OutputCapture() {
    }

    public static String stdout(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            action.run();
            return buffer.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(original);
        }
    }
}
