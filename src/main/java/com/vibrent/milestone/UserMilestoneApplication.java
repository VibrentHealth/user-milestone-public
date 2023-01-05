package com.vibrent.milestone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class that can be used to bootstrap and launch a Spring application from Java main method.
 */
@ComponentScan(basePackages = {"com.vibrent.milestone", "com.vibrent.usermilestone", "com.vibrent.vrp.oidc"})
@SpringBootApplication
public class UserMilestoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserMilestoneApplication.class, args);
    }

}
