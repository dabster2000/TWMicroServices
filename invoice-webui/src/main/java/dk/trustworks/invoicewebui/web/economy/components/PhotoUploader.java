package dk.trustworks.invoicewebui.web.economy.components;

import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.PhotoGlobal;
import dk.trustworks.invoicewebui.model.enums.PhotoGlobalType;
import dk.trustworks.invoicewebui.repositories.PhotoGlobalRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.fields.MTextField;
import server.droporchoose.UploadComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

public class PhotoUploader {

    private final PhotoService photoService;
    private MTextField relatedUuid;

    public PhotoUploader(PhotoService photoService) {
        this.photoService = photoService;
    }

    public Card init() {
        Card card = new Card();

        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Sizeable.Unit.PIXELS);
        uploadComponent.setCaption("File upload");

        card.getLblTitle().setValue("Upload photo");
        relatedUuid = new MTextField("Related UUID");
        card.getContent().addComponent(relatedUuid);
        card.getContent().addComponent(uploadComponent);

        return card;
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("New photo uploaded: " + fileName, HUMANIZED_MESSAGE);
        try {
            photoService.save(new Photo(UUID.randomUUID().toString(), relatedUuid.getValue(), Files.readAllBytes(file)));
        } catch (IOException e) {
            uploadFailed(fileName, file);
        }
    }

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
