package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
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
public class UserDetailsCardDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private VerticalLayout formLayout;
    private TextField txtName;
    private DateField dfBirthday;
    private TextField txtStreet;
    private TextField txtPostal;
    private TextField txtCity;
    private TextField txtPhone;
    private Button btnUpdate;

    public UserDetailsCardDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public VerticalLayout getFormLayout() {
        return formLayout;
    }

    public TextField getTxtName() {
        return txtName;
    }

    public DateField getDfBirthday() {
        return dfBirthday;
    }

    public TextField getTxtStreet() {
        return txtStreet;
    }

    public TextField getTxtPostal() {
        return txtPostal;
    }

    public TextField getTxtCity() {
        return txtCity;
    }

    public TextField getTxtPhone() {
        return txtPhone;
    }

    public Button getBtnUpdate() {
        return btnUpdate;
    }

}
