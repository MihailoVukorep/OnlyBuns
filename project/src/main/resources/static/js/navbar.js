loadScript('/js/roles.js');

let navbar = document.getElementById("navbar");

// STATIC BUTTONS
let div1 = document.createElement("div");
div1.id = "navbar_left";

function createLink(text, link) {
    let a = document.createElement("a");
    a.innerHTML = text;
    a.href = link;
    return a;
}

div1.append(createLink("🏠 Home",     "/home"));
div1.append(createLink("📰 Posts",    "/posts"));
div1.append(createLink("👥 Accounts", "/accounts"));

navbar.append(div1);

let div2 = document.createElement("div");
div2.id = "navbar_right";
navbar.append(div2);

async function load_dynamic() {
    const response = await fetch("/api/myaccount");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        
        if (hasRole(json.accountRoles, "ADMIN")) {
            div2.append(createLink("🛠️ Management", "/admin/manage"));
        }

        div2.append(createLink("👤", "/myaccount"));
        div2.append(createLink("⚙️", "/update"));
        div2.append(createLink("🚪 Log out (" + json.userName + ")", "/logout"));
        div1.append(createLink("🗺️ View Map", "/map"));
        div1.append(createLink("➕ Create Post", "/createpost"));

    }
    else {
        div2.append(createLink("🔑 Log in", "/login"));
        div2.append(createLink("📝 Register", "/register"));
    }
}

load_dynamic();



