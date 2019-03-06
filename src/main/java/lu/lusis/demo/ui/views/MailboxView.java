package lu.lusis.demo.ui.views;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import lu.lusis.demo.ui.MainAppLayout;
import lu.lusis.demo.ui.broadcaster.InboxBroadcaster;

import javax.annotation.PostConstruct;

@Route(value = "mailbox", layout = MainAppLayout.class)
public class MailboxView extends VerticalLayout implements BeforeEnterObserver {

    private final MessageRepository messageRepository;
    private Grid<Message> messageGrid = new Grid<>();

    private Registration registration;

    public MailboxView(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostConstruct
    public void init(){
        //messageGrid.addColumn(Message::isRead);
        messageGrid.addComponentColumn(message -> {
            if (message.isRead()){
                return VaadinIcon.OPEN_BOOK.create();
            } else {
                return VaadinIcon.BOOK.create();
            }

        }).setWidth("70px").setFlexGrow(0);

        messageGrid.addColumn(Message::getSubject).setHeader("Subject");
        messageGrid.addColumn(Message::getFromUser).setHeader("From");

        messageGrid.addComponentColumn(this::editButton);

        messageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        messageGrid.addSelectionListener(selectionEvent -> {
            selectionEvent.getFirstSelectedItem().ifPresent(
                    selectedMessage -> {
                        if (!selectedMessage.isRead()){
                            selectedMessage.setRead(true);
                            messageRepository.save(selectedMessage);
                           // messageGrid.getDataProvider().refreshItem(selectedMessage);
                            InboxBroadcaster.broadcast(selectedMessage);
                        }

                    }

            );

        });
        add(messageGrid);
        setSizeFull();
        expand(messageGrid);


    }

    private Button editButton(Message message) {

        Button button = new Button("Edit", e -> UI.getCurrent().navigate(MessageEditorView.class,message.getId()));

        return button;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
         registration = InboxBroadcaster.register(message -> {
            ui.access(() ->
            messageGrid.getDataProvider().refreshItem(message)
            );
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (registration != null){
            registration.remove();
            registration = null;
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        messageGrid.setItems(InboxBroadcaster.getMessageList(messageRepository));

    }
}
