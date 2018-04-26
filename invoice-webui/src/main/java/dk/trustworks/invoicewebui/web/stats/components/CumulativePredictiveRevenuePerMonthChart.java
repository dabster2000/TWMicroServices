package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class CumulativePredictiveRevenuePerMonthChart {

    @Autowired
    private CountEmployeesJob countEmployeesJob;

    public Chart createCumulativePredictiveRevenuePerMonthChart() {
        System.out.println("CumulativePredictiveRevenuePerMonthChart.createCumulativePredictiveRevenuePerMonthChart");
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Predicted Monthly Revenue");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<Double> dailyForecast = countEmployeesJob.getDailyForecast();
        LocalDate localDate = countEmployeesJob.getStartDate();
        //Period period = Period.between(countEmployeesJob.getStartDate(), LocalDate.now());
        int monthsInPeriod = Math.toIntExact(ChronoUnit.MONTHS.between(localDate, LocalDate.now()))+(dailyForecast.size());
        System.out.println("countEmployeesJob.getStartDate() = " + countEmployeesJob.getStartDate());
        System.out.println("period = " + monthsInPeriod);
        String[] categories = new String[monthsInPeriod];
        DataSeries revenueSeries = new DataSeries("Revenue");

        int month = localDate.getMonthValue();
        double monthSum = 0.0;
        int i = 0;
        categories[i++] = localDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        for (Double amount : dailyForecast) {
            if(localDate.getMonthValue() != month) {
                revenueSeries.add(new DataSeriesItem(localDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(monthSum)));
                System.out.println("localDate.format(DateTimeFormatter.ofPattern(\"MMM-yyyy\")) = " + localDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy")));
                categories[i++] = localDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
                monthSum = 0.0;
                month = localDate.getMonthValue();
            }
            monthSum += amount;
            localDate = localDate.plusMonths(1);
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
