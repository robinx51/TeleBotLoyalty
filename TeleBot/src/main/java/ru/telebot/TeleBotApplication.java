package ru.telebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class TeleBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeleBotApplication.class, args);
    }

}
