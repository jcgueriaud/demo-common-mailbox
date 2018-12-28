package lu.lusis.demo.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import lu.lusis.demo.ui.MainAppLayout;
import lu.lusis.demo.ui.broadcaster.InboxBroadcaster;
import lu.lusis.demo.utils.HasLogger;

import javax.annotation.PostConstruct;
import java.util.Locale;

@PageTitle("Trash Mail Box")
@Route(value = "trash",layout = MainAppLayout.class)
public class CommonTrashView extends VerticalLayout  implements BeforeEnterObserver, HasLogger {

    private Grid<Message> messageGrid = new Grid<>();
    private ListDataProvider<Message> dataProvider;

    private final MessageRepository messageRepository;

    private Registration inboxBroadcasterRegistration;

    public CommonTrashView(MessageRepository messageRepository) {
        UI.getCurrent().setLocale(Locale.FRANCE);
        this.messageRepository = messageRepository;
        setSizeFull();

        expand(messageGrid);
        add(messageGrid);
    }

    @PostConstruct
    private void initGrid(){
        boolean isTrashContext = false;
        CommonMailViewUtils.initGrid(messageRepository,messageGrid, isTrashContext);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        dataProvider = new ListDataProvider<>(InboxBroadcaster.getMessageList(messageRepository));
        messageGrid.setDataProvider(dataProvider);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        getLogger().debug(this +"onAttach");
        inboxBroadcasterRegistration = InboxBroadcaster.register(updatedMessage ->{
            getLogger().debug(this +"InboxBroadcaster.register");
            ui.access(() -> {
                getLogger().debug(this +"ui.access ID = " + updatedMessage.getId());
                if (updatedMessage.isDeleted()){
                    ((ListDataProvider<Message>) (messageGrid.getDataProvider())).getItems().remove(updatedMessage);
                    messageGrid.getDataProvider().refreshAll();
                } else {
                    messageGrid.getDataProvider().refreshItem(updatedMessage);
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        getLogger().debug("onDetach" + this.toString());
        inboxBroadcasterRegistration.remove();
        inboxBroadcasterRegistration = null;
    }
}
