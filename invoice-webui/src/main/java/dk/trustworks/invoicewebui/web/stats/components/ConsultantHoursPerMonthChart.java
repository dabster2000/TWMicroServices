package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.services.WorkService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ConsultantHoursPerMonthChart {


    private final WorkService workService;

    private final UserRepository userRepository;

    @Autowired
    public ConsultantHoursPerMonthChart(WorkService workService, UserRepository userRepository) {
        this.workService = workService;
        this.userRepository = userRepository;
    }

    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("ConsultantHoursPerMonthChart.createTopGrossingConsultantsChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        int period = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Consultants Below 100 Hours 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.BAR);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getxAxis().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("hours");
        chart.getConfiguration().getLegend().setEnabled(true);

        String[] categories = new String[period+1];
        LocalDate iteratorDate = periodStart;
        for (int i = 0; i < categories.length; i++) {
            categories[i] = iteratorDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
            iteratorDate = iteratorDate.plusMonths(1);
        }

        PlotOptionsSeries plot = new PlotOptionsSeries();
        plot.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plot);


        Map<String, double[]> userMonths = new HashMap<>();
        List<Work> workList = workService.findBillableWorkByPeriod(periodStart, periodEnd);

        for (Work work : workList) {
            if(!userMonths.containsKey(work.getUseruuid())) userMonths.put(work.getUseruuid(), new double[period+1]);
            userMonths.get(work.getUseruuid())[Period.between(periodStart, work.getRegistered()).getMonths()] += work.getWorkduration();
        }

        for (String useruuid : userMonths.keySet()) {
            User user = userRepository.findOne(useruuid);
            chart.getConfiguration().addSeries(new ListSeries(user.getFirstname()+" "+user.getLastname(), Arrays.asList(ArrayUtils.toObject(userMonths.get(useruuid)))));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
