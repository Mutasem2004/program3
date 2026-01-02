package model;

import java.util.ArrayList;
import java.util.List;

public class ProductLine {

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
    public void removeTask(Task task) { tasks.remove(task); }

    @Override
    public String toString() { return name; }

    // ===== التخزين =====
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name);
        for (Task t : tasks) sb.append(",").append(t.getId());
        sb.append(",").append(status);
        return sb.toString();
    }

    public static ProductLine fromFileString(String line, List<Task> allTasks) {
        String[] parts = line.split(",");
        ProductLine pl = new ProductLine(Integer.parseInt(parts[0]), parts[1]);
        for (int i = 2; i < parts.length - 1; i++) {
            int finalI = i;
            Task t = allTasks.stream().filter(task -> task.getId() == Integer.parseInt(parts[finalI])).findFirst().orElse(null);
            if (t != null) pl.addTask(t);
        }
        pl.setStatus(Status.valueOf(parts[parts.length - 1]));
        return pl;
    }
}
