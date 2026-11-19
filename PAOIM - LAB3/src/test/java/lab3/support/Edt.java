package lab3.support;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

public final class Edt {
    private Edt() {
    }

    public static void run(Action action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        AtomicReference<Exception> failure = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Exception ex) {
                failure.set(ex);
            }
        });
        if (failure.get() != null) {
            throw failure.get();
        }
    }

    @FunctionalInterface
    public interface Action {
        void run() throws Exception;
    }
}
