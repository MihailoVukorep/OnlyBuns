
const btn = document.getElementById("btn_search");
const div_posts = document.getElementById("div_posts");

function posts_clear(div_posts) {
    while (div_posts.firstChild) {
        div_posts.removeChild(div_posts.lastChild);
    }
}

function make_post_account(json) {
    const post_account = document.createElement("a");
    post_account.className = "post_account"
    post_account.href = "/accounts/" + json.account.id;

    const post_account_image = document.createElement("img");
    post_account_image.className = "post_account_image";
    post_account_image.src = json.account.avatar;
    post_account.appendChild(post_account_image);

    const post_account_userName = document.createElement("p");
    post_account_userName.innerHTML = json.account.userName;
    post_account_userName.className = "post_account_userName";
    post_account.appendChild(post_account_userName);

    return post_account;
}

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

function make_post_controls(json) {
    const post_controls = document.createElement("div");
    post_controls.className = "post_controls"

    const post_controls_like = document.createElement("button");
    post_controls_like.className = "post_controls_like";
    post_controls_like.textContent = "❤️ Like";
    post_controls.appendChild(post_controls_like);

    const post_controls_comment = document.createElement("button");
    post_controls_comment.className = "post_controls_comment";
    post_controls_comment.textContent = "💬 Comment";
    post_controls.appendChild(post_controls_comment);

    return post_controls;
}

function make_post(json) {

    console.log(json);

    const post = document.createElement("div");
    post.className = "post";

    post.appendChild(make_post_account(json));
    post.appendChild(make_post_title(json));
    post.appendChild(make_post_content(json));
    post.appendChild(make_post_controls(json));

    return post;
}

async function load() {
    posts_clear(div_posts);

    const sortOption = document.getElementById("sortOptions").value
    const url = `/api/posts?sort=${sortOption}`;

    const response = await fetch(url);
    const items = await response.json();

    for (let i = 0; i < items.length; i++) {
        div_posts.append(make_post(items[i]));
    }
}

load();



// SEARCH
//   const txt_search = document.getElementById("txt_search");
//   const btn_search = document.getElementById("btn_search");
//   function search() { load_posts(txt_search.value); }
//   txt_search.addEventListener("keydown", function(event) { if (event.key == 'Enter') { search(); } }, false);
//   btn_search.onclick = function() { search(); }