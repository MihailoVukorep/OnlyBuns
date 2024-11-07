loadScript('/js/roles.js');

let navbar = document.getElementById("navbar");

let navbar_static = document.createElement("div");
navbar_static.id = "navbar_left";

function createLink(text, link) {
    let a = document.createElement("a");
    a.innerHTML = text;
    a.href = link;
    return a;
}

navbar_static.append(createLink("🏠 Home",     "/home"));
navbar_static.append(createLink("📰 Posts",    "/posts"));


navbar.append(navbar_static);

let navbar_dynamic = document.createElement("div");
navbar_dynamic.id = "navbar_right";
navbar.append(navbar_dynamic);

async function load_dynamic() {
    const response = await fetch("/api/myaccount");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        
        if (hasRole(json.accountRoles, "ADMIN")) {
            navbar_dynamic.append(createLink("👥 Accounts", "/admin/accounts"));
            navbar_dynamic.append(createLink("🛠️ Management", "/admin/manage"));
        }

        navbar_dynamic.append(createLink("🗺️ View Map", "/map"));
        navbar_dynamic.append(createLink("➕ Create Post", "/createpost"));

        navbar_dynamic.append(createLink("👤", "/myaccount"));
        navbar_dynamic.append(createLink("⚙️", "/update"));
        navbar_dynamic.append(createLink("🚪 Log out (" + json.userName + ")", "/logout"));
    }
    else {
        navbar_dynamic.append(createLink("🔑 Log in", "/login"));
        navbar_dynamic.append(createLink("📝 Register", "/register"));
    }
}

load_dynamic();



