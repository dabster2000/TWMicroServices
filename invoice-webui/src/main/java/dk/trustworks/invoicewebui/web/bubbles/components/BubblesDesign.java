package dk.trustworks.invoicewebui.web.bubbles.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.*;
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
public class BubblesDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Image imgTop;
    private VerticalLayout container;
    private Label lblHeading;
    private HorizontalLayout gaugeContainer;
    private Button btnEdit;
    private Button btnJoin;
    private Button btnLeave;
    private Button btnApply;
    private Panel textContentHolder;
    private Label lblDescription;
    private Panel photosContentHolder;
    private HorizontalLayout photoContainer;

    public BubblesDesign() {
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

    public Label getLblHeading() {
        return lblHeading;
    }

    public HorizontalLayout getGaugeContainer() {
        return gaugeContainer;
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

    public Button getBtnApply() {
        return btnApply;
    }

    public Panel getTextContentHolder() {
        return textContentHolder;
    }

    public Label getLblDescription() {
        return lblDescription;
    }

    public Panel getPhotosContentHolder() {
        return photosContentHolder;
    }

    public HorizontalLayout getPhotoContainer() {
        return photoContainer;
    }

}
