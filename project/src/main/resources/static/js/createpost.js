let btnCreatePost = document.getElementById("btn_createpost");
let fileImage = document.getElementById("file_image");
let imagePreview = document.getElementById("image-preview");
let pStatus = document.getElementById("p_status");

// Kada se promeni odabrana slika, prikazujemo je
fileImage.addEventListener("change", function (e) {
    handleImageUpload(e.target.files[0]);
});

// Funkcija za prikaz slike u elementu za pregled
function handleImageUpload(file) {
    if (file) {
        const reader = new FileReader();
        reader.onload = function (event) {
            const img = document.createElement("img");
            img.src = event.target.result;
            imagePreview.innerHTML = "";  // Brišemo prethodni pregled
            imagePreview.appendChild(img);
            imagePreview.style.display = "block";  // Prikazujemo novi pregled
        };
        reader.readAsDataURL(file);
    }
}

// Funkcija za kreiranje posta
async function api_createpost() {
    const formData = new FormData(document.getElementById('postForm'));
    const imageFile = fileImage.files[0];
    if (imageFile) {
        formData.append("image", imageFile);
    }

    const response = await fetch('/api/createpost', {
        method: 'POST',
        body: formData
    });

    const text = await response.text();
    console.log(response);
    console.log(text);
    pStatus.innerHTML = text;

    if (response.ok) {
        window.location.href = "posts";
    }
}

btnCreatePost.onclick = api_createpost;
