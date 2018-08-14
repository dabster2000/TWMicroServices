package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class BudgetNew {
    @Id
    private String uuid;
    private int month;
    private int year;
    private Double budget;

    @ManyToOne()
    @JoinColumn(name = "consultantuuid")
    private Consultant consultant;

    @ManyToOne()
    @JoinColumn(name = "projectuuid")
    private Project project;

    public BudgetNew() {
        uuid = UUID.randomUUID().toString();
    }

    public BudgetNew(int month, int year, Double budget, Consultant consultant, Project project) {
        this();
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.consultant = consultant;
        this.project = project;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "BudgetNew{" +
                "uuid='" + uuid + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", budget=" + budget +
                '}';
    }
}
