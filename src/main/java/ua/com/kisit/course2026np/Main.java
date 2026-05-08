package ua.com.kisit.course2026np;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // Запуск Spring Boot
        SpringApplication.run(Main.class, args);

        // Вивід красивого повідомлення з посиланням після успішного запуску
        System.out.println("\n");
        System.out.println("🚀 Проект успішно запущено!");
        System.out.println("👉 Натисніть на посилання, щоб відкрити сайт: http://localhost:8080/");
        System.out.println("\n");
    }
}