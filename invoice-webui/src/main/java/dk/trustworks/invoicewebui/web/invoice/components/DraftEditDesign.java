package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

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
public class DraftEditDesign extends VerticalLayout {
    protected Label lblInvoiceHeadline;
    protected Button btnCreateInvoice;
    protected Button btnSave;
    protected TextField txtClientname;
    protected TextField txtStreetname;
    protected TextField txtZipCity;
    protected TextField txtCvr;
    protected TextField txtEan;
    protected TextField txtAttention;
    protected DateField dfInvoiceDate;
    protected Label lblContractReference;
    protected Label lblProjectReference;
    protected TextField txtSpecificDescription;
    protected Button btnCopyDescription;
    protected GridLayout gridInvoiceItems;
    protected Label lblSumNoTax;
    protected Label lblTax;
    protected Label lblSumWithTax;

    public DraftEditDesign() {
        Design.read(this);
    }
}
