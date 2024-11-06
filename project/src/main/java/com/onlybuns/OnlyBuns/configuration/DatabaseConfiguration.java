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

    public Post CreatePost(String title, String text, Account account) {
        Post post = new Post(title, text, account);
        repositoryPost.save(post);
        return post;
    }

    public Post CreatePostComment(String title, String text, Account account, Post root) {
        Post post = new Post(title, text, account, root);
        repositoryPost.save(post);
        return post;
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

        CreateAccount(
                "killmeplzftn+superlongemail@gmail.com",
                "superlongusernametestyouui",
                "123",
                "Super Long First Name",
                "Super Long Last Name",
                "really long address, can't be longer, very annoying",
                "/avatars/bunny1.png",
                "this is a really long profile bio plz format me",
                AccountRole.USER
        );

        Post post = CreatePost("Zabranjeno dilovanje Sargarepa", "NA OVOM FORUMU SE NE SME DILOVATI SARGAREPA!!!! KO BUDE PREKRSIO DOBIJA BAN ISTE SEKUNDE!", acc4);


        Post p2 = CreatePostComment("NEMA SANSE", "I PIVO POSLE 10 CE ZABRANITI!!", acc2, post);

        CreatePostComment(":^)", "Hvala na ideji! Poz :)", acc4, p2);

        CreatePostComment("HMM", "E TOSE NISAM NADO!!", a1, post);


        serviceTest.printAll_accounts();
        serviceTest.printAll_posts();

        // TODO: CREATE LIKES

        // TODO: ADD MORE USERS / POSTS

        return true;
    }
}
