const post_id = document.getElementById("post_id").value;

function make_comment(post) {

}

async function load_post() {
    const response = await fetch("/api/posts/" + post_id);
    if (!response.ok) { return }
    const json = await response.json();

    console.log(json);

    const v_title = document.getElementById("v_title");
    v_title.innerHTML = json.title;
    
    const v_text = document.getElementById("v_text");
    v_text.innerHTML = json.text;
    
    let div_comments = document.getElementById("v_comments");

    for (let i = 0; i < json.comments.length; i++) {
        div_comments.appendChild(make_comment(json.bookReviews[i]));
    }
}

load_post();