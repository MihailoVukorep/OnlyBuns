const div_popup = document.getElementById("popup");

function popup_hide() {
    div_popup.style.display = "none";
}

function popup(text) {
    div_popup.style.display = "block";
    const popup_text = document.getElementById("popup_text");
    popup_text.innerHTML = text;
    setTimeout(function() { popup_hide(); }, 2000);
}