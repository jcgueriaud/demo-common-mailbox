package lu.lusis.demo.ui.broadcaster;

import com.vaadin.flow.shared.Registration;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class InboxBroadcaster {

    private static List<Message> deletedMessageList;

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
                    // we can clear the lists
                    messageList = null;
                    deletedMessageList = null;
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
                messageList = Collections.synchronizedList(messageRepository.findByDeletedFalse());
            }
        }
        return messageList;
    }


    public static List<Message> getDeletedMessageList(MessageRepository messageRepository) {
        synchronized (InboxBroadcaster.class) {
            if (deletedMessageList == null){
                deletedMessageList = Collections.synchronizedList(messageRepository.findByDeletedTrue());
            }
        }
        return deletedMessageList;
    }


    public static void deleteMessage(Message message){
        synchronized (InboxBroadcaster.class) {
            if(messageList!=null)messageList.remove(message);
            if(deletedMessageList!=null)deletedMessageList.add(message);
        }
    }

    public static void addMessage(Message message){
        synchronized (InboxBroadcaster.class) {
            if(messageList!=null)messageList.add(message);
            if(deletedMessageList!=null)deletedMessageList.remove(message);
        }
    }
}
