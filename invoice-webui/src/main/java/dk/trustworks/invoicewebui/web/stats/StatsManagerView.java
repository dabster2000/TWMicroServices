package dk.trustworks.invoicewebui.web.stats;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.admin.components.AdminManagerImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.stats.components.LayoutManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.ADMIN})
@SpringView(name = StatsManagerView.VIEW_NAME)
public class StatsManagerView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(StatsManagerView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private LayoutManager layoutManager;

    public static final String VIEW_NAME = "project_stats";
    public static final String MENU_NAME = "Projects Stats";
    public static final String VIEW_BREADCRUMB = "Projects Statistics";
    public static final FontIcon VIEW_ICON = MaterialIcons.SECURITY;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(layoutManager.init(), VIEW_ICON, MENU_NAME, "Great statistics!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
