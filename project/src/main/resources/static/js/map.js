var map = L.map('map').setView([45.2671, 19.8335], 13);

var markers = L.layerGroup().addTo(map);

function init_map() {
    // Add OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    map.on('click', function (e) {
        var lat = e.latlng.lat;
        var lon = e.latlng.lng;
        const location = `${lat},${lon}`;

        // display location
        L.marker([lat, lon]).addTo(markers).bindPopup(`${lat.toFixed(5)},${lat.toFixed(5)}`).openPopup();
        navigator.clipboard.writeText(location); // copy location
    });
}

// init map on load
document.addEventListener("DOMContentLoaded", function() { init_map(); });

async function fetch_locations() {
    
    // clear all markers
    markers.clearLayers();

    const response = await fetch(`/api/map/locations`, { method: "GET" });
    const locations = await response.json();

    locations.forEach(function(location) {
        if (location == null) { return; }
        let [lat, lon] = location.split(',').map(Number);
        L.marker([lat, lon]).addTo(markers);
    });
}