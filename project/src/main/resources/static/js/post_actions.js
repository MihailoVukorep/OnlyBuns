async function post_like(id) {
    const response = await fetch(`/api/posts/${id}/like`, { method: "POST" });
    const response_text = await response.text();

    if (response.ok) { popup(`✅ ${response_text}`); }
    else             { popup(`❌ ${response_text}`); }
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
    const title = document.getElementById("commentTitle").value;
    const text = document.getElementById("commentText").value;
    const postId = document.getElementById("post_id").value;

    const response = await fetch(`/api/posts/${postId}`, {
        method: "PUT",
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

async function post_delete(id) {

    const response = await fetch(`/api/posts/${id}`, { method: "DELETE" });
    const response_text = await response.text();

    if (response.ok) { popup(`✅ ${response_text}`); }
    else             { popup(`❌ ${response_text}`); }

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
