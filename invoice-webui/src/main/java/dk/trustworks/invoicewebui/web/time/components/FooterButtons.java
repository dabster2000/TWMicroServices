package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class FooterButtons extends HorizontalLayout {
    private Button btnAddTask;
    private Button btnCopyWeek;

    public FooterButtons() {
        Design.read(this);
    }

    public Button getBtnAddTask() {
        return btnAddTask;
    }

    public Button getBtnCopyWeek() {
        return btnCopyWeek;
    }

}
