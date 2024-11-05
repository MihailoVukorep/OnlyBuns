
const btn = document.getElementById("btn_search");
const div_posts = document.getElementById("div_posts");

function posts_clear(div_posts) {
    while (div_posts.firstChild) {
        div_posts.removeChild(div_posts.lastChild);
    }
}

function make_post(item) {

    const post = document.createElement("div");
    post.className = "post";

    const post_head = document.createElement("div");
    post_head.className = "post_head";

    const post_head_account = document.createElement("a");
    post_head_account.className = "post_account"
    post_head_account.href = "/accounts/" + item.account.id;

    const post_head_account_image = document.createElement("img");
    post_head_account_image.className = "post_account_image";
    post_head_account_image.src = item.account.avatar;
    post_head_account.appendChild(post_head_account_image);

    const post_head_account_userName = document.createElement("p");
    post_head_account_userName.innerHTML = item.account.userName;
    post_head_account_userName.className = "post_account_userName";
    post_head_account.appendChild(post_head_account_userName);

    post_head.appendChild(post_head_account);


    const post_head_title = document.createElement("a");
    post_head_title.className = "post_title"
    post_head_title.innerHTML = item.title;
    post_head_title.href = "/posts/" + item.id;
    post_head.appendChild(post_head_title);

    post.appendChild(post_head);

    const post_body = document.createElement("div");
    post_body.className = "post_body";

    const post_body_text = document.createElement("p");
    post_body_text.className = "post_text"
    post_body_text.innerHTML = item.text;
    post_body.appendChild(post_body_text);

    post.appendChild(post_body);
    return post;
}

async function load_posts(search = "") {
    posts_clear(div_posts);

    const url = "/api/posts"
    if (search != "") { url = "/api/posts?s=" + search }
    const response = await fetch(url);
    const items = await response.json();

    for (let i = 0; i < items.length; i++) {
        div_posts.append(make_post(items[i]));
    }
}

load_posts();



// SEARCH
//   const txt_search = document.getElementById("txt_search");
//   const btn_search = document.getElementById("btn_search");
//   function search() { load_posts(txt_search.value); }
//   txt_search.addEventListener("keydown", function(event) { if (event.key == 'Enter') { search(); } }, false);
//   btn_search.onclick = function() { search(); }