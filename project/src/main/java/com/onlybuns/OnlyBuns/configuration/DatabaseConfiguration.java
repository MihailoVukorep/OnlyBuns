package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import com.onlybuns.OnlyBuns.service.Service_Email;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private Repository_Role repository_role;

    @Autowired
    private Repository_Like repository_like;

    @Autowired
    private Service_Email service_email;

    public void printAll_accounts() {
        List<Account> accounts = repository_account.findAll();
        for (Account i : accounts) { System.out.println(i.toString()); }
    }

    public void printAll_posts() {
        List<Post> posts = repository_post.findAll();
        for (Post i : posts) { System.out.println(i.toString()); }
    }

    public Account CreateAccount(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, Boolean addAdminRole) {

        Role role_user = init_role("USER");
        Role role_admin = init_role("ADMIN");

        Account account = new Account(
                email,
                userName,
                password,
                firstName,
                lastName,
                address,
                avatar,
                bio,
                role_user
        );

        if (addAdminRole) {
            Set<Role> roles = account.getRoles();
            roles.add(role_admin);
            account.setRoles(roles);
        }

        repository_account.save(account);

        return account;
    }

    public Role init_role(String name) {
        Optional<Role> optional_role = repository_role.findByName(name);
        if (optional_role.isEmpty()) {
            Role role = new Role(name);
            repository_role.save(role);
            return role;
        }
        else {
            return optional_role.get();
        }
    }

    @Bean
    @Transactional
    public boolean instantiate() {

        Role role_user = init_role("USER");
        Role role_admin = init_role("ADMIN");


        Account acc_pera = CreateAccount(
                "killmeplzftn+pera@gmail.com",
                "rope",
                "123",
                "Pera",
                "Peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                false
        );
        repository_accountActivation.save(service_email.GenerateNewAccountActivation(acc_pera, AccountActivationStatus.APPROVED)); // approve petar on create

        Account acc_ajzak = CreateAccount(
                "killmeplzftn+ajzak@gmail.com",
                "ajzak",
                "123",
                "Ajs",
                "Nigrutin",
                "sutjeska 13",
                "/avatars/ajs.png",
                "gengsta lik",
                false
        );

        Account acc_ranka = CreateAccount(
                "rankaradulovic70@gmail.com",
                "ranxx",
                "123",
                "Ranka",
                "Radulovic",
                "sutjeska 13",
                "/avatars/default.jpg",
                "gengsta lik",
                false
        );

        repository_post.save(new Post("3 zeca piveks", "Prodajem 3 zeca. Treba mi za gajbu piva. ;)","location1", "uploads/img/bunny1.jpg", acc_ajzak));
        repository_post.save(new Post("Sala", "I ja i zeka volimo travu.","location2", "uploads/img/bunny2.jpg", acc_ajzak));


        Account acc_ana = CreateAccount(
                "killmeplzftn+konstrakta@gmail.com",
                "konstrakta",
                "123",
                "Ana",
                "Djuric",
                "tu na keju",
                "/avatars/kons.png",
                "umetnica moze biti zdrava",
                false
        );

        Account acc_hater = CreateAccount(
                "killmeplzftn+hejter@gmail.com",
                "hejter",
                "123",
                "Hejter",
                "McLovin",
                "laze teleckog",
                "/avatars/mclovin.png",
                "mrzim zeceve",
                false
        );


        Post r = new Post("zeka mora biti zdrav", "Morate kupati svog zeku da bi bio zdrav i prav :^).","location3", "uploads/img/bunny5.jpg", acc_ana);
        repository_post.save(r);

        repository_post.save(new Post("e necu", "Sto bi trosio vodu nek smrdi!", acc_ajzak, r));
        repository_post.save(new Post("ti se okupaj", ":)", acc_hater, r));


        Account acc_admin = CreateAccount(
                "bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/bigboss.png",
                "big scary admin guy",
                true
        );
        repository_accountActivation.save(service_email.GenerateNewAccountActivation(acc_admin, AccountActivationStatus.APPROVED)); // approve petar on create


        Post root = new Post("Dilujem Sargarepe", "10 DINARA 100 SARGAREPA!!!!", acc_ana);
        repository_post.save(root);

        Post root2 = new Post("NEMA SANSE", "ALA DRUZE KAKAV DEAL!!", acc_ajzak, root);
        repository_post.save(root2);

        repository_post.save(new Post("DA DA", "Rodilo drvece :^)", acc_ajzak, root2));
        repository_post.save(new Post("HMM", "E TOSE NISAM NADO!!", acc_ajzak, root));

        repository_post.save(new Post("My post", "Moj post!", acc_pera));


        Account acc_sara = CreateAccount(
                "sapundzijas@gmail.com",
                "sarahah",
                "123",
                "Sara",
                "Sara",
                "tu tamo",
                "/avatars/default.jpg",
                "sara",
                true
        );
        Account acc_sara2 = CreateAccount(
                "sapundzijas+superlongemail@gmail.com",
                "sara",
                "123",
                "Sara",
                "Sara",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                false
        );


        repository_like.save(new Like(acc_ajzak, root));
        repository_like.save(new Like(acc_sara, root));
        repository_like.save(new Like(acc_sara2, root));

        printAll_accounts();
        printAll_posts();

        // // UNCOMMENT TO SPAM POSTS
        // for (int i = 0; i < 100; i++) {
        //     repository_post.save(new Post("Testing" + i, "Testing a lot of posts " + i, acc_ana));
        // }

        return true;
    }
}
