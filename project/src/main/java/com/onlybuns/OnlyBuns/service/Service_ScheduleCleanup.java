package com.onlybuns.OnlyBuns.service;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.model.AccountActivationStatus;
import com.onlybuns.OnlyBuns.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class Service_ScheduleCleanup {

    private static final Logger logger = LoggerFactory.getLogger(Service_ScheduleCleanup.class);

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
    private Repository_ChatMember repository_chatMember;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Transactional
    @Scheduled(cron = "0 0 0 L * ?")
    public void deleteUnactivatedAccounts() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
        List<Account> unactivatedAccounts = service_account.findUnactivatedAccounts(thresholdDate);

        for (Account account : unactivatedAccounts) {
            try {

                Optional<AccountActivation> activation = repository_accountActivation.findByAccount(account);
                if (activation.isPresent() && activation.get().getStatus() == AccountActivationStatus.APPROVED) {
                    continue;
                }

                System.out.println("Deleting account - ID: " + account.getId() +
                        ", Username: " + account.getUserName() +
                        ", Email: " + account.getEmail() +
                        ", Created: " + account.getCreatedDate());

                repository_accountActivation.deleteByAccount(account);

                try {
                    repository_chatMember.deleteByAccountId(account.getId());
                } catch (Exception e) {
                    logger.warn("Could not clean up chat members for unactivated account {}: {}",
                            account.getId(), e.getMessage());
                }

                repository_account.delete(account);

            } catch (Exception e) {
                logger.error("Failed to delete unactivated account ID {}: {}",
                        account.getId(), e.getMessage());
            }
        }

        logger.info("Account cleanup completed. Deleted {} unactivated accounts.",
                unactivatedAccounts.size());
    }
}