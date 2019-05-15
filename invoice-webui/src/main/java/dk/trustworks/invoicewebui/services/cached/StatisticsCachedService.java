package dk.trustworks.invoicewebui.services.cached;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.model.dto.WorkDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dk.trustworks.invoicewebui.services.ContractService.getContractsByDate;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class StatisticsCachedService {

    private final static Logger log = LoggerFactory.getLogger(StatisticsCachedService.class.getName());

    private final ContractService contractService;
    private final ExpenseRepository expenseRepository;
    private final WorkService workService;
    private final UserService userService;

    public StatisticsCachedService(ContractService contractService, ExpenseRepository expenseRepository, WorkService workService, UserService userService) {
        this.contractService = contractService;
        this.expenseRepository = expenseRepository;
        this.workService = workService;
        this.userService = userService;
    }

    @Getter(lazy=true) private final List<BudgetDocument> cachedBudgedData = createBudgetData();

    @Getter(lazy=true) private final List<AvailabilityDocument> cachedAvailabilityData = createAvailabilityData();

    @Getter(lazy=true) private final List<ExpenseDocument> cachedExpenseData = createExpenseData();

    @Getter(lazy=true) private final List<WorkDocument> cachedIncomeData = createIncomeData();

    @Scheduled(cron = "0 0 6,12,18 * * *")
    void refreshCache() {
        getCachedBudgedData().clear();
        getCachedBudgedData().addAll(createBudgetData());

        getCachedAvailabilityData().clear();
        getCachedAvailabilityData().addAll(createAvailabilityData());

        getCachedExpenseData().clear();
        getCachedExpenseData().addAll(createExpenseData());

        getCachedIncomeData().clear();
        getCachedIncomeData().addAll(createIncomeData());
    }

    private List<BudgetDocument> createBudgetData() {
        List<BudgetDocument> budgetDocumentList = new ArrayList<>();

        List<Contract> contracts = contractService.findAll();

        for (User user : userService.findAll()) {
            LocalDate startDate = LocalDate.of(2014, 7, 1);

            do {
                List<Contract> activeContracts = getContractsByDate(contracts, user, startDate);
                for (Contract contract : activeContracts) {
                    if(contract.getContractType().equals(ContractType.PERIOD)) {

                        ContractConsultant userContract = contract.findByUser(user);
                        if(userContract == null) continue;

                        double budget = userContract.getHours();
                        if(budget == 0.0) continue;

                        AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, startDate);
                        double monthBudget = (budget * availability.getWeeks()) - availability.getVacation();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, monthBudget, userContract.getRate());
                        budgetDocumentList.add(budgetDocument);
                    } else {
                        LocalDate finalStartDate = startDate;

                        ContractConsultant userContract = contract.findByUser(user);
                        if(userContract == null) continue;

                        double budget = userContract.getBudgets().stream()
                                .filter(budgetNew -> budgetNew.getYear() == finalStartDate.getYear() &&
                                        budgetNew.getMonth() == (finalStartDate.getMonthValue()+1))
                                .mapToDouble(budgetNew -> budgetNew.getBudget() / userContract.getRate()).sum();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, budget, contract.findByUser(user).getRate());
                        budgetDocumentList.add(budgetDocument);
                    }
                }
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return budgetDocumentList;
    }

    private List<WorkDocument> createIncomeData() {
        List<WorkDocument> workDocumentList = new ArrayList<>();

        List<Contract> contracts = contractService.findAll();

        for (User user : userService.findAll()) {
            LocalDate startDate = LocalDate.of(2014, 7, 1);

            do {
                List<Contract> activeContracts = ContractService.getContractsByDate(contracts, user, startDate);
                for (Contract contract : activeContracts) {
                    Double hoursRegisteredOnContractByPeriod = workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), startDate, startDate.plusMonths(1).minusDays(1));
                    double hours = (hoursRegisteredOnContractByPeriod==null)?0.0:hoursRegisteredOnContractByPeriod;
                    if(contract.findByUser(user)==null) continue;
                    WorkDocument workDocument = new WorkDocument(startDate, contract.getClient(), user, contract, hours, contract.findByUser(user).getRate());
                    workDocumentList.add(workDocument);
                }
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return workDocumentList;
    }

    private List<AvailabilityDocument> createAvailabilityData() {
        List<AvailabilityDocument> availabilityDocumentList = new ArrayList<>();

        for (User user : userService.findAll()) {
            List<Work> vacationByUser = workService.findVacationByUser(user);
            List<Work> sicknessByUser = workService.findSicknessByUser(user);
            LocalDate startDate = LocalDate.of(2014, 7, 1);
            do {
                LocalDate finalStartDate = startDate;
                double vacation = vacationByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(Work::getWorkduration).sum();
                double sickness = sicknessByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(Work::getWorkduration).sum();
                int capacity = userService.calculateCapacityByMonthByUser(user.getUuid(), stringIt(finalStartDate));
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);

                availabilityDocumentList.add(new AvailabilityDocument(user, finalStartDate, capacity, vacation, sickness, userStatus.getType(), userStatus.getStatus()));

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return availabilityDocumentList;
    }


    private List<ExpenseDocument> createExpenseData() {
        List<ExpenseDocument> expenseDocumentList = new ArrayList<>();

        LocalDate startDate = LocalDate.of(2014, 7, 1);
        do {
            LocalDate finalStartDate = startDate;
            int consultantSalaries = userService.getMonthSalaries(finalStartDate, ConsultantType.CONSULTANT.toString());
            double expenseSalaries = expenseRepository.findByPeriod(
                    finalStartDate.withDayOfMonth(1)).stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            long consultantCount = getActiveConsultantCountByMonth(finalStartDate);
            double staffSalaries = (expenseSalaries - consultantSalaries) / consultantCount;
            double sharedExpense = expenseRepository.findByPeriod(finalStartDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;

            if(expenseSalaries <= 0) {
                startDate = startDate.plusMonths(1);
                continue;
            }

            for (User user : userService.findAll()) {
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);
                if(userStatus.getType().equals(ConsultantType.CONSULTANT) && userStatus.getStatus().equals(StatusType.ACTIVE)) {
                    AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, finalStartDate);
                    if (availability == null || availability.getAvailableHours() <= 0.0) continue;
                    int salary = userService.getUserSalary(user, finalStartDate);
                    ExpenseDocument expenseDocument = new ExpenseDocument(finalStartDate, user, sharedExpense, salary, staffSalaries);
                    expenseDocumentList.add(expenseDocument);
                }
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));

        return expenseDocumentList;
    }


    public long getActiveEmployeeCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0)
                .count();
    }

    public long getActiveConsultantCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0 &&
                                availabilityDocument.getConsultantType().equals(ConsultantType.CONSULTANT) &&
                                availabilityDocument.getStatusType().equals(StatusType.ACTIVE))
                .count();
    }

    public double getConsultantRevenueByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getCachedIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(workDocument -> workDocument.getRate() * workDocument.getWorkHours()).sum();
    }

    public double getConsultantRevenueHoursByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getCachedIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(WorkDocument::getWorkHours).sum();
    }

    public double getConsultantBudgetByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getCachedBudgedData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(budgetDocument -> budgetDocument.getBudgetHours() * budgetDocument.getRate()).sum();
    }

    public double getConsultantBudgetHoursByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getCachedBudgedData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(BudgetDocument::getBudgetHours).sum();
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(User user, LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getUser().getUuid().equals(user.getUuid()) &&
                                availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(null);
    }

    public double getExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getCachedExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(ExpenseDocument::getExpenseSum).sum();
    }

    public ExpenseDocument getConsultantExpensesByMonth(User user, LocalDate month) {
        List<ExpenseDocument> expenceData = getCachedExpenseData();
        return expenceData.stream()
                .filter(
                        expenseDocument -> expenseDocument.getUser().getUuid().equals(user.getUuid())
                                && expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(new ExpenseDocument(month, user, 0.0, 0.0, 0.0));
    }

}
