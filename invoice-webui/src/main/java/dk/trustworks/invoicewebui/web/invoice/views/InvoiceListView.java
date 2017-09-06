package dk.trustworks.invoicewebui.web.invoice.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.invoice.components.InvoiceListImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 11/07/2017.
 */
@SpringView(name = InvoiceListView.VIEW_NAME)
public class InvoiceListView extends VerticalLayout implements View, Broadcaster.BroadcastListener {

    public static final String VIEW_NAME = "invoice_list";

    protected static Logger logger = LoggerFactory.getLogger(InvoiceListView.class.getName());

    @Autowired
    private InvoiceListImpl invoiceListComponent;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private LeftMenu leftMenu;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        Broadcaster.register(this);
        mainTemplate.setMainContent(invoiceListComponent);
    }

    @Override
    public void receiveBroadcast(String message) {
        logger.debug("MainWindowImpl.receiveBroadcast");
        logger.debug("message = " + message);
        /*
        int numberOfDrafts = invoiceClient.findByStatus(DRAFT).size();
        System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            invoiceListComponent.reloadData();
            if(numberOfDrafts>0) getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            else getMenuButton4().setCaption("DRAFTS");
        });
        */
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
        invoiceListComponent.loadInvoicesToGrid();
        leftMenu.getMenuItems().get(VIEW_NAME).getParent().foldOut();
    }
}
