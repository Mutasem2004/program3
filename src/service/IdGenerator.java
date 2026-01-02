package service;

public class IdGenerator {
    private int count = 1;

    public int getNextId() {
        return count++;
    }
}
