package ru.telebot.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
public class OneCConfig {
    @Value("${1c.login}")
    private String login;
    @Value("${1c.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}