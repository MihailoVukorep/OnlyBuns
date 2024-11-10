const links = document.getElementById("links");

function index_link(text, link) {
    const a = document.createElement("a");
    a.classList.add("onlybuns_link");
    a.classList.add("onlybuns_link_block");
    a.innerHTML = text;
    a.href = link;
    return a;
}

async function load_index_links() {
    const response = await fetch("/api/user");

    if (response.ok) {
        const json = await response.json();

        if (hasRole(json.roles, "ADMIN")) {
            links.append(index_link("👥 Accounts", "/admin/accounts"));
            links.append(index_link("🛠️ Management", "/admin/manage"));
        }

        links.append(index_link("➕ Create Post", "/createpost"));
        links.append(index_link("💬 Chat", "/chat"));
        links.append(index_link("📈 Analytics", "/analytics"));
        links.append(index_link("🗺️ View Map", "/map"));
        links.append(index_link("👤 My Account", "/user"));
        links.append(index_link("⚙️ My Account - Update", "/user/update"));
        links.append(index_link("🚪 Log out (" + json.userName + ")", "/logout"));
    }
    else {
        links.append(index_link("🔑 Log in", "/login"));
        links.append(index_link("📝 Register", "/register"));
    }
}


async function init() {
    await loadjs('/js/roles.js');
    load_index_links();
}

init();

