package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Post repositoryPost;

    @Autowired
    private Repository_AccountActivation repositoryAccountActivation;

    public void printAll_accounts() {
        List<Account> accounts = repositoryAccount.findAll();
        for (Account i : accounts) { System.out.println(i.toString()); }
    }

    public void printAll_posts() {
        List<Post> posts = repositoryPost.findAll();
        for (Post i : posts) { System.out.println(i.toString()); }
    }

    public Account CreateAccount(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, AccountRole accountRole) {
        Account account = new Account(
                email,
                userName,
                password,
                firstName,
                lastName,
                address,
                avatar,
                bio,
                accountRole
        );
        repositoryAccount.save(account);
        return account;
    }

    @Bean
    @Transactional
    public boolean instantiate() {

        Account acc_pera = CreateAccount(
                "pera@gmail.com",
                "rope",
                "123",
                "Pera",
                "Peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                AccountRole.USER
        );
        repositoryAccountActivation.save(new AccountActivation(acc_pera, AccountActivationStatus.APPROVED)); // approve petar on create

        Account acc_ajzak = CreateAccount(
                "ajzak@gmail.com",
                "ajzak",
                "123",
                "Ajs",
                "Nigrutin",
                "sutjeska 13",
                "/avatars/ajs.png",
                "gengsta lik",
                AccountRole.USER
        );

        Account acc_ranka = CreateAccount(
                "rankaradulovic70@gmail.com",
                "ranxx",
                "123",
                "Ranka",
                "Radulovic",
                "sutjeska 13",
                "/avatars/ajs.png",
                "gengsta lik",
                AccountRole.USER
        );

        repositoryPost.save(new Post("3 zeca piveks", "Prodajem 3 zeca. Treba mi za gajbu piva. ;)","location1", "uploads/img/bunny1.png", acc_ajzak));
        repositoryPost.save(new Post("Sala", "I ja i zeka volimo travu.","location2", "uploads/img/bunny2.png", acc_ajzak));


        Account acc_ana = CreateAccount(
                "konstrakta@gmail.com",
                "konstrakta",
                "123",
                "Ana",
                "Djuric",
                "tu na keju",
                "/avatars/kons.png",
                "umetnica moze biti zdrava",
                AccountRole.USER
        );

        repositoryPost.save(new Post("zeka mora biti zdrav", "Morate kupati svog zeku da bi bio zdrav i prav :^).","location3", "uploads/img/bunny3.png", acc_ana));

        Account acc_admin = CreateAccount(
                "bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/bigboss.png",
                "big scary admin guy",
                AccountRole.ADMIN
        );
        repositoryAccountActivation.save(new AccountActivation(acc_admin, AccountActivationStatus.APPROVED)); // approve petar on create


        Post root = new Post("Dilujem Sargarepe", "10 DINARA 100 SARGAREPA!!!!", acc_ana);
        repositoryPost.save(root);

        Post root2 = new Post("NEMA SANSE", "ALA DRUZE KAKAV DEAL!!", acc_ajzak, root);
        repositoryPost.save(root2);

        repositoryPost.save(new Post("DA DA", "Rodilo drvece :^)", acc_ajzak, root2));
        repositoryPost.save(new Post("HMM", "E TOSE NISAM NADO!!", acc_ajzak, root));


        Account acc_sara = CreateAccount(
                "sapundzijas@gmail.com",
                "sarahah",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/bigboss.png",
                "big scary admin guy",
                AccountRole.ADMIN
        );
        Account acc_sara2 = CreateAccount(
                "sapundzijas+superlongemail@gmail.com",
                "sara",
                "123",
                "Pera",
                "Peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                AccountRole.USER
        );

        printAll_accounts();
        printAll_posts();

        // TODO: CREATE LIKES

        // TODO: ADD MORE USERS / POSTS

        return true;
    }
}
