
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

    const p_title = document.createElement("a");
    p_title.className = "post_title"
    p_title.innerHTML = item.title;
    p_title.href = "/posts/" + item.id;
    post_head.appendChild(p_title);

    const p_account = document.createElement("a");
    p_account.className = "post_account"
    p_account.innerHTML = item.account.userName;
    p_account.href = "/accounts/" + item.account.id;
    post_head.appendChild(p_account);

    post.appendChild(post_head);

    const post_body = document.createElement("div");
    post_body.className = "post_body";

    const p_text = document.createElement("p");
    p_text.className = "post_text"
    p_text.innerHTML = item.text;
    post_body.appendChild(p_text);

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
const txt_search = document.getElementById("txt_search");
const btn_search = document.getElementById("btn_search");
function search() { load_posts(txt_search.value); }
txt_search.addEventListener("keydown", function(event) { if (event.key == 'Enter') { search(); } }, false);
btn_search.onclick = function() { search(); }