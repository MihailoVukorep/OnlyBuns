let btnEditPost = document.getElementById("btn_editpost");
let fileImage = document.getElementById("file_image");
let imagePreview = document.getElementById("image-preview");
let pStatus = document.getElementById("p_status");

let postId = getPostIdFromUrl();

document.addEventListener('DOMContentLoaded', function() {
    fetchPostData(postId);
});

function getPostIdFromUrl() {
    const paths = window.location.pathname.split("/");
    const id = paths[paths.length - 1];

    return id;


}

async function fetchPostData(postId) {
    const response = await fetch(`/api/posts/${postId}`);

    if (response.ok) {
        const postData = await response.json();

        document.getElementById('txt_title').value = postData.title;
        document.getElementById('txt_text').value = postData.text; // Updated to 'text'
        document.getElementById('txt_location').value = postData.location;

        if (postData.picture) {
            const img = document.createElement("img");
            img.src = postData.picture;
            imagePreview.innerHTML = "";
            imagePreview.appendChild(img);
            imagePreview.style.display = "block";
        }
    } else {
        pStatus.innerHTML = "Error loading post data.";
    }
}

fileImage.addEventListener("change", function(e) {
    handleImageUpload(e.target.files[0]);
});


function handleImageUpload(file) {
    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            const img = document.createElement("img");
            img.src = event.target.result;
            imagePreview.innerHTML = "";
            imagePreview.appendChild(img);
            imagePreview.style.display = "block";
        };
        reader.readAsDataURL(file);
    }
}

async function post_update(id) {
    const formData = new FormData();
    const title = document.getElementById("txt_title").value;
    const text = document.getElementById("txt_text").value; // Updated to 'text'
    const location = document.getElementById("txt_location").value;
    const imageFile = document.getElementById("file_image").files[0];

    formData.append("title", title);
    formData.append("text", text); // Updated to 'text'
    formData.append("location", location);
    if (imageFile) {
        formData.append("image", imageFile);
    }

    const response = await fetch(`/api/posts/${id}`, {
        method: "PUT",
        body: formData,
    });

    const responseText = await response.text();
    popup(response.ok ? `✅ ${responseText}` : `❌ ${responseText}`);
    hideCommentForm();
}


async function api_editpost() {
    console.log(`api_editpost called with id: ${postId}`);
    const formData = new FormData(document.getElementById('postForm'));
    const id = getPostIdFromUrl();

    const response = await fetch(`/api/posts/${postId}`, {
        method: 'PUT',
        body: formData
    });

    const text = await response.text();
    pStatus.innerHTML = text;

    if (response.ok) {
        window.location.href = `/posts/${postId}`;
    }
    
}

btnEditPost.onclick = api_editpost;
