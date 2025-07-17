package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import com.onlybuns.OnlyBuns.service.Service_Account;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import com.onlybuns.OnlyBuns.service.Service_Email;
import com.onlybuns.OnlyBuns.service.Service_Trend;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Follow repository_follow;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private Repository_Role repository_role;

    @Autowired
    private Repository_Like repository_like;

    @Autowired
    private Service_Email service_email;

    @Autowired
    private Service_Trend service_trend;

    @Autowired
    private Service_Chat service_chat;


    private final String LOCATION_NOVI_SAD = "45.25120485988152,19.82688903808594";
    private final String LOCATION_BELGRADE = "44.81423651177903,20.45860290527344";

    @PostConstruct
    public void initUsers() {
        if (repository_account.count() >= 20) return;

        Role userRole = repository_role.findByName("USER").orElseGet(() -> repository_role.save(new Role("USER")));

        for (int i = 1; i <= 20; i++) {
            String email = "test+fake" + i + "@gmail.com";
            if (repository_account.findByEmail(email).isEmpty()) {
                Account acc = new Account(
                        email,
                        "fakeuser" + i,
                        "123",  // lozinka će se hash-ovati u konstruktoru
                        "User" + i,
                        "Test",
                        "Test Address",
                        "/avatars/avatar" + i + ".png",
                        "Test bio " + i,
                        userRole
                );
                repository_account.save(acc);
            }
        }

        System.out.println("✅ 20 test korisnika kreirano.");
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
        repository_accountActivation.save(service_email.GenerateNewAccountActivation(account, AccountActivationStatus.APPROVED)); // approve all accounts when created

        return account;
    }
    public Account TESTCreateAccount(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, Boolean addAdminRole, LocalDateTime created) {

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
                role_user,
                created
        );

        if (addAdminRole) {
            Set<Role> roles = account.getRoles();
            roles.add(role_admin);
            account.setRoles(roles);
        }

        repository_account.save(account);
        repository_accountActivation.save(service_email.GenerateNewAccountActivation(account, AccountActivationStatus.APPROVED)); // approve all accounts when created

        return account;
    }

    public Role init_role(String name) {
        Optional<Role> optional_role = repository_role.findByName(name);
        if (optional_role.isEmpty()) {
            Role role = new Role(name);
            repository_role.save(role);
            return role;
        } else {
            return optional_role.get();
        }
    }


    public void likePost(Post post, Account account) {
        repository_like.save(new Like(account, post));
        post.incrementLikeCount();
        repository_post.save(post);
    }

    public void follow(Account follower, Account followee) {
        Follow follow = new Follow(follower, followee, LocalDateTime.now());
        repository_follow.save(follow);
    }

    private void gen_accounts() {
        String[] firstNames = {"Alice", "Bob", "Charlie", "Daisy", "Ethan", "Fiona", "George", "Hannah", "Ian", "Jenny", "Kevin", "Laura", "Mason", "Nina", "Oliver", "Paula", "Quincy", "Rachel", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xander", "Yvonne", "Zack", "Ella", "Liam", "Sophia", "Noah", "Emma", "James", "Isabella", "Benjamin", "Mia", "Lucas", "Amelia", "Logan", "Harper", "Jacob", "Evelyn", "Michael", "Abigail", "Elijah", "Emily", "Alexander", "Avery", "Daniel", "Scarlett", "Henry", "Sofia"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts", "Gomez", "Phillips"};
        String[] bios = {
                "Loves bunnies and carrots.",
                "Bunny whisperer.",
                "Hop into my world of bunnies!",
                "Raising the fluffiest bunnies.",
                "Carrot connoisseur.",
                "Big fan of floppy ears.",
                "Guardian of the cutest buns.",
                "Living the bunny life.",
                "Carrot farming for my buns.",
                "Dedicated bunny caretaker.",
                "Proud owner of 3 rabbits.",
                "Loves rabbits more than people.",
                "Exploring bunny habitats.",
                "Sharing bunny cuddles.",
                "Fur and fluff everywhere!",
                "Hoppy days are the best.",
                "Bunny tales and adventures.",
                "All about bunny adoption.",
                "Carrot lover, bunny owner.",
                "Floppy ears, full heart.",
                "Bunny mom/dad extraordinaire.",
                "Caring for the cutest creatures.",
                "Bunny love knows no bounds.",
                "Life with bunnies is better.",
                "Champion of bunny rescue.",
                "A true bunny lover.",
                "Hopping through life with my buns.",
                "Always surrounded by fluffy tails.",
                "Expert in bunny cuddles.",
                "Bunnies are my happiness.",
                "Sharing the joy of bunny life.",
                "Dedicated to bunny welfare.",
                "Forever a bunny enthusiast.",
                "My world revolves around bunnies.",
                "Lover of all things fluffy.",
                "Bringing joy to bunnies everywhere.",
                "Hoppy and thriving.",
                "Living for bunny snuggles.",
                "Fluffiest member of the bunny community.",
                "Providing the best for my buns.",
                "A hoppy life is a happy life.",
                "Rabbit lover, carrot grower.",
                "Caring for every bunny I meet.",
                "Sharing bunny joy, one hop at a time.",
                "Forever enchanted by floppy ears.",
                "Advocate for bunny happiness.",
                "All bunnies deserve love and care.",
                "Living my best bunny-filled life."
        };

        for (int i = 0; i < 50; i++) {
            String email = "killmeplzftn+bunnyLover" + (i + 1) + "@gmail.com";
            String username = "bunnyLover" + (i + 1);
            String password = "123";
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[i % lastNames.length];
            String bio = bios[i % bios.length];

            CreateAccount(
                    email,                // email
                    username,             // username
                    password,             // password
                    firstName,            // first name
                    lastName,             // last name
                    LOCATION_NOVI_SAD,    // location
                    "/avatars/default.jpg", // avatar
                    bio,                  // bio
                    false                 // some boolean flag
            );
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
                LOCATION_NOVI_SAD,
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                false
        );

        Account acc_ajzak = CreateAccount(
                "killmeplzftn+ajzak@gmail.com",
                "ajzak",
                "123",
                "Ajs",
                "Nigrutin",
                LOCATION_BELGRADE,
                "/avatars/ajs.png",
                "gengsta lik",
                false
        );

        Account acc_patrik = CreateAccount(
                "killmeplzftn+patrik@gmail.com",
                "patrik",
                "123",
                "Patrik",
                "Zvezda",
                LOCATION_BELGRADE,
                "/avatars/patrik.png",
                "koga ti nazivas glavonjom?",
                false
        );

        repository_post.save(new Post("3 zeca piveks", "Prodajem 3 zeca. Treba mi za gajbu piva. ;)", "44.8184772717805,20.466587343599706", "uploads/img/bunny1.jpg", acc_ajzak));
        repository_post.save(new Post("Sala", "I ja i zeka volimo travu.", "44.813968405692904,20.48101087841278", "uploads/img/bunny2.jpg", acc_ajzak));


        Account acc_ana = CreateAccount(
                "killmeplzftn+konstrakta@gmail.com",
                "konstrakta",
                "123",
                "Ana",
                "Djuric",
                LOCATION_NOVI_SAD,
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
                LOCATION_NOVI_SAD,
                "/avatars/mclovin.png",
                "mrzim zeceve",
                false
        );


        Post post_kupajzeku = new Post("zeka mora biti zdrav", "Morate kupati svog zeku da bi bio zdrav i prav :^).", "45.248991995657825,19.8151251703269", "uploads/img/bunny5.jpg", acc_ana);
        repository_post.save(post_kupajzeku);

        repository_post.save(new Post("e necu", "Sto bi trosio vodu nek smrdi!", "44.80909355927732,20.472940567267337", acc_ajzak, post_kupajzeku));
        repository_post.save(new Post("ti se okupaj", ":)", "45.262173276394655,19.85307280358509", acc_hater, post_kupajzeku));


        Account acc_admin = CreateAccount(
                "bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                LOCATION_NOVI_SAD,
                "/avatars/bigboss.png",
                "big scary admin guy",
                true
        );

        Account acc_test = TESTCreateAccount(
                "killmeplzftn+test@gmail.com",
                "testacc",
                "123",
                "Pera",
                "Peric",
                LOCATION_NOVI_SAD,
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                false,
                LocalDateTime.of(2022, 12, 22, 15, 30, 45)
        );
        //repository_accountActivation.save(service_email.GenerateNewAccountActivation(acc_admin, AccountActivationStatus.APPROVED)); // approve petar on create


        Post post_dilujem = new Post("Dilujem Sargarepe", "10 DINARA 100 SARGAREPA!!!!", "45.25818293635168,19.808943655407006", acc_ana);
        repository_post.save(post_dilujem);

        Post post_nemasanse = new Post("NEMA SANSE", "ALA DRUZE KAKAV DEAL!!", "45.24197685597521,19.81958959776903", acc_ajzak, post_dilujem);
        repository_post.save(post_nemasanse);

        repository_post.save(new Post("DA DA", "Rodilo drvece :^)", "45.24572643495814,19.795722081828355", acc_ana, post_nemasanse));
        repository_post.save(new Post("HMM", "E TOSE NISAM NADO!!", "45.25008047417238,19.847921541151837", acc_ajzak, post_dilujem));

        repository_post.save(new Post("My post", "Moj post!", "45.23907378615232,19.812892956605797", acc_pera));


        Account acc_andjela = CreateAccount(
                "killmeplzftn+anakonda@gmail.com",
                "anakonda",
                "123",
                "Andjela",
                "Anakonda",
                LOCATION_NOVI_SAD,
                "/avatars/andjela.png",
                "zivot je kratak pojedi batak",
                false
        );

        Account acc_icy = CreateAccount(
                "rankaradulovic70@gmail.com",
                "icy",
                "123",
                "Icy",
                "Trix",
                LOCATION_NOVI_SAD,
                "/avatars/icy.png",
                "abrakadabra",
                true
        );

        // likes
        likePost(post_dilujem, acc_ajzak);
        likePost(post_dilujem, acc_andjela);
        likePost(post_dilujem, acc_patrik);
        likePost(post_nemasanse, acc_ajzak);
        likePost(post_kupajzeku, acc_ajzak);

        // follow
        follow(acc_ajzak, acc_icy);
        follow(acc_icy, acc_ajzak);
        follow(acc_ajzak, acc_ana);
        follow(acc_ajzak, acc_andjela);

        follow(acc_pera, acc_ajzak);
        follow(acc_pera, acc_hater);
        follow(acc_pera, acc_icy);

        service_chat.CreateChat(acc_pera, acc_ajzak);

        // // UNCOMMENT TO SPAM POSTS
        // for (int i = 0; i < 100; i++) {
        //     repository_post.save(new Post("Testing" + i, "Testing a lot of posts " + i, acc_ana));
        // }

        // RANDOM POSTS
        repository_post.save(new Post("Bunny Care 101", "How often should I clean my rabbit's cage?", acc_hater));
        repository_post.save(new Post("Favorite Bunny Breeds", "What's your favorite breed? I'm partial to Holland Lops!", acc_hater));
        repository_post.save(new Post("Funny Bunny Moments", "My bunny just did the craziest binky! What's the funniest thing your bunny has done?", acc_hater));
        repository_post.save(new Post("Healthy Bunny Treats", "Are there any treats that are safe for rabbits but also super healthy?", acc_hater));
        repository_post.save(new Post("DIY Bunny Toys", "I made a cardboard castle for my bunny, and they love it! Any other DIY toy ideas?", acc_hater));
        repository_post.save(new Post("First Time Bunny Owner", "Just got my first rabbit! Any tips for bonding with my new furry friend?", acc_hater));
        repository_post.save(new Post("Bunny Housing Advice", "Is it better to have a pen or free roam in a bunny-proof room?", acc_hater));
        repository_post.save(new Post("Rabbits and Other Pets", "Can rabbits get along with cats? I'd love to hear your experiences.", acc_hater));
        repository_post.save(new Post("Bunny Behavior Help", "My bunny keeps thumping at night. What does it mean?", acc_hater));
        repository_post.save(new Post("Best Bunny Photos!", "Let's have a thread of the cutest bunny photos! Here's mine to start!", acc_hater));
        repository_post.save(new Post("Best Hay Brands?", "What's the best brand of hay for picky rabbits?", acc_hater));
        repository_post.save(new Post("Are Bunnies Nocturnal?", "My rabbit is super active at night. Is this normal?", acc_hater));
        repository_post.save(new Post("Favorite Bunny Names", "What are some of the cutest bunny names you've heard?", acc_hater));
        repository_post.save(new Post("Litter Training Tips", "How did you teach your bunny to use the litter box?", acc_hater));
        repository_post.save(new Post("Bunny Zoomies!", "My bunny has been running in circles nonstop. Is this a good sign?", acc_hater));
        repository_post.save(new Post("Vegetables for Bunnies", "Which veggies are safe to feed daily?", acc_hater));
        repository_post.save(new Post("Are Rabbits Affectionate?", "How do rabbits show they love you?", acc_hater));
        repository_post.save(new Post("Bunny Bonding Stories", "How long did it take for your rabbits to bond?", acc_hater));
        repository_post.save(new Post("Rabbit-Proofing Tips", "What's the best way to protect my wires and furniture?", acc_hater));
        repository_post.save(new Post("Should I Get Two Rabbits?", "Is it better to keep rabbits in pairs?", acc_hater));
        repository_post.save(new Post("How to Groom a Bunny?", "My rabbit hates being brushed. Any advice?", acc_hater));
        repository_post.save(new Post("Fun Bunny Activities", "What games do your rabbits enjoy?", acc_hater));
        repository_post.save(new Post("Bunny Birthday Ideas", "How do you celebrate your rabbit's birthday?", acc_hater));
        repository_post.save(new Post("Rabbits and the Heat", "What's the best way to keep rabbits cool in summer?", acc_hater));
        repository_post.save(new Post("Winter Bunny Care", "Do indoor rabbits need anything special in winter?", acc_hater));
        repository_post.save(new Post("Safe Bunny Snacks", "Are there any store-bought treats you recommend?", acc_hater));
        repository_post.save(new Post("Bunny Habitat Size", "How much space does a single rabbit need?", acc_hater));
        repository_post.save(new Post("Traveling with Rabbits", "Has anyone traveled long-distance with their bunny? Tips?", acc_hater));
        repository_post.save(new Post("Adopt or Shop?", "What are the benefits of adopting a bunny?", acc_hater));
        repository_post.save(new Post("Do Rabbits Like Music?", "I noticed my rabbit gets calmer when I play soft music. Does yours?", acc_hater));
        repository_post.save(new Post("Bunny-Safe Plants", "What houseplants are safe to have around rabbits?", acc_hater));


        repository_post.save(new Post("Rabbit Health Check", "How often should I take my bunny to the vet?", acc_pera));
        repository_post.save(new Post("Do Rabbits Get Lonely?", "Can a single rabbit be happy, or do they need a friend?", acc_pera));
        repository_post.save(new Post("Best Bunny Beds", "Do rabbits prefer blankets, cushions, or something else?", acc_pera));
        repository_post.save(new Post("Signs of a Happy Bunny", "What are some signs that my bunny is happy?", acc_pera));
        repository_post.save(new Post("How Long Do Rabbits Live?", "What's the average lifespan of a domestic rabbit?", acc_pera));
        repository_post.save(new Post("Rabbits and Children", "Are rabbits good pets for kids?", acc_pera));
        repository_post.save(new Post("Bunny Cage Setup", "What essentials should I include in my rabbit's cage?", acc_pera));
        repository_post.save(new Post("Homemade Bunny Treats", "Anyone have recipes for bunny-safe treats?", acc_pera));
        repository_post.save(new Post("Why Is My Bunny Digging?", "My rabbit keeps digging at the carpet. What does this mean?", acc_pera));
        repository_post.save(new Post("First Vet Visit", "What should I expect during my bunny's first check-up?", acc_pera));
        repository_post.save(new Post("Rabbits and Cuddles", "My bunny seems to avoid cuddling. Can I change this?", acc_pera));
        repository_post.save(new Post("Outdoor Playtime", "Is it safe to let rabbits play in the backyard?", acc_pera));
        repository_post.save(new Post("Bunny Nail Trimming", "How do you trim your rabbit's nails without stressing them?", acc_pera));
        repository_post.save(new Post("Rabbits and Holidays", "How do you keep your bunny safe during holiday chaos?", acc_pera));
        repository_post.save(new Post("Bonding with My Bunny", "What are the best ways to earn a rabbit's trust?", acc_pera));
        repository_post.save(new Post("Rabbit Exercise Needs", "How much exercise does a rabbit need daily?", acc_pera));
        repository_post.save(new Post("Bunny Toy Recommendations", "What are your bunny's favorite toys?", acc_pera));
        repository_post.save(new Post("Signs of a Sick Bunny", "What are the warning signs that my rabbit is unwell?", acc_pera));
        repository_post.save(new Post("Rabbit Socialization", "How can I help my rabbit get used to new people?", acc_pera));
        repository_post.save(new Post("Do Rabbits Recognize Faces?", "Does your bunny know who you are?", acc_pera));
        repository_post.save(new Post("Rabbits and Bonding", "My bunny seems shy. How do I help them open up?", acc_pera));
        repository_post.save(new Post("Bunny Safe Chews", "What can I give my rabbit to chew on safely?", acc_pera));
        repository_post.save(new Post("How to Pick Up a Rabbit", "Is there a proper way to hold a bunny?", acc_pera));
        repository_post.save(new Post("Rabbits and Other Pets", "How do I introduce my bunny to my dog?", acc_pera));
        repository_post.save(new Post("Bunny Hideouts", "What's the best type of hideout for a rabbit?", acc_pera));
        repository_post.save(new Post("Bunny Poop Questions", "Is it normal for my rabbit's poop to change size?", acc_pera));
        repository_post.save(new Post("Training a Bunny", "Can rabbits be taught tricks? If so, how?", acc_pera));
        repository_post.save(new Post("Bunny Playdates", "Is it safe to introduce my bunny to a friend's rabbit?", acc_pera));
        repository_post.save(new Post("Rabbit Water Preferences", "Do bunnies prefer bowls or water bottles?", acc_pera));
        repository_post.save(new Post("Bunny Binky Facts", "What does it mean when my rabbit does a binky?", acc_pera));
        repository_post.save(new Post("Favorite Bunny Facts", "What's your favorite fun fact about rabbits?", acc_pera));
        repository_post.save(new Post("Rabbits and Allergies", "Can rabbits cause allergic reactions in humans?", acc_pera));
        repository_post.save(new Post("Bunny Nighttime Behavior", "My rabbit makes noise at night. How can I help?", acc_pera));
        repository_post.save(new Post("Rabbit Adoption Tips", "What should I look for when adopting a rabbit?", acc_pera));
        repository_post.save(new Post("Bunny Fights!", "My bonded rabbits had a scuffle. What should I do?", acc_pera));
        repository_post.save(new Post("Bunny First Aid Kit", "What should I include in an emergency kit for my rabbit?", acc_pera));
        repository_post.save(new Post("Do Rabbits Like Being Pet?", "Where's the best spot to pet a rabbit?", acc_pera));
        repository_post.save(new Post("Bunny Eye Health", "One of my bunny's eyes is watering. What could it mean?", acc_pera));
        repository_post.save(new Post("Bunny Bonding Techniques", "How do I help my bunnies bond with each other?", acc_pera));
        repository_post.save(new Post("Bunny Moods", "How can I tell if my rabbit is grumpy?", acc_pera));
        repository_post.save(new Post("Rabbit Ear Positions", "What do different ear positions mean?", acc_pera));
        repository_post.save(new Post("Bunny Bedding Materials", "What type of bedding is safest for rabbits?", acc_pera));
        repository_post.save(new Post("Bunnies in Apartments", "Can rabbits be happy in a small apartment?", acc_pera));
        repository_post.save(new Post("Favorite Bunny Stories", "What's the funniest or sweetest thing your rabbit has done?", acc_pera));
        repository_post.save(new Post("Rabbits and Bonding Time", "How much daily time should I spend with my bunny?", acc_pera));
        repository_post.save(new Post("Bunny-Proof Fences", "What fencing works best for indoor free-range rabbits?", acc_pera));
        repository_post.save(new Post("Rabbits and Vegetables", "Is it okay to feed carrots every day?", acc_pera));
        repository_post.save(new Post("Bunny Travel Carriers", "What's the safest travel carrier for rabbits?", acc_pera));
        repository_post.save(new Post("Rabbits and the Vet", "How can I make vet visits less stressful for my bunny?", acc_pera));
        repository_post.save(new Post("Bunny Winter Playtime", "Do indoor rabbits still need outdoor playtime in winter?", acc_pera));
        repository_post.save(new Post("Fun Bunny Challenges", "What bunny challenges have you done, like obstacle courses?", acc_pera));
        repository_post.save(new Post("Rabbit Digestive Health", "What should I do if my bunny isn't eating?", acc_pera));
        repository_post.save(new Post("Rabbits and Teeth", "How can I tell if my bunny's teeth are overgrown?", acc_pera));

        // init users for paging testing
        gen_accounts();

        System.out.println("INIT DB");

        service_trend.getCurrentTrend();

        return true;
    }
}
