package ru.onec.configuration;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Data
@Configuration
public class OneCConfig {
    @Value("${1c.login}")
    private String login;
    @Value("${1c.password}")
    private String password;

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return requestTemplate -> {
            String auth = login + ":" + password;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            requestTemplate.header("Authorization", "Basic " + encodedAuth);
        };
    }

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new SpringDecoder(this::httpMessageConverters));
    }
}