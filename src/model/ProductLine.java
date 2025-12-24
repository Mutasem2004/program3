package model;

import java.util.ArrayList;
import java.util.List;

public class ProductLine {

    // ✅ هذا هو التعديل الأساسي: enum Status بدل ProductLineStatus
    public enum Status { ACTIVE, STOPPED }

    private int id;
    private String name;
    private List<Task> tasks;
    private Status status;

    public ProductLine(int id, String name) {
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<>();
        this.status = Status.STOPPED;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Task> getTasks() { return tasks; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public void addTask(Task task) { tasks.add(task); }
}
