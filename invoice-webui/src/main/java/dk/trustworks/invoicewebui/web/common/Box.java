package dk.trustworks.invoicewebui.web.common;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

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
public class Box extends VerticalLayout {
    private VerticalLayout cardHolder;
    private VerticalLayout content;

    public Box() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public VerticalLayout getContent() {
        return content;
    }

}
