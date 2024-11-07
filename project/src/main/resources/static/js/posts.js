
const div_posts = document.getElementById("posts");

function posts_clear(div_posts) {
    while (div_posts.firstChild) {
        div_posts.removeChild(div_posts.lastChild);
    }
}

function make_post_title(json) {
    const post_title = document.createElement("span");
    post_title.className = "post_title";

    return post_title;
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

function make_post_body(json) {

    const post_body_link = document.createElement("a");
    post_body_link.className = "post_body_link";
    post_body_link.href = "/posts/" + json.id;

    const post_body = document.createElement("div");
    post_body.className = "post_body";
    post_body.appendChild(make_post_content(json));

    post_body_link.appendChild(post_body);
    return post_body_link;
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

    const post_account_userName = document.createElement("p");
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
    post_controls_like.textContent = "❤️ Like";
    post_controls.appendChild(post_controls_like);

    const post_controls_comment = document.createElement("button");
    post_controls_comment.className = "post_controls_comment";
    post_controls_comment.textContent = "💬 Comment";
    post_controls.appendChild(post_controls_comment);

    post_controls.appendChild(make_post_account(json));

    return post_controls;
}

function make_post(json) {

    console.log(json);

    const post = document.createElement("div");
    post.className = "post";

    post.appendChild(make_post_body(json));
    post.appendChild(make_post_controls(json));

    return post;
}

async function load() {
    posts_clear(div_posts);

    const sortOption = document.getElementById("sortOptions").value;
    const url = `/api/posts?sort=${sortOption}`;

    const response = await fetch(url);
    const items = await response.json();

    for (let i = 0; i < items.length; i++) {
        div_posts.append(make_post(items[i]));
    }
}

load();
