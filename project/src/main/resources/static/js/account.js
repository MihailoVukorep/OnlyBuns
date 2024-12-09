function drawFollowButton(button, following) {
    if (following) {
        button.textContent = `✅ Following`;
        button.classList.add("following");
    }
    else {
        button.textContent = `➕ Follow`;
        button.classList.remove("following");
    }
} 
async function follow(id) {
    const response = await fetch(`/api/accounts/${id}/follow`, { method: "POST" });
    const response_text = await response.text();

    const btn_follow = document.getElementById("btn_follow");

    if (response.ok) {
        if      (response_text == "Followed.")   { drawFollowButton(btn_follow, true);  }
        else if (response_text == "Unfollowed.") { drawFollowButton(btn_follow, false); }
        else                                     { popup(`✅ ${response_text}`);    }
    }
    else {
        popup(`❌ ${response_text}`);
    }

    // TODO: maybe refresh page so the counter updates, or do it manually
}