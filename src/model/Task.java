package model;

import java.time.LocalDate;

public class Task {

    public enum TaskStatus { PENDING, COMPLETED, CANCELLED }

    private int id;
    private Product product;
    private int requiredQuantity;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private TaskStatus status;
    private int progress;

    public Task(int id, Product product, int requiredQuantity, String clientName,
                LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.product = product;
        this.requiredQuantity = requiredQuantity;
        this.clientName = clientName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = TaskStatus.PENDING;
        this.progress = 0;
    }

    public int getId() { return id; }
    public Product getProduct() { return product; }
    public int getRequiredQuantity() { return requiredQuantity; }
    public TaskStatus getStatus() { return status; }
    public int getProgress() { return progress; }

    public void setStatus(TaskStatus status) { this.status = status; }
    public void updateProgress(int percent) {
        progress += percent;
        if (progress > 100) progress = 100;
    }
}
