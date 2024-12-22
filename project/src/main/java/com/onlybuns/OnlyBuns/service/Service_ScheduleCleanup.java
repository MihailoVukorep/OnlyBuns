package com.onlybuns.OnlyBuns.service;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class Service_ScheduleCleanup {

    @Autowired
    private Service_Account service_account;

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Like repository_like;

    @Autowired
    private Repository_Follow repository_follow;

    @Autowired
    private Repository_Post repository_post;


    @Autowired
    private Repository_AccountActivation repository_accountActivation;
    @Transactional
    @Scheduled(cron = "0 0 0 L * ?")
    public void deleteUnactivatedAccounts() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
        List<Account> unactivatedAccounts = service_account.findUnactivatedAccounts(thresholdDate);

        for (Account account : unactivatedAccounts) {
            //Optional<AccountActivation> opt_accountActivation = repository_accountActivation.findByAccount(account);

            repository_follow.deleteByAccountId(account.getId());

            repository_like.deleteByPostAccountId(account.getId());

            repository_like.deleteByAccountId(account.getId());

            repository_post.deleteByAccountId(account.getId());

            repository_accountActivation.deleteAccountActivationByAccount_Id(account.getId());

            repository_account.delete(account);
        }

        System.out.println("Deleted " + unactivatedAccounts.size() + " unactivated accounts.");
    }
}