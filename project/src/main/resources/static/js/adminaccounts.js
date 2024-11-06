let div_accounts = document.getElementById("adminaccounts");

function accounts_clear(div_accounts) {
    
    while (div_accounts.firstChild) {
        div_accounts.removeChild(div_accounts.lastChild);
    }
}

function make_account(json) {

    const account_link = document.createElement("a");
    account_link.className = "account_link";
    account_link.href = "/accounts/" + json.id;

    const account = document.createElement("div");
    account.className = "account";
    
    const account_img_cont = document.createElement("div");
    account_img_cont.className = "account_img_cont";

    const account_img = document.createElement("img");
    account_img.className = "account_img_cont";
    account_img.src = json.avatar;
    account_img_cont.append(account_img);

    account.appendChild(account_img_cont);

    // public Integer id;
    // public String email;
    // public String userName;
    // public String avatar;
    // public AccountRole accountRole;
    // public Integer posts_count;
    // public Integer following_count;

    const account_info = document.createElement("div");
    account_info.className = "account_info";

    // HEAD
    const account_info_head = document.createElement("div");
    account_info_head.className = "account_info_head";

    // username
    const account_info_head_userName = document.createElement("div");
    account_info_head_userName.className = "account_info_head_userName";
    account_info_head_userName.innerHTML = json.userName;
    account_info_head.appendChild(account_info_head_userName);

    // email
    const account_info_head_email = document.createElement("p");
    account_info_head_email.className = "account_info_head_email";
    account_info_head_email.innerHTML = json.email;
    account_info_head.appendChild(account_info_head_email);


    account_info.appendChild(account_info_head);

    // BODY
    const account_info_body = document.createElement("div");
    account_info_body.className = "account_info_body";

    // following_count
    const account_info_head_follow = document.createElement("p");
    account_info_head_follow.className = "following_count";
    account_info_head_follow.innerHTML = json.follow_count > 0 ? `${json.follow_count} followers` : "0 followers";
    account_info_head.appendChild(account_info_head_follow);

    // posts_count
    const account_info_head_posts = document.createElement("p");
    account_info_head_posts.className = "account_info_head_posts";
    account_info_head_posts.innerHTML = json.posts_count > 0 ? `${json.posts_count} posts` : "0 posts";
    account_info_head.appendChild(account_info_head_posts);

    const account_info_body_accountRole = document.createElement("p");
    account_info_body_accountRole.className = "account_info_body_accountRole";
    account_info_body_accountRole.innerHTML = json.accountRole;
    account_info_body.appendChild(account_info_body_accountRole);

    account_info.appendChild(account_info_body);

    account.appendChild(account_info);

    account_link.appendChild(account);
    return account_link;
}

function accounts_populate(div_accounts, items) {

    for (let i = 0; i < items.length; i++) {
        div_accounts.appendChild(make_account(items[i]));
    }
}


async function load() {

    accounts_clear(div_accounts);

    const response = await fetch("/api/adminaccounts");
    const json = await response.json();

    console.log(json)
    accounts_populate(div_accounts, json);
}

load();

