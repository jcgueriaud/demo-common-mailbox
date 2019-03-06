package lu.lusis.demo.ui;

import com.github.appreciated.app.layout.behaviour.AppLayout;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import lu.lusis.demo.ui.views.CommonMailboxView;
import lu.lusis.demo.ui.views.CommonTrashView;
import lu.lusis.demo.ui.views.MailboxView;


@PWA(name="Common Mailbox", shortName = "commonMB", description = "Common Mail box ")
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@HtmlImport("styles/shared-styles.html")
public class MainAppLayout extends AppLayoutRouterLayout {

    @Override
    public AppLayout createAppLayoutInstance() {
        return AppLayoutBuilder
                .get(Behaviour.LEFT_HYBRID)
                .withTitle("Demo app")
                .withAppBar(AppBarBuilder
                        .get()
                        .build())
                .withAppMenu(LeftAppMenuBuilder
                        .get()
                        .add(new LeftNavigationComponent("Inbox", VaadinIcon.INBOX.create(), CommonMailboxView.class))
                        .add(new LeftNavigationComponent("Trash", VaadinIcon.TRASH.create(), CommonTrashView.class))
                        .add(new LeftNavigationComponent("Mailbox", VaadinIcon.MAILBOX.create(), MailboxView.class))
                        .build())
                .build();
    }

}