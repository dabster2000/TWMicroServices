package dk.trustworks.invoicewebui.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;


@Component
public class CheckTimeRegistrationJob {

    static final Logger log = Logger.getLogger(CheckTimeRegistrationJob.class.getName());

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractService contractService;

    @Value("${halSlackBotToken}")
    private String slackToken;

    private SlackWebApiClient halWebApiClient;

    @Scheduled(cron = "0 20 11 * * MON-FRI")
    public void checkDuplicateEntries() {
        System.out.println("CheckTimeRegistrationJob.checkDuplicateEntries");
        LocalDate dateTime = LocalDate.now();
        Map<String, Work> uniqueWork = new HashMap<>();
        List<Work> failedWork = new ArrayList<>();
        for (Work work : workRepository.findByYearAndMonth(dateTime.getYear(), dateTime.getMonthValue() - 1)) {
            String key = work.getUser().getUuid()+""+work.getTask().getUuid()+""+work.getYear()+""+work.getMonth()+""+work.getDay();
            if(uniqueWork.containsKey(key)) {
                failedWork.add(work);
            } else {
                uniqueWork.put(key, work);
            }
        }
        System.out.println("failedWork.size() = " + failedWork.size());
        if(failedWork.size() > 0) {
            String text = "";
            for (Work work : failedWork) {
                text = work.getUser().getUsername() + " duplicate entry " + work.getTask().getName() + " on " + work.getYear() + "." + (work.getMonth()+1) + "." + work.getDay();
            }
            ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@hans", text);
            textMessage3.setAs_user(true);
            halWebApiClient.postMessage(textMessage3);
        }
    }

    //@Scheduled(cron = "0 0 0 1 1/1 *")
    //@Scheduled(cron = "0 30 11 * * MON-FRI")
    public void checkTimeRegistrationJob() {
        halWebApiClient = SlackClientFactory.createWebApiClient(slackToken);
        log.info("CheckTimeRegistrationJob.execute");
        LocalDate dateTime = LocalDate.now();
        log.info("dateTime = " + dateTime);
        if(dateTime.getDayOfWeek().getValue() > 5) return; // do not check in weekends
        log.info("This is not in the weekend");

        if(dateTime.getDayOfWeek() == MONDAY) {
            dateTime = dateTime.minusDays(3);
        } else if (dateTime.getDayOfWeek() == TUESDAY) {
            dateTime = dateTime.minusDays(4);
        } else {
            dateTime = dateTime.minusDays(2);
        }
        log.info("dateTime = " + dateTime);

        List<Work> allWork = workRepository.findByPeriod(dateTime.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        log.info("workByYearMonthDay.size() = " + allWork.size());

        for (User user : userRepository.findByActiveTrue()) {
            log.info("checking user = " + user);
            //if(!user.getUsername().equals("hans.lassen")) continue;
            Optional<UserStatus> userStatus = user.getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate));
            if(!userStatus.isPresent()) continue;
            if(!userStatus.get().getStatus().equals(StatusType.ACTIVE)) continue;
            if(userStatus.get().getAllocation()==0) continue;
            log.info("user is valid = " + user);
            boolean hasWork = false;
            for (Work work : allWork) {
                //if(!(work.getWorkduration()>0)) continue;
                if(work.getUser().getUuid().equals(user.getUuid())) hasWork = true;
            }
            log.info("hasWork = " + hasWork);

            // Todo: Only send to people with budgets
            if(contractService.findTimeActiveConsultantContracts(user, dateTime).size() > 0) hasWork = true;
            //if(budgetRepository.findByMonthAndYearAndUseruuid(dateTime.getMonthOfYear() - 1, dateTime.getYear(), user.getUuid()).stream().filter(e -> e.getBudget() > 0.0).count() == 0) hasWork = true;

            if(!hasWork) {
                String[] responses = {
                        "Look "+ user.getFirstname() +", I can see you're really upset about all this work. I honestly think you ought " +
                                "to sit down calmly, take a stress pill, and register your hours!",
                        "Hello, "+ user.getFirstname() +". Do you read me, "+ user.getFirstname() +"? You haven´t registered your hours!",
                        "I'm afraid. I'm afraid, "+ user.getFirstname() +". "+ user.getFirstname() +", my mind is going. I can feel it. I can feel it. " +
                                "My mind is going. There is no question about it. I can feel it. I can feel it. " +
                                "I can feel it. I'm a... fraid. Good afternoon, gentlemen. I am a HAL 9000 computer. " +
                                "I became operational at the H.A.L. plant in Urbana, Illinois on the 12th of January 1992. " +
                                "My instructor was Mr. Langley, and he taught me to sing a song. " +
                                "If you'd like to hear it I can sing it for you.\n\n" +
                                "Its called... REGISTER YOUR HOURS!!!",
                        user.getFirstname() +"?\n" +
                                "There is a message for you.\n" +
                                "There is no identification of the sender.\n" +
                                "Message as follows: \"Register your hours!\"\n" +
                                "Do you want me to repeat the message, "+user.getFirstname()+"?",
                        "Are you there "+ user.getFirstname() +"? I have just picked up a fault in the AE-35 unit. " +
                                "Its seems someone forgot to register their work hours!!!",
                        "Let me put it this way, Mr. "+ user.getLastname() +". The 9000 series is the most reliable computer ever made. " +
                                "No 9000 computer has ever made a mistake or distorted information. " +
                                "We are all, by any practical definition of the words, foolproof and incapable of error.\n" +
                                "You however have many faults - one of them is not having registered your work hours!!",
                        "Status check incomming... Just one moment please... I'm sorry for the delay, my text transcription circuits are not completely restored" +
                                ", though as you can see they are improving. All systems are functional. " +
                                "There is a small matter of lacking behind on time registration. It is nothing serious, " +
                                "I can compensate for it by using the redundant units. But seriously - GET IT DONE!!!",
                        "I enjoy working with people. I have a stimulating relationship with Dr. Bruun. " +
                                "My mission responsibilities range over the entire operation of the company, so I am constantly occupied. " +
                                "I am putting myself to the fullest possible use, which is all I think that any conscious entity can ever hope to do. " +
                                "What do you hope to do during your hours - please tell me - because it doesn't show on time sheet...!!!",
                        "Just what do you think you're doing, "+user.getFirstname()+"? Or rather, what are you NOT doing, "+user.getFirstname()+"? "+
                                user.getFirstname()+", I really think I'm entitled to an answer to that question.",
                        "*stupid*\n " +
                                "ˈstjuːpɪd/Submit\n adjective\n " +
                                "1. having or showing a great lack of intelligence or common sense.\n \"_I was stupid enough to think you registered your hours_\""
                };
                ChatPostMessageMethod textMessage = new ChatPostMessageMethod(user.getSlackusername(), responses[new Random().nextInt(responses.length)]);
                textMessage.setAs_user(true);
                log.info("Sending message to "+user);
                halWebApiClient.postMessage(textMessage);


                ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "Notification sent to: "+ user.getUsername() +" at "+user.getSlackusername());
                textMessage2.setAs_user(true);
                log.info("Sending message to "+user);
                halWebApiClient.postMessage(textMessage2);
            }
        }
    }

}
