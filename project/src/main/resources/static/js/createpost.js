let btnCreatePost = document.getElementById("btn_createpost");
let txtTitle = document.getElementById("txt_title");
let txtDescription = document.getElementById("txt_description");
let txtLocation = document.getElementById("txt_location");
let fileInput = document.getElementById("file_input");
let p_status = document.getElementById("p_status");


async function api_createpost(v_title, v_description, v_location, v_file) {
    const formData = new FormData();
    formData.append("title", v_title);
    formData.append("description", v_description);
    formData.append("location", v_location);
    if (v_file) {
        formData.append("file", v_file);
    );
    const response = await fetch('/api/createpost', {
        method: 'POST',
        body: formData
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
    const file = fileInput.files[0];
    api_createpost(txtTitle.value, txtDescription.value, txtLocation.value, file);
}
btnCreatePost.onclick = createpost;
