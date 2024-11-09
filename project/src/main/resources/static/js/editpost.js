let btnEditPost = document.getElementById("btn_editpost");
let fileImage = document.getElementById("file_image");
let imagePreview = document.getElementById("image-preview");
let pStatus = document.getElementById("p_status");

let postId = getPostIdFromUrl();

document.addEventListener('DOMContentLoaded', function() {
    fetchPostData(postId);
});

function getPostIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

async function fetchPostData(postId) {
    const response = await fetch(`/api/posts/${postId}`);

    if (response.ok) {
        const postData = await response.json();

        document.getElementById('title').value = postData.title;
        document.getElementById('description').value = postData.text;
        document.getElementById('location').value = postData.location;

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

// Funkcija za kreiranje posta
async function api_editpost() {
    console.log(`post_update called with id: ${id}`);
    const formData = new FormData(document.getElementById('postForm'));
    const postId = getPostIdFromUrl();

    // Adjust the endpoint to match the REST controller path
    const response = await fetch(`/api/posts/${postId}`, {
        method: 'PUT',
        body: formData
    });

    const text = await response.text();
    pStatus.innerHTML = text;

    if (response.ok) {
        window.location.href = "/posts";  // Redirect after a successful edit
    }
}

btnEditPost.onclick = api_editpost;
