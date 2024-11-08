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

function showCommentForm(postId) {
    document.getElementById("post_id").value = postId;

    document.getElementById("commentForm").style.display = "block";
}

function hideCommentForm() {
    document.getElementById("commentForm").style.display = "none";
    document.getElementById("commentTitle").value = "";
    document.getElementById("commentText").value = "";
}

async function post_reply() {
    const title = document.getElementById("commentTitle").value;
    const text = document.getElementById("commentText").value;
    const postId = document.getElementById("post_id").value;

    const response = await fetch(`/api/posts/${postId}/replies`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ title, text })
    });

    if (response.ok) {
        popup("Comment posted successfully!");
        hideCommentForm();
    } else {
        popup("Failed to post comment.");
    }
}

function editPost() {
    const post_id = document.getElementById("post_id").value;
    window.location.href = `/posts/{post_id}/edit`; 
}


