package dk.trustworks.invoicewebui.web.contracts.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.annotations.PropertyId;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
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
public class ContractFormDesign extends VerticalLayout {
    private CssLayout cardHolder;
    private Label lblTitle;
    private VerticalLayout container;
    private ComboBox<dk.trustworks.invoicewebui.model.enums.ContractType> cbType;
    @PropertyId("name")
    private CheckBoxGroup<dk.trustworks.invoicewebui.model.Project> chkProjects;
    private DateField dfFrom;
    private DateField dfTo;
    private TextField txtAmount;
    private Button btnUpdate;
    private Button btnCreate;
    private Button btnEdit;

    public ContractFormDesign() {
        Design.read(this);
    }

    public CssLayout getCardHolder() {
        return cardHolder;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public VerticalLayout getContainer() {
        return container;
    }

    public ComboBox<dk.trustworks.invoicewebui.model.enums.ContractType> getCbType() {
        return cbType;
    }

    public CheckBoxGroup<dk.trustworks.invoicewebui.model.Project> getChkProjects() {
        return chkProjects;
    }

    public DateField getDfFrom() {
        return dfFrom;
    }

    public DateField getDfTo() {
        return dfTo;
    }

    public TextField getTxtAmount() {
        return txtAmount;
    }

    public Button getBtnUpdate() {
        return btnUpdate;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

}
