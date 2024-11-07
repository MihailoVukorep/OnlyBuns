loadScript('/js/post_renderer.js');

const div_posts = document.getElementById("posts");

async function load_posts() {
    prune(div_posts);

    const sortOption = document.getElementById("sortOptions").value;
    const url = `/api/posts?sort=${sortOption}`;

    const response = await fetch(url);
    const items = await response.json();

    for (let i = 0; i < items.length; i++) {
        div_posts.append(make_post(items[i], true));
    }
}

load_posts();
