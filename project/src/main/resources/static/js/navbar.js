loadScript('/js/roles.js');

let navbar = document.getElementById("navbar");

function navbar_mklink(text, link) {
    let a = document.createElement("a");
    a.innerHTML = text;
    a.href = link;
    return a;
}

function navbar_load_static() {
    let links_static = document.createElement("div");
    links_static.id = "navbar_left";

    links_static.append(navbar_mklink("🏠 Home",     "/home"));
    links_static.append(navbar_mklink("📰 Posts",    "/posts"));

    navbar.append(links_static);
}

async function navbar_load_dynamic() {

    let links_dynamic = document.createElement("div");
    links_dynamic.id = "navbar_right";
    navbar.append(links_dynamic);

    const response = await fetch("/api/myaccount");

    if (response.ok) {
        const json = await response.json();
        
        if (hasRole(json.roles, "ADMIN")) {
            links_dynamic.append(navbar_mklink("👥 Accounts", "/admin/accounts"));
            links_dynamic.append(navbar_mklink("🛠️ Management", "/admin/manage"));
        }

        links_dynamic.append(navbar_mklink("🗺️ View Map", "/map"));
        links_dynamic.append(navbar_mklink("➕ Create Post", "/createpost"));

        links_dynamic.append(navbar_mklink("👤", "/myaccount"));
        links_dynamic.append(navbar_mklink("⚙️", "/myaccount/update"));
        links_dynamic.append(navbar_mklink("🚪 Log out (" + json.userName + ")", "/logout"));
    }
    else {
        links_dynamic.append(navbar_mklink("🔑 Log in", "/login"));
        links_dynamic.append(navbar_mklink("📝 Register", "/register"));
    }
}

navbar_load_static();
navbar_load_dynamic();



