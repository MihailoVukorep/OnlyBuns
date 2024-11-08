loadScript('/js/post_renderer.js');

const post_id = document.getElementById("post_id").value;
const thread = document.getElementById("thread");


async function fetch_post(json, indent = 0) {

    // make post
    const post = make_post(json, false);
    post.style.marginLeft = `${indent}px`;

    // Append this post to the main thread container
    thread.appendChild(post);

    // Check if there are replies and load them recursively
    if (json.replies > 0) {
        const response = await fetch(`/api/posts/${json.id}/replies`);
        const replies = await response.json();

        // Loop through replies and append each recursively with increased indentation
        for (let i = 0; i < replies.length; i++) {
            await fetch_post(replies[i], indent + 30); // Increase indentation for replies
        }
    }
}

async function load_posts() {
    // Fetch the main post data
    const response = await fetch(`/api/posts/${post_id}`);
    if (!response.ok) {
        console.error("Failed to load post.");
        return;
    }
    
    const json = await response.json();

    if (json.parentPostId != null) {
        const parent = document.getElementById("parent");
        parent.href = `/posts/${json.parentPostId}`
        parent.style.display = "block";
    }

    // const loggedInUserId = document.getElementById("logged_in_user_id").value;
    // if (json.userId === loggedInUserId) {
    //     document.getElementById("editPostButton").style.display = "block";
    // }

    // Load the main post (assuming json is a single post object)
    await fetch_post(json); // Start with the main post
}

// Start loading posts
load_posts();
