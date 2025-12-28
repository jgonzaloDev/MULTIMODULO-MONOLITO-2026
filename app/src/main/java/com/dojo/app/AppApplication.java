package com.dojo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dojo.customers","com.dojo.orders","com.dojo.opentelemetry"})
@EnableJpaRepositories(basePackages = {"com.dojo.customers.repository","com.dojo.orders.repository"})
@EntityScan(basePackages = {"com.dojo.customers.entities","com.dojo.orders.entities"})
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
