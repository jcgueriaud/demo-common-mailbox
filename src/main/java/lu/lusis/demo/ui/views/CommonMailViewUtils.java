package lu.lusis.demo.ui.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridContextMenu;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import lu.lusis.demo.ui.broadcaster.InboxBroadcaster;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

class CommonMailViewUtils {

    static void initGrid(MessageRepository messageRepository, Grid<Message> messageGrid, boolean isTrashContext){

        GridSelectionModel<Message> selectionModel =  messageGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        messageGrid.addComponentColumn((message -> message.isImportant()? VaadinIcon.ARROW_UP.create():new Span()))
                .setFlexGrow(0).setWidth("40px");
        messageGrid.addComponentColumn((message -> message.isRead()?VaadinIcon.OPEN_BOOK.create():VaadinIcon.BOOK.create()))
                .setFlexGrow(0).setWidth("40px");
        messageGrid.addColumn(Message::getFromUser).setHeader("From").setSortProperty("fromUser");
        messageGrid.addColumn(Message::getSubject).setHeader("Subject").setSortProperty("subject");
        messageGrid.addColumn(Message::getId).setHeader("ID").setSortProperty("id").setFlexGrow(0).setWidth("40px");
        messageGrid.addColumn(
                new LocalDateTimeRenderer<>(Message::getCreationDate,  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)))
                .setHeader("Received").setFlexGrow(0).setWidth("200px").setSortProperty("id").setKey("creationDate");
        selectionModel.addSelectionListener(event -> {
            event.getAllSelectedItems().stream().filter(message -> !message.isRead()).filter(m -> (((ListDataProvider<Message>) messageGrid.getDataProvider()).getItems().contains(m)))
                    .forEach(
                    message -> {
                        message.setRead(true);
                        messageRepository.save(message);
                        InboxBroadcaster.broadcast(message);
                        //publisher.onNext(message);
                        //messageGrid.getDataProvider().refreshItem(message);
                    }
            );
        });

        GridContextMenu<Message> messageGridContextMenu = messageGrid.addContextMenu();

        messageGridContextMenu.addItem("Mark As Important",
                e ->
                        messageGrid.getSelectedItems().stream().filter(m -> (((ListDataProvider<Message>) messageGrid.getDataProvider()).getItems().contains(m)))
                                .filter(message -> !message.isImportant())
                                .forEach(
                                        message -> {
                                            message.setImportant(true);
                                            messageRepository.save(message);
                                            InboxBroadcaster.broadcast(message);
                                        }
                                )
        );

        messageGridContextMenu.addItem("Mark As Not Important",
                e ->
                        messageGrid.getSelectedItems().stream().filter(m -> (((ListDataProvider<Message>) messageGrid.getDataProvider()).getItems().contains(m)))
                                .filter(Message::isImportant)
                                .forEach(
                                        message -> {
                                            message.setImportant(false);
                                            messageRepository.save(message);
                                            InboxBroadcaster.broadcast(message);
                                        }
                                )
        );

        if (isTrashContext){

            messageGridContextMenu.addItem("Restore",
                    e ->
                            messageGrid.getSelectedItems().stream().filter(m -> (((ListDataProvider<Message>) messageGrid.getDataProvider()).getItems().contains(m)))
                                    .forEach(
                                            message -> {
                                                message.setDeleted(false);
                                                messageRepository.save(message);
                                                InboxBroadcaster.addMessage(message);
                                                InboxBroadcaster.broadcast(message);
                                            }
                                    )
            );
        } else {
            messageGridContextMenu.addItem("Trash",
                    e ->
                            messageGrid.getSelectedItems().stream().filter(m -> (((ListDataProvider<Message>) messageGrid.getDataProvider()).getItems().contains(m)))
                                    .forEach(
                                            message -> {
                                                message.setDeleted(true);
                                                messageRepository.save(message);
                                                InboxBroadcaster.deleteMessage(message);
                                                InboxBroadcaster.broadcast(message);
                                            }
                                    )
            );
        }
    }
}
