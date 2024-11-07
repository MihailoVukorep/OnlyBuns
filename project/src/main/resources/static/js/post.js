const post_id = document.getElementById("post_id").value;
const thread = document.getElementById("thread");


async function create_post(json, indent = 0) {

    console.log(json);

    // Create the main post container
    const post = document.createElement("div");
    post.className = "post";
    post.style.marginLeft = `${indent}px`; // Indentation for visual hierarchy

    const post_title = document.createElement("p");
    post_title.className = "post_title";
    post_title.innerHTML = json.title;
    post.appendChild(post_title);

    if (json.picture != null) {
        const post_picture = document.createElement("img");
        post_picture.className = "post_picture";
        post_picture.src = json.picture;
        post.appendChild(post_picture);
    }

    const post_text = document.createElement("p");
    post_text.className = "post_text";
    post_text.innerHTML = json.text;
    post.appendChild(post_text);

    // Append this post to the main thread container
    document.getElementById("thread").appendChild(post);

    // Check if there are replies and load them recursively
    if (json.replies > 0) {
        const response = await fetch(`/api/posts/${json.id}/replies`);
        const replies = await response.json();

        // Loop through replies and append each recursively with increased indentation
        for (let i = 0; i < replies.length; i++) {
            await create_post(replies[i], indent + 20); // Increase indentation for replies
        }
    }
}

async function load_post() {
    // Fetch the main post data
    const response = await fetch(`/api/posts/${post_id}`);
    if (!response.ok) {
        console.error("Failed to load post.");
        return;
    }
    
    const json = await response.json();

    // Load the main post (assuming json is a single post object)
    await create_post(json); // Start with the main post
}

// Start loading posts
load_post();