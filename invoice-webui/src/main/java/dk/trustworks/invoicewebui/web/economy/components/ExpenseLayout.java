package dk.trustworks.invoicewebui.web.economy.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.utils.EconomicExcelUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDate;

import static com.vaadin.ui.Notification.Type.*;

@SpringComponent
@SpringUI
public class ExpenseLayout extends VerticalLayout {

    private final EconomicExcelUtility economicExcelUtility;

    private DateField date;

    @Autowired
    public ExpenseLayout(EconomicExcelUtility economicExcelUtility, GlobalPhotoUploader globalPhotoUploader, NotificationManager notificationManager) {
        this.economicExcelUtility = economicExcelUtility;

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        Card card = new Card();

        date = new DateField("Date");
        date.setDateFormat("yyyy-MM");
        date.setSizeFull();
        date.setRequiredIndicatorVisible(true);
        date.setResolution(DateResolution.MONTH);
        date.setValue(LocalDate.now());
        card.getContent().addComponent(date);
/*
        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Unit.PIXELS);
        uploadComponent.setCaption("File upload");
*/
        //card.getLblTitle().setValue("Upload excel");
        //card.getContent().addComponent(uploadComponent);

        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(card);
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(globalPhotoUploader.init());
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(notificationManager.init());
        this.addComponent(responsiveLayout);
    }

    @Transactional
    public ExpenseLayout init() {

        return this;
    }
/*
    private void uploadReceived(String fileName, Path file) {
        Notification.show("New expense report added: " + fileName, HUMANIZED_MESSAGE);
        try {
            expenseRepository.deleteByPeriod(date.getValue().withDayOfMonth(1));
            Table<LocalDate, ExcelExpenseType, Double> expenses = economicExcelUtility.getExpenses(Files.readAllBytes(file));
            for (Table.Cell<LocalDate, ExcelExpenseType, Double> cellSet : expenses.cellSet()) {
                expenseRepository.save(new Expense(date.getValue().withDayOfMonth(1), cellSet.getColumnKey(), cellSet.getValue()));
            }
        } catch (IOException e) {
            uploadFailed(fileName, file);
        }
    }
*/
    private void uploadStarted(String fileName) {
        Notification.show("Upload started: " + fileName, HUMANIZED_MESSAGE);
    }

    private void uploadProgress(String fileName, long readBytes, long contentLength) {
        Notification.show(String.format("Progress: %s : %d/%d", fileName, readBytes, contentLength), TRAY_NOTIFICATION);
    }

    private void uploadFailed(String fileName, Path file) {
        Notification.show("Upload failed: " + fileName, ERROR_MESSAGE);
    }
}
