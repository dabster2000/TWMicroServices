package dk.trustworks.invoicewebui.model;

import com.google.common.base.Objects;

import javax.persistence.*;

/**
 * Created by hans on 28/06/2017.
 */

public class Week {

    @Id private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    private String useruuid;
    private int weeknumber;
    private int year;
    private int sorting;

    private String workas;

    public Week() {
    }

    public Week(String uuid, int weeknumber, int year, String useruuid, Task task) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.useruuid = useruuid;
        this.task = task;
    }

    public Week(String uuid, int weeknumber, int year, String useruuid, Task task, String workas) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.useruuid = useruuid;
        this.task = task;
        this.workas = workas;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getWeeknumber() {
        return weeknumber;
    }

    public void setWeeknumber(int weeknumber) {
        this.weeknumber = weeknumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getWorkas() {
        return workas;
    }

    public void setWorkas(String workas) {
        this.workas = workas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        return getWeeknumber() == week.getWeeknumber() &&
                getYear() == week.getYear() &&
                Objects.equal(getTask(), week.getTask()) &&
                Objects.equal(getUseruuid(), week.getUseruuid()) &&
                Objects.equal(getWorkas(), week.getWorkas());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTask(), getUseruuid(), getWeeknumber(), getYear(), getWorkas());
    }

    @Override
    public String toString() {
        return "Week{" +
                "uuid='" + uuid + '\'' +
                ", task=" + task.getUuid() +
                ", user=" + useruuid +
                ", weeknumber=" + weeknumber +
                ", year=" + year +
                ", sorting=" + sorting +
                '}';
    }
}
