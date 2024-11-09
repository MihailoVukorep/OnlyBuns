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

function make_post_content(json) {

    const post_content = document.createElement("div");
    post_content.className = "post_content";

    const post_title = document.createElement("span");
    post_title.className = "post_title";
    post_title.innerHTML = json.title;
    post_content.appendChild(post_title);

    const post_text = document.createElement("span");
    post_text.className = "post_text";
    post_text.innerHTML = json.text;
    post_content.appendChild(post_text);

    if (json.picture != null) {
        const post_picture = document.createElement("img");
        post_picture.className = "post_picture";
        post_picture.src = json.picture;
        post_content.appendChild(post_picture);
    }

    return post_content;
}

function make_post_body(json, link_body) {

    const post_body = document.createElement("div");
    post_body.className = "post_body";
    post_body.appendChild(make_post_content(json));

    if (link_body) {
        const post_body_link = document.createElement("a");
        post_body_link.className = "post_body_link";
        post_body_link.href = "/posts/" + json.id;
        post_body_link.appendChild(post_body);
        return post_body_link;
    }

    return post_body;
}

function make_post_account(json) {

    const post_account = document.createElement("div");
    post_account.className = "post_account"

    const post_account_link = document.createElement("a");
    post_account_link.className = "post_account_link"
    post_account_link.href = "/accounts/" + json.account.id;

    const post_account_image = document.createElement("img");
    post_account_image.className = "post_account_image";
    post_account_image.src = json.account.avatar;
    post_account_link.appendChild(post_account_image);

    const post_account_userName = document.createElement("span");
    post_account_userName.innerHTML = json.account.userName;
    post_account_userName.className = "post_account_userName";
    post_account_link.appendChild(post_account_userName);

    post_account.appendChild(post_account_link);

    return post_account;
}


function make_post_controls(json) {
    const post_controls = document.createElement("div");
    post_controls.className = "post_controls";

    const post_controls_like = document.createElement("button");
    post_controls_like.className = "post_controls_like";
    post_controls_like.value = parseInt(json.likes, 10);
    drawLikeButton(post_controls_like, json.liked, json.likes);
    post_controls_like.onclick = () => post_like(json.id, post_controls_like);
    post_controls.appendChild(post_controls_like);

    const post_controls_comment = document.createElement("button");
    post_controls_comment.className = "post_controls_comment";
    post_controls_comment.textContent = `💬 ${json.totalChildren}`;
    post_controls_comment.onclick = () => showCommentForm(json.id);
    //post_controls_comment.onclick = () => showCommentForm(json.id);
    post_controls.appendChild(post_controls_comment);

    if (json.myPost) {
        const post_controls_update = document.createElement("button");
        post_controls_update.className = "post_controls_update";
        post_controls_update.textContent = `📝`;
        post_controls_update.onclick = () => post_update(json.id);
        post_controls.appendChild(post_controls_update);

        const post_controls_delete = document.createElement("button");
        post_controls_delete.className = "post_controls_delete";
        post_controls_delete.textContent = `🗑`;
        post_controls_delete.onclick = () => post_delete(json.id);
        post_controls.appendChild(post_controls_delete);
    }

    post_controls.appendChild(make_post_account(json));

    return post_controls;
}

function make_post(json, link_body=false) {

    const post = document.createElement("div");
    post.className = "post";

    post.appendChild(make_post_body(json, link_body));
    post.appendChild(make_post_controls(json));

    return post;
}
