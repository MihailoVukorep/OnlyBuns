
const div_posts = document.getElementById("posts");

function posts_clear(div_posts) {
    while (div_posts.firstChild) {
        div_posts.removeChild(div_posts.lastChild);
    }
}


async function load_posts() {
    posts_clear(div_posts);

    const sortOption = document.getElementById("sortOptions").value;
    const url = `/api/posts?sort=${sortOption}`;

    const response = await fetch(url);
    const items = await response.json();

    for (let i = 0; i < items.length; i++) {
        div_posts.append(make_post(items[i], true));
    }
}

load_posts();
