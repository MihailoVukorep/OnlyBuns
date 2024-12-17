package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.model.AccountActivationStatus;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import com.onlybuns.OnlyBuns.repository.Repository_Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class Service_Email {

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Follow repositoryFollow;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    public ResponseEntity<String> get_api_verify(@RequestParam("token") String token) {
        Optional<AccountActivation> activationOpt = repository_accountActivation.findByToken(token);
        if (activationOpt.isEmpty()) {
            return new ResponseEntity<>("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
        }

        AccountActivation activation = activationOpt.get();
        if (activation.getStatus() == AccountActivationStatus.APPROVED) {
            return new ResponseEntity<>("Account already verified.", HttpStatus.BAD_REQUEST);
        }

        activation.setStatus(AccountActivationStatus.APPROVED);
        repository_accountActivation.save(activation);

        return new ResponseEntity<>("Account verified successfully.", HttpStatus.OK);
    }

    public AccountActivation GenerateNewAccountActivation(Account account, AccountActivationStatus status) {
        String token = UUID.randomUUID().toString();
        do {
            token = UUID.randomUUID().toString();
        } while (repository_accountActivation.existsByToken(token));

        return new AccountActivation(account, status, token);
    }

    public void sendVerificationEmail(Account account) {
        // Generate a verification token and save it in the AccountActivation table
        AccountActivation activation = GenerateNewAccountActivation(account, AccountActivationStatus.WAITING);
        repository_accountActivation.save(activation);

        // Send verification email
        String verificationLink = "http://127.0.0.1:8080/api/verify?token=" + activation.getToken();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(account.getEmail());
        mail.setFrom(env.getProperty("spring.mail.username"));
        mail.setSubject("OnlyBuns - Verify Email");
        mail.setText("Hello, Please visit the following link to verify your email: " + verificationLink);
        javaMailSender.send(mail);
    }

    @Value("${spring.mail.username}")
    private String mailSender;

    public void sendNotificationsToInactiveUsers() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusMinutes(1);
        List<Account> inactiveAccounts = repositoryAccount.findInactiveAccounts(thresholdDate);

        for (Account account : inactiveAccounts) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(account.getEmail());
            mail.setFrom(mailSender);
            mail.setSubject("We miss you at OnlyBuns!");
            mail.setText(generateEmailContent(account));
            javaMailSender.send(mail);
        }
    }

    private String generateEmailContent(Account account) {
        long newFollowersCount = repositoryFollow.countNewFollowers(account.getId(), account.getLastActivityDate());
        long newLikesCount = repositoryAccount.countLikesOnUsersPosts(account.getId(), account.getLastActivityDate());
        long newPostsCount = repositoryAccount.countOtherUsersPosts(account.getId(), account.getLastActivityDate());

        return "Hello " + account.getUserName() + ",\n\n"
                + "You haven't visited OnlyBuns for a while. Here's what you've missed:\n"
                + "- New followers: " + newFollowersCount + "\n"
                + "- New likes: " + newLikesCount + "\n"
                + "- New posts: " + newPostsCount + "\n\n"
                + "Come back and see what's new!\n\n"
                + "Best regards,\nOnlyBuns Team";
    }

}
