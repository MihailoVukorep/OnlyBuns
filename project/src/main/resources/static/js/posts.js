function drawLikeButton(button, liked, likes) {
    if (liked) {
        button.textContent = `💗 ${likes}`;
        button.classList.add("liked");
    }
    else {
        button.textContent = `❤️ ${likes}`;
        button.classList.remove("liked");
    }
} 

async function post_like(id, button) {
    const response = await fetch(`/api/posts/${id}/like`, {method: "POST"});
    const response_text = await response.text();

    if (response.ok) {
        if (response_text == "Post liked.") {
            button.value++;
            drawLikeButton(button, true, button.value);
        }
        else if (response_text == "Post unliked.") {
            button.value--;
            drawLikeButton(button, false, button.value);
        }
        else {
            popup(`✅ ${response_text}`);
        }
    }

    else {
        popup(`❌ ${response_text}`);
    }
}

async function post_reply(id, title, text) {
    const response = await fetch(`/api/posts/${id}/replies`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({title, text})
    });
    const response_text = await response.text();

    if (response.ok) {popup(`✅ ${response_text}`);}
    else {popup(`❌ ${response_text}`);}

    hideCommentForm();
}

async function post_update(id) {
    console.log(`post_update called with id: ${id}`);
    const title = document.getElementById("txt_title").value;
    const text = document.getElementById("txt_text").value;
    const location = document.getElementById("txt_location").value;
    const imageFile = document.getElementById("file_image").files[0];
    const postId = document.getElementById("post_id").value;

    formData.append("title", title);
    formData.append("text", text);
    formData.append("location", location);
    if (imageFile) {
        formData.append("image", imageFile);
    }

    const response = await fetch(`/api/posts/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: formData
    });

    const response_text = await response.text();

    if (response.ok) {popup(`✅ ${response_text}`);}
    else {popup(`❌ ${response_text}`);}

    hideCommentForm();
}

async function post_delete(id) {

    const response = await fetch(`/api/posts/${id}`, {method: "DELETE"});
    const response_text = await response.text();

    if (response.ok) {popup(`✅ ${response_text}`);}
    else {popup(`❌ ${response_text}`);}

}

let commentFormCurrentId = null;

function showCommentForm(postId) {
    commentFormCurrentId = postId;
    document.getElementById("commentForm").style.display = "block";
}

function hideCommentForm() {
    document.getElementById("commentForm").style.display = "none";
    document.getElementById("commentTitle").value = "";
    document.getElementById("commentText").value = "";
}

function submitCommentForm() {
    post_reply(commentFormCurrentId, document.getElementById("commentTitle").value, document.getElementById("commentText").value);
    hideCommentForm();
}