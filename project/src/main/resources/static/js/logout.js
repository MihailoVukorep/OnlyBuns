async function api_logout() {
    await fetch('/api/logout', { method: 'POST' });
    window.location.href = "home";
}

api_logout();
