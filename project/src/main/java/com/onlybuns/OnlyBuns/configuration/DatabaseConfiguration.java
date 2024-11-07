package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import com.onlybuns.OnlyBuns.repository.Repository_Role;
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
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Post repositoryPost;

    @Autowired
    private Repository_AccountActivation repositoryAccountActivation;

    @Autowired
    private Repository_Role repository_role;

    public void printAll_accounts() {
        List<Account> accounts = repositoryAccount.findAll();
        for (Account i : accounts) { System.out.println(i.toString()); }
    }

    public void printAll_posts() {
        List<Post> posts = repositoryPost.findAll();
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

        repositoryAccount.save(account);

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
        repositoryAccountActivation.save(new AccountActivation(acc_pera, AccountActivationStatus.APPROVED)); // approve petar on create

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

        repositoryPost.save(new Post("3 zeca piveks", "Prodajem 3 zeca. Treba mi za gajbu piva. ;)","location1", "/uploads/img/bunny1.png", acc_ajzak));
        repositoryPost.save(new Post("Sala", "I ja i zeka volimo travu.","location2", "/uploads/img/bunny2.png", acc_ajzak));


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
                "Mafijas",
                "laze teleckog",
                "/avatars/mclovin.png",
                "mrzim zeceve",
                false
        );


        Post r = new Post("zeka mora biti zdrav", "Morate kupati svog zeku da bi bio zdrav i prav :^).","location3", "/uploads/img/bunny3.png", acc_ana);
        repositoryPost.save(r);

        repositoryPost.save(new Post("e necu", "Sto bi trosio vodu nek smrdi!", acc_ajzak, r));
        repositoryPost.save(new Post("ti se okupaj", ":)", acc_hater, r));


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

        printAll_accounts();
        printAll_posts();

        // TODO: CREATE LIKES

        // TODO: ADD MORE USERS / POSTS

        return true;
    }
}
