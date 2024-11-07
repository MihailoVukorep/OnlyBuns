const account_id = document.getElementById("account_id").value;

async function load_account() {
    const response_users_id = await fetch("/api/accounts/" + account_id);
    const response_users_id_json = await response_users_id.json();
    
    // display user
    document.getElementById("account_email").innerHTML       = response_users_id_json.email;
    document.getElementById("account_userName").innerHTML    = response_users_id_json.userName;
    document.getElementById("account_firstName").innerHTML   = response_users_id_json.firstName;
    document.getElementById("account_lastName").innerHTML    = response_users_id_json.lastName;
    document.getElementById("account_address").innerHTML     = response_users_id_json.address;
    document.getElementById("account_avatar").src            = response_users_id_json.avatar;
    document.getElementById("account_bio").innerHTML         = response_users_id_json.bio;

    const role = document.getElementById("account_accountRole");
    if (response_users_id_json.accountRole == "USER") {
        role.innerHTML = "👤";
        role.title = "User"
    }
    else if (response_users_id_json.accountRole == "ADMIN") {
        role.innerHTML = "👑";
        role.title = "Admin"
    }
}

load_account();