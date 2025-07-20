package com.onlybuns.OnlyBuns.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Service_UserActivity {

    private final Service_Email serviceEmail;

    public Service_UserActivity(Service_Email serviceEmail) {
        this.serviceEmail = serviceEmail;
    }

    @Scheduled(cron = "0 12 23 * * *")
    public void scheduleNotificationTask() {
        serviceEmail.sendNotificationsToInactiveUsers();
    }
}
