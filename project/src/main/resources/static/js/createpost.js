let btnCreatePost = document.getElementById("btn_createpost");
let txtTitle = document.getElementById("txt_title");
let txtDescription = document.getElementById("txt_description");
let txtLocation = document.getElementById("txt_location");

async function api_createpost(v_title, v_description, v_location) {
    const response = await fetch('/api/createpost', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({title: v_title, description: v_description, location: v_location})
    });
    const text = await response.text();

    console.log(response);

    console.log(text);
    p_status.innerHTML = text;

    if (response.ok) {
        window.location.href = "posts";
    }
}

function createpost() {
    api_createpost(txtTitle.value, txtDescription.value, txtLocation.value);
}
btnCreatePost.onclick = createpost;