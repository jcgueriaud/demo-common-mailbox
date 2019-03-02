package lu.lusis.demo.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import lu.lusis.demo.ui.MainAppLayout;
import lu.lusis.demo.ui.broadcaster.InboxBroadcaster;
import lu.lusis.demo.utils.HasLogger;
import org.vaddon.CustomMediaQuery;

import javax.annotation.PostConstruct;
import java.util.Locale;

@PageTitle("Common Mail Box")
@Route(value = "",layout = MainAppLayout.class)
public class CommonMailboxView extends VerticalLayout implements BeforeEnterObserver, HasLogger {


    private Grid<Message> messageGrid = new Grid<>();
    private ListDataProvider<Message> dataProvider;

    private final MessageRepository messageRepository;

    private Registration inboxBroadcasterRegistration;
    private final CustomMediaQuery customMediaQuery1200;

    public CommonMailboxView(MessageRepository messageRepository) {
        UI.getCurrent().setLocale(Locale.FRANCE);
        this.messageRepository = messageRepository;
        setSizeFull();
        customMediaQuery1200 = new CustomMediaQuery(this::toggleColumnCreationDate);
        expand(messageGrid);
        add(messageGrid, customMediaQuery1200);
    }


    @PostConstruct
    private void initGrid(){
        boolean isTrashContext = false;
        CommonMailViewUtils.initGrid(messageRepository,messageGrid, isTrashContext);
        customMediaQuery1200.setQuery("(min-width: 1200px)");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        dataProvider = new ListDataProvider<>(InboxBroadcaster.getMessageList(messageRepository));
        messageGrid.setDataProvider(dataProvider);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        getLogger().debug("{} onAttach", this);
        inboxBroadcasterRegistration = InboxBroadcaster.register(updatedMessage ->{
            getLogger().debug("{} InboxBroadcaster.Action", this);
            ui.access(() -> {
                getLogger().debug("{} ui.access ID = {}",this, updatedMessage.getId());
                if (updatedMessage.isDeleted() && dataProvider.getItems().contains(updatedMessage)){
                    dataProvider.getItems().remove(updatedMessage);
                } else if (!updatedMessage.isDeleted() && !dataProvider.getItems().contains(updatedMessage)){
                    dataProvider.getItems().add(updatedMessage);
                } else {
                    messageGrid.getDataProvider().refreshItem(updatedMessage);
                }
                messageGrid.getDataProvider().refreshAll();
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        getLogger().debug("onDetach {}", this);
        inboxBroadcasterRegistration.remove();
        inboxBroadcasterRegistration = null;
    }

    private void toggleColumnCreationDate(Boolean visible) {
        messageGrid.getColumnByKey("creationDate").setVisible(visible);
    }
}
