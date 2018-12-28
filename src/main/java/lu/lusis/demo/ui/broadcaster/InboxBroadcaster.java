package lu.lusis.demo.ui.broadcaster;

import com.vaadin.flow.shared.Registration;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class InboxBroadcaster {

    private static List<Message> messageList;

    private static Executor executor = Executors.newSingleThreadExecutor();

    private static LinkedList<Consumer<Message>> listeners = new LinkedList<>();

    public static synchronized Registration register(
            Consumer<Message> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (InboxBroadcaster.class) {
                listeners.remove(listener);
                if (listeners.isEmpty()){
                    // on peut vider la liste
                    messageList = null;
                }
            }
        };
    }

    public static synchronized void broadcast(Message message) {
        for (Consumer<Message> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }

    public static List<Message> getMessageList(MessageRepository messageRepository) {
        synchronized (InboxBroadcaster.class) {
            if (messageList == null){
                messageList = messageRepository.findByDeletedFalse();
            }
        }
        return messageList;
    }
}
