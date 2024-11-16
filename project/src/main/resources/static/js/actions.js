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
    const response = await fetch(`/api/posts/${id}/like`, { method: "POST" });
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
        body: JSON.stringify({ title, text })
    });
    const response_text = await response.text();

    if (response.ok) { popup(`✅ ${response_text}`); }
    else             { popup(`❌ ${response_text}`); }

    hideCommentForm();
}

async function post_update(id) {
    window.location.href = `/posts/${id}/edit`;
}

async function post_delete(id) {

    const response = await fetch(`/api/posts/${id}`, { method: "DELETE" });
    const response_text = await response.text();

    if (response.ok) { popup(`✅ ${response_text}`); }
    else             { popup(`❌ ${response_text}`); }

}

let commentFormCurrentId = null;
const commentForm = document.getElementById("commentForm");
const commentTitle = document.getElementById("commentTitle");
const commentText = document.getElementById("commentText");

function showCommentForm(postId) {
    commentFormCurrentId = postId;
    commentForm.style.display = "block";

    commentTitle.select();
    commentTitle.focus();
}

function hideCommentForm() {
    commentForm.style.display = "none";
    commentTitle.value = "";
    commentText.value = "";
}

function submitCommentForm() {
    post_reply(commentFormCurrentId, commentTitle.value, commentText.value);
    hideCommentForm();
}

commentTitle.addEventListener("keydown", function (event) {
    if (event.key == 'Enter') { commentText.focus(); event.preventDefault(); }
    if (event.key == 'Escape') { hideCommentForm(); }
}, false);
commentText.addEventListener("keydown", function (event) {
    if (event.key == 'Enter') { submitCommentForm(); }
    if (event.key == 'Escape') { hideCommentForm(); }
}, false);
