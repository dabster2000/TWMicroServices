package dk.trustworks.invoicewebui.web.project.model;

import dk.trustworks.invoicewebui.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hans on 21/08/2017.
 */
public class TaskRow {

    private Task task;
    private String taskName;
    private List<TaskRow> userRows = new ArrayList<>();

    private String[] budget;

    public TaskRow(int months) {
        budget = new String[months];
    }

    public TaskRow(Task task, int months) {
        this(months);
        this.task = task;
        this.taskName = task.getName();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<TaskRow> getUserRows() {
        return userRows;
    }

    public void setUserRows(List<TaskRow> userRows) {
        this.userRows = userRows;
    }

    public void addUserRow(UserRow userRow) {
        userRows.add(userRow);
    }

    public String[] getBudget() {
        return budget;
    }

    public void setBudget(String[] budget) {
        this.budget = budget;
    }

    public void setMonth(int month, String value) {
        //budget[month] = value;
    }

    @Override
    public String toString() {
        return "TaskRow{" + "task=" + task +
                ", taskName='" + taskName + '\'' +
                ", userRows=" + userRows +
                ", budget=" + Arrays.toString(budget) +
                '}';
    }

    public String getUsername() {
        return "";
    }

    public String getRate() {
        return "";
    }

    public void setRate(String rate) {
    }

    public String getMonth(int actualMonth) {
        return budget[actualMonth];
    }
}
