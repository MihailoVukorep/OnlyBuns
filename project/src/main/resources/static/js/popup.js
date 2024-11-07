function popup(text) {
    const popup = document.getElementById("popup");
    popup.style.display = "block";
    const popup_text = document.getElementById("popup_text");
    popup_text.innerHTML = text;

    setTimeout(function() { popup.style.display = "none"; }, 2000);
}

function popup_init() {
    const popup = document.getElementById("popup");
    popup.onclick = function () {
        popup.style.display = "none";
    }
}

popup_init();