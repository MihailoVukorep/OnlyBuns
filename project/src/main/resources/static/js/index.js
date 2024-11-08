loadScript('/js/roles.js');

const links = document.getElementById("links");

function createLink(text, link) {
    const a = document.createElement("a");
    a.classList.add("onlybuns_link");
    a.classList.add("onlybuns_link_block");
    a.innerHTML = text;
    a.href = link;
    return a;
}

async function navbar_load_dynamic() {
    const response = await fetch("/api/myaccount");

    const links_dynamic = document.getElementById("links");

    if (response.ok) {
        const json = await response.json();
        
        if (hasRole(json.roles, "ADMIN")) {
            links_dynamic.append(createLink("👥 Accounts", "/admin/accounts"));
            links_dynamic.append(createLink("🛠️ Management", "/admin/manage"));
        }

        links_dynamic.append(createLink("🗺️ View Map", "/map"));
        links_dynamic.append(createLink("➕ Create Post", "/createpost"));

        links_dynamic.append(createLink("👤 My Account", "/myaccount"));
        links_dynamic.append(createLink("⚙️ My Account - Update", "/myaccount/update"));
        links_dynamic.append(createLink("🚪 Log out (" + json.userName + ")", "/logout"));
    }
    else {
        links_dynamic.append(createLink("🔑 Log in", "/login"));
        links_dynamic.append(createLink("📝 Register", "/register"));
    }
}

navbar_load_dynamic();

