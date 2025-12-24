package io;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ErrorLogger {

    public static synchronized void log(String message) {
        try (FileWriter fw = new FileWriter("error.txt", true)) {
            fw.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("فشل تسجيل الخطأ");
        }
    }
}
