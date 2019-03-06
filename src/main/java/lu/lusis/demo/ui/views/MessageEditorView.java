package lu.lusis.demo.ui.views;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import lu.lusis.demo.ui.MainAppLayout;
import lu.lusis.demo.ui.broadcaster.InboxBroadcaster;

import javax.annotation.PostConstruct;

@Route(value = "message/edit", layout = MainAppLayout.class)
public class MessageEditorView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final MessageRepository messageRepository;

    private TextField subjectField = new TextField("Subject");

    private Button saveButton = new Button("Save");
    private Button cancelButton = new Button("Cancel");

    private Binder<Message> binder = new Binder<>();
    private Message message;

    public MessageEditorView(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostConstruct
    public void init(){

        binder.forField(subjectField).asRequired()
                .withValidator(subject -> subject.length()>3,"Subject is not valid")
                .bind(Message::getSubject, Message::setSubject);

        cancelButton.addClickListener(this::cancel);
        saveButton.addClickListener(this::save);

        add(subjectField,saveButton,cancelButton);

    }

    private void save(ClickEvent<Button> buttonClickEvent) {

        if (binder.writeBeanIfValid(message)){
            messageRepository.save(message);
            InboxBroadcaster.broadcast(message);

            UI.getCurrent().navigate(MailboxView.class);
        }

    }

    private void cancel(ClickEvent<Button> buttonClickEvent) {
        UI.getCurrent().navigate(MailboxView.class);
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer id) {
        message = InboxBroadcaster.findById(messageRepository,id).orElse(new Message());

        binder.readBean(message);
    }
}
