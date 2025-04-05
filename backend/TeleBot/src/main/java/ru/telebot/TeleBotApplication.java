package ru.telebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TeleBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeleBotApplication.class, args);
    }

}
