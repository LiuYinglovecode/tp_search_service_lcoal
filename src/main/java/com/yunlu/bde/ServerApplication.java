package com.yunlu.bde;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, SecurityAutoConfiguration.class},
        scanBasePackages = {"com.yunlu.bde"})
public class ServerApplication {
    public static void main(String[] args) {
        String[] applicationArgs = args;
        if (args == null || args.length == 0) {
            args = new String[]{"-port", "9997", "-d", "dev"};

        }
        if(!String.join(";", args).contains("--server.port=")) {
            applicationArgs = args;
            if (applicationArgs == null) {
                return;
            }
        }

        SpringApplication.run(ServerApplication.class, applicationArgs);
    }
}
