// get roles to spans
function roles(json) {

    function make_role(parent, json, name, emoji, text) {
        if (json.name === name) {
            const span_role = document.createElement("span");
            span_role.title = `${emoji} ${text}`;
            span_role.innerHTML = emoji;
            parent.appendChild(span_role);
        }
    }

    const roles_span = document.createElement("span");

    for (let i = 0; i < json.length; i++) {
        make_role(roles_span, json[i], "USER", "👤", "User");
        make_role(roles_span, json[i], "ADMIN", "👑", "Admin");
    }

    return roles_span;
}


// check if role exists
function hasRole(roles, role) {
    return roles.some(r => r.name === role);
}

