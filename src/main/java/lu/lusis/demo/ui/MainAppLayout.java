package lu.lusis.demo.ui;

import com.github.appreciated.app.layout.behaviour.AppLayout;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.app.layout.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.notification.component.AppBarNotificationButton;
import com.github.appreciated.app.layout.notification.entitiy.DefaultNotification;
import com.github.appreciated.app.layout.notification.entitiy.Priority;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import lu.lusis.demo.ui.views.CommonMailboxView;
import lu.lusis.demo.ui.views.CommonTrashView;

import static com.github.appreciated.app.layout.notification.entitiy.Priority.MEDIUM;


@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@HtmlImport("styles/shared-styles.html")
public class MainAppLayout extends AppLayoutRouterLayout {
    private Behaviour variant;
    private DefaultNotificationHolder notificationHolder;
    private DefaultBadgeHolder badgeHolder = new DefaultBadgeHolder();

    @Override
    public AppLayout createAppLayoutInstance() {
        if (variant == null) {
            variant = Behaviour.LEFT_HYBRID;
            notificationHolder = new DefaultNotificationHolder(newStatus -> {
            });
            getUI().ifPresent(ui -> ui.access(() -> {
                addNotification(MEDIUM, "Test");
            }));
        }
        return AppLayoutBuilder
                .get(variant)
                .withTitle("Demo app")
                .withAppBar(AppBarBuilder
                        .get()
                        .add(new AppBarNotificationButton(VaadinIcon.BELL, notificationHolder))
                        .build())
                .withAppMenu(LeftAppMenuBuilder
                        .get()
                        .add(new LeftNavigationComponent("Inbox", VaadinIcon.INBOX.create(), CommonMailboxView.class))
                        .add(new LeftNavigationComponent("Trash", VaadinIcon.TRASH.create(), CommonTrashView.class))
                        .build())
                .build();
    }


    private void addNotification(Priority priority, String message) {
        notificationHolder.addNotification(new DefaultNotification(
                "Title" + badgeHolder.getCount(),
                message,
                priority
        ));
        badgeHolder.increase();
    }

}