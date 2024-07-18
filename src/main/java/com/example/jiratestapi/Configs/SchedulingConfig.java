package com.example.jiratestapi.Configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.jiratestapi.Jira.JiraController;


@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    private JiraController ticketController;

    // @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncTicketsNightly() throws Exception {
        ticketController.syncTickets();
    }
}

