package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.IncomeForecast;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.IncomeForcastRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CountEmployeesJob {

    private static final Logger log = LoggerFactory.getLogger(CountEmployeesJob.class);

    private final IncomeForcastRepository incomeForcastRepository;

    private final WorkService workService;

    private final ContractService contractService;

    private final UserService userService;

    private Map<LocalDate, List<User>> usersByLocalDate;

    private final LocalDate startDate;

    private final List<Double> dailyForecast;

    private final List <Integer> dailyPeopleForecast;

    @Autowired
    public CountEmployeesJob(IncomeForcastRepository incomeForcastRepository, WorkService workService, ContractService contractService, UserService userService) {
        this.incomeForcastRepository = incomeForcastRepository;
        this.workService = workService;
        this.contractService = contractService;
        this.userService = userService;
        usersByLocalDate = new HashMap<>();
        startDate = LocalDate.of(2014, 2, 1);
        dailyForecast = new ArrayList<>();
        dailyPeopleForecast = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        countEmployees();
        try {
            //forecastIncome();
            //forecastPeople();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 4 5 1/1 ?")
    public void countEmployees() {
        log.info("CountEmployeesJob.countEmployees");
        usersByLocalDate.clear();
        for (User user : userService.findAll()) {
            LocalDate tryDate = startDate;
            StatusType currentStatus = StatusType.TERMINATED;
            while(tryDate.isBefore(LocalDate.now().plusMonths(2))) {
                if(!usersByLocalDate.containsKey(tryDate)) {
                    usersByLocalDate.put(tryDate, new ArrayList<>());
                }
                for (UserStatus userStatus : user.getStatuses().stream().sorted(Comparator.comparing(UserStatus::getStatusdate).reversed()).collect(Collectors.toList())) {
                    if(userStatus.getStatusdate().isEqual(tryDate) || userStatus.getStatusdate().isBefore(tryDate)) {
                        currentStatus = userStatus.getStatus();
                        break;
                    }
                }
                if(currentStatus.equals(StatusType.ACTIVE)) {
                    usersByLocalDate.get(tryDate).add(user);
                }
                tryDate = tryDate.plusMonths(1);
            }
        }
    }

    // http://wiki.pentaho.com/display/DATAMINING/Time+Series+Analysis+and+Forecasting+with+Weka
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void forecastIncome() throws Exception {
        log.info("CountEmployeesJob.forecastIncome");
        LocalDate now = LocalDate.now().minusDays(7);
        dailyForecast.clear();

        incomeForcastRepository.deleteByCreatedAndItemtype(Date.valueOf(LocalDate.now()), "INCOME");

        String pattern = "yyyy-MM-dd";
        Map<String, Double> workByDate = new HashMap<>();
        for (Work work : workService.findByPeriod(startDate, now)) {
            String dateString = DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern(pattern));
            if(!workByDate.containsKey(dateString)) {
                workByDate.put(dateString, 0.0);
            }
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0.0) {
                double income = workByDate.get(dateString) + (work.getWorkduration() * rate);
                workByDate.put(dateString, income);
            }
        }
        log.debug("dailyForecast.size() = " + dailyForecast.size());

        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION Timestamps\n");
        sb.append("\n");
        sb.append("@ATTRIBUTE timestamp DATE \"yyyy-MM-dd HH:mm:ss\"\n");
        sb.append("@ATTRIBUTE hours  NUMERIC\n");
        sb.append("\n");
        sb.append("@DATA\n");
        LocalDate localDate = startDate;
        while(localDate.isBefore(now)) {
            if(localDate.getDayOfMonth() != 1) {
                localDate = localDate.plusDays(1);
                continue;
            }
            String dateString = localDate.format(DateTimeFormatter.ofPattern(pattern));
            if(workByDate.containsKey(dateString)) {
                sb.append(patternizer(dateString)).append(",").append(workByDate.get(dateString)).append("\n");
                dailyForecast.add(workByDate.get(dateString));
            } else {
                dailyForecast.add(0.0);
                sb.append(patternizer(dateString)).append(",0.0\n");
            }
            localDate = localDate.plusDays(1);
        }

        //System.out.println("sb = " + sb);
        System.out.println(sb.toString());
        //System.out.println("localDate = " + localDate);
        //System.out.println("dailyForecast.size() = " + dailyForecast.size());


        Instances data = new Instances(new BufferedReader(new StringReader(sb.toString())));
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1)  data.setClassIndex(data.numAttributes() - 1);

        WekaForecaster forecaster = new WekaForecaster();

        // set the targets we want to forecast. This method calls
        // setFieldsToLag() on the lag maker object for us
        forecaster.setFieldsToForecast("hours");

        // default underlying classifier is SMOreg (SVM) - we'll use
        // gaussian processes for regression instead
        forecaster.setBaseForecaster(new GaussianProcesses());

        forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
        forecaster.getTSLagMaker().setPeriodicity(TSLagMaker.Periodicity.MONTHLY);
        forecaster.getTSLagMaker().setMinLag(1);
        forecaster.getTSLagMaker().setMaxLag(12); // monthly data

        // add a month of the year indicator field
        forecaster.getTSLagMaker().setAddMonthOfYear(true);
        forecaster.getTSLagMaker().setAddNumDaysInMonth(true);
        forecaster.getTSLagMaker().setAddQuarterOfYear(true);
        //forecaster.getTSLagMaker().setAddDayOfWeek(true);
        //forecaster.getTSLagMaker().setAddWeekendIndicator(true);
        //forecaster.getTSLagMaker().setAddDayOfMonth(true);

        // add a quarter of the year indicator field
        //forecaster.getTSLagMaker().setAddQuarterOfYear(true);

        // build the model
        forecaster.buildForecaster(data, System.out);

        // prime the forecaster with enough recent historical data
        // to cover up to the maximum lag. In our case, we could just supply
        // the 12 most recent historical instances, as this covers our maximum
        // lag period
        forecaster.primeForecaster(data);

        //int daysLeftInCurrentYear = 1095 + 150 + Math.toIntExact(ChronoUnit.DAYS.between(now, LocalDate.of((now.getMonthValue()>=7)?now.getYear()+1:now.getYear(), 7, 1)));
        int daysLeftInCurrentYear = 48;
        log.info("daysLeftInCurrentYear: " + daysLeftInCurrentYear);
        // forecast for 12 units (months) beyond the end of the
        // training data
        List<List<NumericPrediction>> forecast = forecaster.forecast(daysLeftInCurrentYear, System.out);

        // output the predictions. Outer list is over the steps; inner list is over
        // the targets

        double sum = 0.0;
        for (int i = 0; i < daysLeftInCurrentYear; i++) {
            List<NumericPrediction> predsAtStep = forecast.get(i);
            NumericPrediction predForTarget = predsAtStep.get(0);
            sum += predForTarget.predicted();
            System.out.println("predForTarget.predicted() = " + predForTarget.predicted());
            Double amount = (predForTarget.predicted() < 0.0) ? 0.0 : predForTarget.predicted();
            dailyForecast.add(amount);
            incomeForcastRepository.save(new IncomeForecast(i, amount, "INCOME"));
        }

        log.info("dailyForecast.size() = " + dailyForecast.size());
        log.debug("forecast = " + sum);
    }

    @Transactional
    @Scheduled(cron = "0 0 22 * * ?")
    public void forecastPeople() throws Exception {
        log.info("CountEmployeesJob.forecastPeople");
        LocalDate now = LocalDate.now().minusDays(7);
        dailyPeopleForecast.clear();

        incomeForcastRepository.deleteByCreatedAndItemtype(Date.valueOf(LocalDate.now()), "PEOPLE");

        String pattern = "yyyy-MM-dd";

        log.debug("dailyPeopleForecast.size() = " + dailyPeopleForecast.size());

        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION Timestamps\n");
        sb.append("\n");
        sb.append("@ATTRIBUTE timestamp DATE \"yyyy-MM-dd HH:mm:ss\"\n");
        sb.append("@ATTRIBUTE people  NUMERIC\n");
        sb.append("\n");
        sb.append("@DATA\n");
        LocalDate localDate = startDate;
        while(localDate.isBefore(now)) {
            String dateString = localDate.format(DateTimeFormatter.ofPattern(pattern));
            int consultants = 0;
            for (User user : getUsersByLocalDate(localDate)) {
                if(user.getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate)).get().getAllocation()>0) {
                    consultants++;
                    System.out.print("" + user.getLastname() + " | ");
                }
            }
            System.out.println();
            sb.append(patternizer(dateString)).append(",").append(consultants).append("\n");
            dailyPeopleForecast.add(consultants);
            localDate = localDate.plusMonths(1);
        }

        System.out.println(sb.toString());

        Instances data = new Instances(new BufferedReader(new StringReader(sb.toString())));
        if (data.classIndex() == -1)  data.setClassIndex(data.numAttributes() - 1);
        WekaForecaster forecaster = new WekaForecaster();
        forecaster.setFieldsToForecast("people");
        forecaster.setBaseForecaster(new GaussianProcesses());
        forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
        forecaster.getTSLagMaker().setPeriodicity(TSLagMaker.Periodicity.MONTHLY);
        forecaster.getTSLagMaker().setMinLag(1);
        forecaster.getTSLagMaker().setMaxLag(12); // monthly data

        forecaster.getTSLagMaker().setAddMonthOfYear(true);
        forecaster.getTSLagMaker().setAddNumDaysInMonth(true);
        forecaster.getTSLagMaker().setAddQuarterOfYear(true);

        // build the model
        forecaster.buildForecaster(data, System.out);

        // prime the forecaster with enough recent historical data
        // to cover up to the maximum lag. In our case, we could just supply
        // the 12 most recent historical instances, as this covers our maximum
        // lag period
        forecaster.primeForecaster(data);

        //int daysInCurrentYear = Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.of(now.getYear(), 7, 1), now));

        // forecast for 12 units (months) beyond the end of the
        // training data
        List<List<NumericPrediction>> forecast = forecaster.forecast(48, System.out);

        // output the predictions. Outer list is over the steps; inner list is over
        // the targets

        double sum = 0.0;
        for (int i = 0; i < 48; i++) {
            List<NumericPrediction> predsAtStep = forecast.get(i);
            NumericPrediction predForTarget = predsAtStep.get(0);
            sum += predForTarget.predicted();
            //System.out.println("predForTarget.predicted() = " + predForTarget.predicted());
            Integer amount = new Long((predForTarget.predicted() < 0.0) ? 0 : Math.round(predForTarget.predicted())).intValue();
            dailyPeopleForecast.add(amount);
            incomeForcastRepository.save(new IncomeForecast(i, amount, "PEOPLE"));
            System.out.println("amount = " + amount);
        }

        log.debug("dailyForecast.size() = " + dailyForecast.size());
        log.debug("forecast = " + sum);
    }

    public List<User> getUsersByLocalDate(LocalDate localDate) {
        log.info("CountEmployeesJob.getUsersByLocalDate");
        log.info("localDate = [" + localDate + "]");
        List<User> users = usersByLocalDate.get(localDate);
        if(users == null) {
            log.warn("no users found: "+localDate);
            return new ArrayList<>();
        }
        return users;
    }


    private String patternizer(String date) {
        return "\""+date+" 00:00:00\"";
    }

    public List<Double> getDailyForecast() {
        if(dailyForecast.size() == 0) try {
            log.warn("Daily revenue forecast not run!!!");
            forecastIncome();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dailyForecast;
    }

    public List<Integer> getPeopleForecast() {
        if(dailyPeopleForecast.size() == 0) try {
            log.warn("Daily people forecast not run!!!");
            forecastPeople();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dailyPeopleForecast;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
