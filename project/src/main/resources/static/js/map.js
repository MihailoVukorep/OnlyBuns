var map = L.map('map').setView([45.25120485988152, 19.82688903808594], 13);

var markers = L.layerGroup().addTo(map);

const emojiIcon_post = L.divIcon({
    className: 'emoji-icon',
    html: '📰',
});

const emojiIcon_vet = L.divIcon({
    className: 'emoji-icon',
    html: '🧑‍⚕️',
});

const emojiIcon_shelter = L.divIcon({
    className: 'emoji-icon',
    html: '🏥',
});

async function fetch_locations() {

    // clear all markers
    markers.clearLayers();

    const response = await fetch(`/api/map/locations`, { method: "GET" });
    const json = await response.json();

    map.setView(json.userCoordinates.split(',').map(Number), 13);

    json.locations.forEach(function(location) {
        if (location == null) { return; }
        if (location.coordinates == null) { return; }

        let [lat, lon] = location.coordinates.split(',').map(Number);

        if (location.type == "POST") { }

        switch (location.type) {
            case "POST": L.marker([lat, lon], { icon: emojiIcon_post }).addTo(markers).bindPopup(`<a href="${location.url}" target="_blank">Post</a>`); break;
            case "VETERINARIAN": L.marker([lat, lon], { icon: emojiIcon_vet }).addTo(markers).bindPopup(`<a href="${location.url}" target="_blank">Veterinarian</a>`); break;
            case "SHELTER": L.marker([lat, lon], { icon: emojiIcon_shelter }).addTo(markers).bindPopup(`<a href="${location.url}" target="_blank">Shelter</a>`); break;
        }
    });
}

function init_map() {
    // Add OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    map.on('click', function(e) {
        var lat = e.latlng.lat;
        var lon = e.latlng.lng;
        const location = `${lat},${lon}`;

        // display location
        L.marker([lat, lon]).addTo(markers).bindPopup(`${lat.toFixed(5)},${lat.toFixed(5)}`).openPopup();
        navigator.clipboard.writeText(location); // copy location

        // set selected location to
        const locationTxt = document.getElementById('txt_location');
        if (locationTxt != null) { locationTxt.value = location; }
    });

    fetch_locations();
}

// init map on load
document.addEventListener("DOMContentLoaded", function() { init_map(); });

