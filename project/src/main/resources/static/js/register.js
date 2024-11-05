let btn_register = document.getElementById("btn_register");
let p_status = document.getElementById("p_status");

async function api_register() {

    p_status.innerHTML = "Registering..."

    let txt_email = document.getElementById("txt_email");
    let txt_userName = document.getElementById("txt_userName");
    let txt_password = document.getElementById("txt_password");
    let txt_password_repeat = document.getElementById("txt_password_repeat");
    let txt_firstName = document.getElementById("txt_firstName");
    let txt_lastName = document.getElementById("txt_lastName");
    let txt_address = document.getElementById("txt_address");

    if (txt_password.value != txt_password_repeat.value) {
        p_status.innerHTML = "Passwords don't match";
        return;
    }

    const response = await fetch('/api/register', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email:     txt_email.value,
            userName:  txt_userName.value,
            password:  txt_password.value,
            firstName: txt_firstName.value,
            lastName:  txt_lastName.value,
            address:   txt_address.value,
        })
    });

    const text = await response.text();

    console.log(response);
    p_status.innerHTML = text;

    //if (response.ok) {
    //    window.location.href = "myaccount"; // no redirect needs verification
    //}
}

btn_register.onclick = api_register;


// TODO: auto focus
// txt_username.addEventListener("keydown", function(event) { if (event.key == 'Enter') { txt_password.focus(); } }, false);
// txt_password.addEventListener("keydown", function(event) { if (event.key == 'Enter') { register(); } }, false);




