package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.PropertyId;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
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
public class NewProjectDesign extends VerticalLayout {
    private TextField txtProjectName;
    @PropertyId("name")
    private ComboBox<dk.trustworks.invoicewebui.model.Client> cbClients;
    private ComboBox<dk.trustworks.invoicewebui.model.Clientdata> cbClientdatas;
    private Button btnCreate;
    private Button btnCancel;

    public NewProjectDesign() {
        Design.read(this);
    }

    public TextField getTxtProjectName() {
        return txtProjectName;
    }

    public ComboBox<dk.trustworks.invoicewebui.model.Client> getCbClients() {
        return cbClients;
    }

    public ComboBox<dk.trustworks.invoicewebui.model.Clientdata> getCbClientdatas() {
        return cbClientdatas;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

}
