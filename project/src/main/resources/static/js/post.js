const post_id = document.getElementById("post_id").value;
const thread = document.getElementById("thread");



function make_post_title(json) {
    const post_title = document.createElement("a");
    post_title.className = "post_title"
    post_title.innerHTML = json.title;
    post_title.href = "/posts/" + json.id;
    return post_title;
}

function make_post_content(json) {

    const post_content = document.createElement("div");
    post_content.className = "post_content"

    const post_text = document.createElement("span");
    post_text.className = "post_text"
    post_text.innerHTML = json.text;
    post_content.appendChild(post_text);

    if (json.picture != null) {
        const post_body_picture = document.createElement("img");
        post_body_picture.className = "post_picture";
        post_body_picture.src = json.picture;
        post_content.appendChild(post_body_picture);
    }

    return post_content;
}

function make_post_account(json) {
    const post_account_link = document.createElement("a");
    post_account_link.className = "post_account_link"
    post_account_link.href = "/accounts/" + json.account.id;

    const post_account = document.createElement("div");
    post_account.className = "post_account"

    const post_account_image = document.createElement("img");
    post_account_image.className = "post_account_image";
    post_account_image.src = json.account.avatar;
    post_account.appendChild(post_account_image);

    const post_account_userName = document.createElement("p");
    post_account_userName.innerHTML = json.account.userName;
    post_account_userName.className = "post_account_userName";
    post_account.appendChild(post_account_userName);

    post_account_link.appendChild(post_account);

    return post_account_link;
}

function make_post_controls(json) {
    const post_controls = document.createElement("div");
    post_controls.className = "post_controls"

    const post_controls_like = document.createElement("button");
    post_controls_like.className = "post_controls_like";
    post_controls_like.textContent = "❤️ Like";
    post_controls_like.onclick = () => post_like(json.id);
    post_controls.appendChild(post_controls_like);

    const post_controls_comment = document.createElement("button");
    post_controls_comment.className = "post_controls_comment";
    post_controls_comment.textContent = "💬 Reply";
    post_controls_comment.onclick = () => post_reply(json.id);
    post_controls.appendChild(post_controls_comment);

    post_controls.appendChild(make_post_account(json));

    return post_controls;
}

function make_post(json) {

    console.log(json);

    const post = document.createElement("div");
    post.className = "post";
    

    post.appendChild(make_post_title(json));
    post.appendChild(make_post_content(json));
    post.appendChild(make_post_controls(json));

    return post;
}

async function fetch_post(json, indent = 0) {

    console.log(json);

    // make post
    const post = make_post(json);
    post.style.marginLeft = `${indent}px`;

    // Append this post to the main thread container
    document.getElementById("thread").appendChild(post);

    // Check if there are replies and load them recursively
    if (json.replies > 0) {
        const response = await fetch(`/api/posts/${json.id}/replies`);
        const replies = await response.json();

        // Loop through replies and append each recursively with increased indentation
        for (let i = 0; i < replies.length; i++) {
            await fetch_post(replies[i], indent + 20); // Increase indentation for replies
        }
    }
}

async function load() {
    // Fetch the main post data
    const response = await fetch(`/api/posts/${post_id}`);
    if (!response.ok) {
        console.error("Failed to load post.");
        return;
    }
    
    const json = await response.json();

    // Load the main post (assuming json is a single post object)
    await fetch_post(json); // Start with the main post
}

// Start loading posts
load();