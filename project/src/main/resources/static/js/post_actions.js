async function post_like(id) {
    const response = await fetch("/api/myaccount");
    if (!response.ok) {
        popup("You need to login first.");
        return;
    }
    console.log(`like: ${id}`);
}

async function post_reply(id) {
    const response = await fetch("/api/myaccount");
    if (!response.ok) {
        popup("You need to login first.");
        return;
    }
    console.log(`reply: ${id}`);
}