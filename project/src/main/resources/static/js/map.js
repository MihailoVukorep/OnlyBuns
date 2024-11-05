document.addEventListener("DOMContentLoaded", function () {
    // Inicijalizacija mape
    var map = L.map('map').setView([45.2671, 19.8335], 13); // Postavi koordinatne tačke i zoom nivo

    // Postavljanje slojeva mape koristeći OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
    }).addTo(map);

    // Dodavanje markera na mapu
    var marker = L.marker([45.2671, 19.8335]).addTo(map);
    marker.bindPopup("<b>Hello world!</b><br>I am a popup.").openPopup(); // Popup prozor
});
