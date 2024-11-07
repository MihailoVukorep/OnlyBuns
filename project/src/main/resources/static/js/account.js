loadScript('/js/roles.js');

const account_id = document.getElementById("account_id").value;

async function load_account() {
    const response = await fetch("/api/accounts/" + account_id);
    const json = await response.json();
    
    // display user
    document.getElementById("account_email").innerHTML       = json.email;
    document.getElementById("account_userName").innerHTML    = json.userName;
    document.getElementById("account_firstName").innerHTML   = json.firstName;
    document.getElementById("account_lastName").innerHTML    = json.lastName;
    document.getElementById("account_address").innerHTML     = json.address;
    document.getElementById("account_avatar").src            = json.avatar;
    document.getElementById("account_bio").innerHTML         = json.bio;
    const role = document.getElementById("account_roles");
    role.appendChild(roles(json.roles))

    document.getElementById("account_postsCount").innerHTML     = json.postsCount;
    document.getElementById("account_followingCount").innerHTML = json.followingCount;
    document.getElementById("account_followersCount").innerHTML = json.followersCount;

}

load_account();
