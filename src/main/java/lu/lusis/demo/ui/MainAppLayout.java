package lu.lusis.demo.ui;

import com.github.appreciated.app.layout.behaviour.AppLayout;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.notification.component.AppBarNotificationButton;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import lu.lusis.demo.ui.views.CommonMailboxView;
import lu.lusis.demo.ui.views.CommonTrashView;


@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@HtmlImport("styles/shared-styles.html")
public class MainAppLayout extends AppLayoutRouterLayout {
    private Behaviour variant;
    private DefaultNotificationHolder notifications;

    @Override
    public AppLayout createAppLayoutInstance() {
        if (variant == null) {
            variant = Behaviour.LEFT_HYBRID;
            notifications = new DefaultNotificationHolder(newStatus -> {
            });
        }
        return AppLayoutBuilder
                .get(variant)
                .withTitle("Application de démonstration")
                .withAppBar(AppBarBuilder
                        .get()
                        .add(new AppBarNotificationButton(VaadinIcon.BELL, notifications))
                        .build())
                .withAppMenu(LeftAppMenuBuilder
                        .get()
                        .add(new LeftNavigationComponent("Inbox", VaadinIcon.INBOX.create(), CommonMailboxView.class))
                        .add(new LeftNavigationComponent("Trash", VaadinIcon.TRASH.create(), CommonTrashView.class))
                        .build())
                .build();
    }
}