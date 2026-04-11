package com.cb.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class WorkflowSaasPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowSaasPlatformApplication.class, args);
    }

}
