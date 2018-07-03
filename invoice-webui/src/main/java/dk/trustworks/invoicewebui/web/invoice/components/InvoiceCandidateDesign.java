package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;

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
public class InvoiceCandidateDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Image imgTop;
    private VerticalLayout container;
    private Button btnEdit;
    private Button btnJoin;
    private Button btnLeave;
    private Panel textContentHolder;
    private Panel photosContentHolder;
    private HorizontalLayout photoContainer;

    public InvoiceCandidateDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Image getImgTop() {
        return imgTop;
    }

    public VerticalLayout getContainer() {
        return container;
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

    public Button getBtnJoin() {
        return btnJoin;
    }

    public Button getBtnLeave() {
        return btnLeave;
    }

    public Panel getTextContentHolder() {
        return textContentHolder;
    }

    public Panel getPhotosContentHolder() {
        return photosContentHolder;
    }

    public HorizontalLayout getPhotoContainer() {
        return photoContainer;
    }

}
