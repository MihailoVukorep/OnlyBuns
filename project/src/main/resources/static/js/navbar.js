let navbar = document.getElementById("navbar");

function navbar_mklink(text, link) {
    let a = document.createElement("a");
    a.innerHTML = text;
    a.href = link;
    return a;
}

function navbar_load_left() {
    let navbar_left = document.createElement("div");
    navbar_left.id = "navbar_left";

    navbar_left.append(navbar_mklink("🏠 Home", "/home"));
    navbar_left.append(navbar_mklink("📰 Posts", "/posts"));

    navbar.append(navbar_left);
}

async function navbar_load_right() {

    let navbar_right = document.createElement("div");
    navbar_right.id = "navbar_right";
    navbar.append(navbar_right);

    const response = await fetch("/api/myaccount");

    if (response.ok) {
        const json = await response.json();

        if (hasRole(json.roles, "ADMIN")) {
            navbar_right.append(navbar_mklink("👥 Accounts", "/admin/accounts"));
            navbar_right.append(navbar_mklink("🛠️ Management", "/admin/manage"));
        }

        navbar_right.append(navbar_mklink("🗺️ View Map", "/map"));
        navbar_right.append(navbar_mklink("➕ Create Post", "/createpost"));

        // account link
        const post_account = document.createElement("div");
        post_account.className = "onlybuns_account";
        post_account.className = "post_account"
        const post_account_link = document.createElement("a");
        post_account_link.className = "onlybuns_account_link";
        post_account_link.href = "/accounts/" + json.id;
        const post_account_image = document.createElement("img");
        post_account_image.className = "onlybuns_avatar";
        post_account_image.src = json.avatar;
        post_account_link.appendChild(post_account_image);
        const post_account_userName = document.createElement("span");
        post_account_userName.innerHTML = json.userName;
        //post_account_userName.className = "onlybuns_username";
        post_account_link.appendChild(post_account_userName);
        post_account.appendChild(post_account_link);
        navbar_right.append(post_account);

        navbar_right.append(navbar_mklink("⚙️ Update", "/myaccount/update"));
        navbar_right.append(navbar_mklink("🚪 Log out", "/logout"));
    }
    else {
        navbar_right.append(navbar_mklink("🔑 Log in", "/login"));
        navbar_right.append(navbar_mklink("📝 Register", "/register"));
    }
}

navbar_load_left();

async function init() {
    await loadjs('/js/roles.js');
    navbar_load_right();
}

init();
