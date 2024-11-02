let user_id = document.getElementById("user_id").value;

function displayUserControls() {

}

async function load_account() {
    const response_users_id = await fetch("/api/users/" + user_id);
    const response_users_id_json = await response_users_id.json();
    console.log(response_users_id_json);
    
    // display user
    document.getElementById("user_firstName").innerHTML   = json.firstName;
    document.getElementById("user_lastName").innerHTML    = json.lastName;
    document.getElementById("user_username").innerHTML    = json.username;
    document.getElementById("user_mailAddress").innerHTML = json.mailAddress;
    document.getElementById("user_dateOfBirth").innerHTML = json.dateOfBirth;
    document.getElementById("user_description").innerHTML = json.description;
    document.getElementById("user_accountRole").innerHTML = json.accountRole;
    document.getElementById("user_profilePicture").src    = json.profilePicture;

    const response_myaccount = await fetch("/api/myaccount");

    if (response_myaccount.ok) {

        console.log(window.location.pathname);
        if (window.location.pathname == "/myaccount") {
            displayUserControls();
        }
    }
}

load_account();