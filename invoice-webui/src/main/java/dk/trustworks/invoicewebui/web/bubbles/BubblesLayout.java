package dk.trustworks.invoicewebui.web.bubbles;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.bubbles.components.BubbleForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@SpringComponent
@SpringUI
public class BubblesLayout extends VerticalLayout {

    private UserRepository userRepository;
    private PhotoRepository photoRepository;
    private BubbleRepository bubbleRepository;
    private BubbleMemberRepository bubbleMemberRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow bubblesRow;

    private BubbleForm bubbleForm;

    //@Value("${motherSlackBotToken}")
    //private String motherSlackToken;

    @Value("${bubbleSlackBotToken}")
    private String bubbleSlackToken;

    @Value("${bubbleSlackBotUserToken}")
    private String bubbleBotUserSlackToken;

    private SlackWebApiClient bubbleWebApiClient;

    private SlackWebApiClient bubbleUserBotClient;

    @Autowired
    public BubblesLayout(UserRepository userRepository, BubbleRepository bubbleRepository, PhotoRepository photoRepository, BubbleMemberRepository bubbleMemberRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
    }

    @Transactional
    public BubblesLayout init() {
        System.out.println("BubblesLayout.init");
        bubbleWebApiClient = SlackClientFactory.createWebApiClient(bubbleSlackToken);
        bubbleUserBotClient = SlackClientFactory.createWebApiClient(bubbleBotUserSlackToken);
        bubbleForm = new BubbleForm(userRepository, bubbleRepository, bubbleMemberRepository, photoRepository, bubbleWebApiClient);

        responsiveLayout.removeAllComponents();
        responsiveLayout.addRow(bubbleForm.getNewBubbleButton());
        responsiveLayout.addRow(bubbleForm.getDialogRow());

        bubblesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadBubbles();
        return this;
    }

    private void loadBubbles() {
        bubblesRow.removeAllComponents();
        //User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        User user = userRepository.findByUsername("hans.lassen");

        for (Bubble bubble : bubbleRepository.findBubblesByActiveTrueOrderByCreated()) {
            BubblesDesign bubblesDesign = new BubblesDesign();

            bubblesDesign.getLblHeading().setValue(bubble.getName());
            bubblesDesign.getLblDescription().setValue(bubble.getDescription());

            bubblesDesign.getBtnLeave().setVisible(false);
            bubblesDesign.getBtnEdit().setVisible(false);
            bubblesDesign.getBtnApply().setVisible(false);
            bubblesDesign.getBtnJoin().setVisible(false);

            if(bubble.getApplication().equals("Open")) {
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(true);
            }
            if(bubble.getApplication().equals("Closed")) {
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
            }
            if(bubble.getApplication().equals("Invitation")) {
                bubblesDesign.getBtnApply().setVisible(true);
                bubblesDesign.getBtnJoin().setVisible(false);
            }

            bubblesDesign.getBtnEdit().addClickListener(event -> bubbleForm.editFormAction(bubble));
            bubblesDesign.getBtnApply().addClickListener(event -> {
                //bubble.getUser().getSlackusername()
                ChatPostMessageMethod applyMessage = new ChatPostMessageMethod(user.getSlackusername(), "Hi "+bubble.getUser().getFirstname()+", *"+user.getUsername()+"* would like to join your bubble "+bubble.getName()+"!");
                applyMessage.setAs_user(true);
                bubbleUserBotClient.postMessage(applyMessage);
                Notification.show("You have now applied for membership. The bubble owner will get back to you soon!", Notification.Type.ASSISTIVE_NOTIFICATION);
            });

            bubblesDesign.getBtnJoin().addClickListener(event -> {
                bubbleMemberRepository.save(new BubbleMember(user, bubble));
                try {
                    bubbleWebApiClient.inviteUserToGroup(bubbleWebApiClient.getGroupInfo(bubble.getSlackchannel()).getId(), user.getSlackusername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Page.getCurrent().reload();
            });

            bubblesDesign.getBtnLeave().addClickListener(event -> {
                bubbleMemberRepository.delete(bubbleMemberRepository.findByBubbleAndMember(bubble, user));
                try {
                    bubbleWebApiClient.kickUserFromGroup(bubbleWebApiClient.getGroupInfo(bubble.getSlackchannel()).getId(), user.getSlackusername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Page.getCurrent().reload();
            });

            if(bubbleMemberRepository.findByBubbleAndMember(bubble, user) != null) {
                bubblesDesign.getBtnLeave().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
            }
            if(bubble.getUser().getUuid().equals(user.getUuid())) {
                bubblesDesign.getBtnEdit().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
                bubblesDesign.getBtnLeave().setVisible(false);
            }

            bubblesDesign.getPhotoContainer().addComponent(getMemberImage(bubble.getUser(), true));
            for (BubbleMember member : bubbleMemberRepository.findByBubble(bubble)) {
                if(member.getMember().getUuid().equals(bubble.getUser().getUuid())) continue;
                Image image = getMemberImage(member.getMember(), false);
                bubblesDesign.getPhotoContainer().addComponent(image);
            }

            Photo bubblephoto = photoRepository.findByRelateduuid(bubble.getUuid());
            bubblesDesign.getImgTop().setSource(
                    new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(bubblephoto.getPhoto()),
                    bubble.getName()+System.currentTimeMillis()+".jpg"));
            if(bubble.getUser().getUuid().equals(user.getUuid())) {
                bubblesDesign.getImgTop().addClickListener(event -> bubbleForm.editPhotoAction(bubble));
            }
            bubblesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(bubblesDesign);
        }
    }

    private Image getMemberImage(User member, boolean isOwner) {
        Photo photo = photoRepository.findByRelateduuid(member.getUuid());

        Image image = new Image(null,
                new StreamResource((StreamResource.StreamSource) () ->
                        new ByteArrayInputStream(photo.getPhoto()),
                        member.getUsername()+System.currentTimeMillis()+".jpg"));
        if(isOwner) image.setStyleName("img-circle-gold");
        else image.setStyleName("img-circle");
        image.setWidth(75, Unit.PIXELS);
        image.setHeight(75, Unit.PIXELS);
        return image;
    }
}
