loadScript('roles.js');

let div_accounts = document.getElementById("accounts");

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

    const account_info = document.createElement("div");
    account_info.className = "account_info";
    
    // HEAD
    const account_info_head = document.createElement("div");
    account_info_head.className = "account_info_head";

    // NAMES
    const account_info_head_names = document.createElement("div");
    account_info_head_names.className = "account_info_head_names";

    // role
    const account_info_head_accountRole = document.createElement("span");
    account_info_head_accountRole.className = "account_info_body_accountRole";
    account_info_head_accountRole.appendChild(roles(json.accountRoles)); // KOJI KURAC
    account_info_head_names.appendChild(account_info_head_accountRole);

    // space
    account_info_head_names.appendChild(document.createTextNode(" "));

    // first name
    const account_info_head_firstName = document.createElement("span");
    account_info_head_firstName.className = "account_info_head_firstName";
    account_info_head_firstName.innerHTML = json.firstName;
    account_info_head_names.appendChild(account_info_head_firstName);

    // space
    account_info_head_names.appendChild(document.createTextNode(" "));

    // last name
    const account_info_head_lastName = document.createElement("span");
    account_info_head_lastName.className = "account_info_head_lastName";
    account_info_head_lastName.innerHTML = json.lastName;
    account_info_head_names.appendChild(account_info_head_lastName);

    // space
    account_info_head_names.appendChild(document.createTextNode(" "));

    // (username)
    const account_info_head_userName_start = document.createElement("span");
    account_info_head_userName_start.innerHTML = "(";
    account_info_head_names.appendChild(account_info_head_userName_start);
    const account_info_head_userName = document.createElement("span");
    account_info_head_userName.className = "account_info_head_userName";
    account_info_head_userName.innerHTML = json.userName;
    account_info_head_names.appendChild(account_info_head_userName);
    const account_info_head_userName_end = document.createElement("span");
    account_info_head_userName_end.innerHTML = ")";
    account_info_head_names.appendChild(account_info_head_userName_end);

    account_info_head.appendChild(account_info_head_names);

    // email
    const account_info_head_email = document.createElement("div");
    account_info_head_email.className = "account_info_head_email";
    account_info_head_email.innerHTML = json.email;
    account_info_head.appendChild(account_info_head_email);

    account_info.appendChild(account_info_head);

    // BODY
    const account_info_body = document.createElement("div");
    account_info_body.className = "account_info_body";

    // address
    const account_info_body_address = document.createElement("div");
    account_info_body_address.className = "account_info_body_address";
    account_info_body_address.innerHTML = json.address;
    account_info_body.appendChild(account_info_body_address);

    // bio
    const account_info_body_bio = document.createElement("div");
    account_info_body_bio.className = "account_info_body_bio";
    account_info_body_bio.innerHTML = json.bio;
    account_info_body.appendChild(account_info_body_bio);

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


async function load_posts() {
    accounts_clear(div_accounts);

    const response = await fetch("/api/accounts");
    const json = await response.json();

    console.log(json)
    accounts_populate(div_accounts, json);
}

load_posts();

