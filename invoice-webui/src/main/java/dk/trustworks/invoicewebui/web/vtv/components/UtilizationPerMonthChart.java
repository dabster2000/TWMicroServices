package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.ChartUtils.createDataSeries;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerMonthChart {

    private UserService userService;

    private StatisticsService statisticsService;

    @Autowired
    public UtilizationPerMonthChart(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    public Chart createUtilizationPerMonthChart(LocalDate periodStart) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(periodStart, LocalDate.now().withDayOfMonth(1).plusMonths(12))+1;

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption(null);
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("%");
        chart.getConfiguration().getyAxes().getAxis(0).setMax(100);
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        double[] monthTotalAvailabilites = new double[monthPeriod];
        double[] monthAvailabilites = new double[monthPeriod];

        LocalDate localDate = periodStart.withDayOfMonth(1);
        int m = 0;
        do {
            for (User user : userService.findWorkingUsersByDate(localDate, ConsultantType.CONSULTANT)) {
                double budget = statisticsService.getConsultantBudgetHoursByMonth(user, localDate);
                monthAvailabilites[m] += budget;
                double availability = statisticsService.getConsultantAvailabilityByMonth(user, localDate).getAvailableHours();
                monthTotalAvailabilites[m] += availability;
                //localDate = localDate.plusMonths(1);
            }
            m++;
            localDate = localDate.plusMonths(1);
        } while (m<=monthPeriod);

        ListSeries budgetListSeries = new ListSeries("Budget utilization");
        for (int j = 0; j < monthPeriod; j++) {
            budgetListSeries.addData(Math.round((monthAvailabilites[j] / monthTotalAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(budgetListSeries);

        DataSeries actualDataSeries = new DataSeries("Actual utilization");
        actualDataSeries.setData(getAverageAllocationByYear(periodStart));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, LocalDate.now().withDayOfMonth(1).plusMonths(13)));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getAverageAllocationByYear(LocalDate startDate) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();
        do {
            double allocation = 0.0;
            double countEmployees = 0.0;
            for (User user : userService.findEmployedUsersByDate(startDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                double billableWorkHours = statisticsService.getConsultantRevenueHoursByMonth(user, startDate);
                AvailabilityDocument availability = statisticsService.getConsultantAvailabilityByMonth(user, startDate);
                if (availability == null) {
                    availability = new AvailabilityDocument(user, startDate, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED);
                }
                double monthAllocation = 0.0;
                if (billableWorkHours > 0.0 && availability.getAvailableHours() > 0.0) {
                    monthAllocation = (billableWorkHours / availability.getAvailableHours()) * 100.0;
                }
                countEmployees++;
                allocation += monthAllocation;
            }
            allocation = Math.floor(allocation / countEmployees);
            dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), NumberUtils.round(allocation, 0)));
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now()));
        return dataSeriesItemList;
    }
}