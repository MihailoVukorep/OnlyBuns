async function post_like(id) {
    const response = await fetch("/api/myaccount");
    if (!response.ok) {
        popup("You need to login first.");
        return;
    }
    //console.log(`like: ${id}`);
    try {
        const likeResponse = await fetch(`/api/posts/${id}/like`, {
            method: "POST"
        });

        if (likeResponse.ok) {
            popup("Post liked!");
        } else {
            const message = await likeResponse.text();
            popup(`Failed to like post: ${message}`);
        }
    } catch (error) {
        console.error("Error liking post:", error);
        popup("An error occurred while liking the post.");
    }
}

async function post_reply(id) {
    const response = await fetch("/api/myaccount");
    if (!response.ok) {
        popup("You need to login first.");
        return;
    }
    console.log(`reply: ${id}`);
}