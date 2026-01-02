package model;

import java.time.LocalDate;
import java.util.List;

public class Task {
    public void setStatus(model.TaskStatus taskStatus) {

    }

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
    public void setProgress(int progress) {
        if (progress < 0) this.progress = 0;
        else if (progress > 100) this.progress = 100;
        else this.progress = progress;
    }
    public void updateProgress(int percent) { setProgress(this.progress + percent); }

    // ===== التخزين =====
    public String toFileString() {
        return id + "," + product.getId() + "," + requiredQuantity + "," + status + "," + progress + "," + clientName + "," + startDate + "," + endDate;
    }

    public static Task fromFileString(String line, List<Product> allProducts) {
        String[] parts = line.split(",");
        Product product;
        product = allProducts.stream().filter(p -> p.getId() == Integer.parseInt(parts[1])).findFirst().orElse(null);
        Task task = new Task(
                Integer.parseInt(parts[0]),
                product,
                Integer.parseInt(parts[2]),
                parts[5],
                LocalDate.parse(parts[6]),
                LocalDate.parse(parts[7])
        );
        task.setStatus(TaskStatus.valueOf(parts[3]));
        task.setProgress(Integer.parseInt(parts[4]));
        return task;
    }
}
