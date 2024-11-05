let txt_email = document.getElementById("txt_email");
let txt_password = document.getElementById("txt_password");
let btn_login = document.getElementById("btn_login");
let p_status = document.getElementById("p_status");


txt_email.focus();
txt_email.select();


async function api_login(v_username, v_password) {

    p_status.innerHTML = "Logging in...";

    const response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({email: v_username, password: v_password})
    });


    const text = await response.text();

    console.log(response);

    console.log(text);
    p_status.innerHTML = text;

    if (response.ok) {
        window.location.href = "myaccount";
    }
}




function login() {
    api_login(txt_email.value, txt_password.value);
}


btn_login.onclick = login;

txt_email.addEventListener("keydown", function(event) { if (event.key == 'Enter') { txt_password.focus(); } }, false);
txt_password.addEventListener("keydown", function(event) { if (event.key == 'Enter') { login(); } }, false);




