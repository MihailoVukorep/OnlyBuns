package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.model.AccountActivationStatus;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;
import java.util.UUID;

@Service
public class Service_Email {

    @Autowired
    private Repository_AccountActivation repositoryAccountActivation;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    public ResponseEntity<String> api_verify(@RequestParam("token") String token) {
        Optional<AccountActivation> activationOpt = repositoryAccountActivation.findByToken(token);
        if (activationOpt.isEmpty()) {
            return new ResponseEntity<>("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
        }

        AccountActivation activation = activationOpt.get();
        if (activation.getStatus() == AccountActivationStatus.APPROVED) {
            return new ResponseEntity<>("Account already verified.", HttpStatus.BAD_REQUEST);
        }

        activation.setStatus(AccountActivationStatus.APPROVED);
        repositoryAccountActivation.save(activation);

        return new ResponseEntity<>("Account verified successfully.", HttpStatus.OK);
    }

    public void sendVerificationEmail(Account account) {
        // Generate a verification token and save it in the AccountActivation table
        String token = UUID.randomUUID().toString();
        AccountActivation activation = new AccountActivation();
        activation.setAccount(account);
        activation.setStatus(AccountActivationStatus.WAITING);
        activation.setToken(token);
        repositoryAccountActivation.save(activation);

        // Send verification email
        String verificationLink = "http://127.0.0.1:8080/api/verify?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(account.getEmail());
        mail.setFrom(env.getProperty("spring.mail.username"));
        mail.setSubject("OnlyBuns - Verify Email");
        mail.setText("Hello, Please visit the following link to verify your email: " + verificationLink);
        javaMailSender.send(mail);
    }
}
