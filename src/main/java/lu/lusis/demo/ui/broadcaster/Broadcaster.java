package lu.lusis.demo.ui.broadcaster;

import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Example from https://vaadin.com/docs/v12/flow/advanced/tutorial-push-broadcaster.html
 */
public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<String>> listeners = new LinkedList<>();

    public static synchronized Registration register(
            Consumer<String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        for (Consumer<String> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }
}