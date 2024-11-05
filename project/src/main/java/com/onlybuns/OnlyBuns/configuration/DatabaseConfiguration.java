package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import com.onlybuns.OnlyBuns.service.Service_Test;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Post repositoryPost;


    @Autowired
    private Service_Test serviceTest;

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

    public void CreatePost(String title, String text, Account account) {
        Post post = new Post(title, text, account);
        repositoryPost.save(post);
    }


    @Bean
    @Transactional
    public boolean instantiate() {

        Account a1 = CreateAccount(
                "killmeplzftn+pera@gmail.com",
                "rope",
                "123",
                "Pera",
                "Peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                AccountRole.USER
        );

        Account acc2 = CreateAccount(
                "killmeplzftn+ajzak@gmail.com",
                "ajzak",
                "123",
                "Ajs",
                "Nigrutin",
                "sutjeska 13",
                "/avatars/ajs.png",
                "gengsta lik",
                AccountRole.USER
        );

        CreatePost("3 zeca piveks", "Prodajem 3 zeca. Treba mi za gajbu piva. ;)", acc2);
        CreatePost("Sala", "I ja i zeka volimo travu.", acc2);


        Account acc3 = CreateAccount(
                "killmeplzftn+konstrakta@gmail.com",
                "konstrakta",
                "123",
                "Ana",
                "Djuric",
                "tu na keju",
                "/avatars/kons.png",
                "umetnica moze biti zdrava",
                AccountRole.USER
        );

        CreatePost("zeka mora biti zdrav", "Morate kupati svog zeku da bi bio zdrav i prav :^).", acc3);

        Account acc4 = CreateAccount(
                "killmeplzftn+bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/bigboss.png",
                "big scary admin guy",
                AccountRole.ADMIN
        );

        CreatePost("Zabranjeno dilovanje Sargarepa", "NA OVOM FORUMU SE NE SME DILOVATI SARGAREPA!!!! KO BUDE PREKRSIO DOBIJA BAN ISTE SEKUNDE!", acc4);

        serviceTest.printAll_accounts();
        serviceTest.printAll_posts();

        return true;
    }
}
