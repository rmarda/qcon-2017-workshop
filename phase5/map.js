// FIXME: Replace with real URL
var apiUrl = '';

// Center on United States
var mymap = L.map('map').setView([39.833333, -98.583333], 5);

// Use free CartoDB tileset (served from a CDN and no API key required, yay!)
L.tileLayer('https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png', {
maxZoom: 18, attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>, &copy; <a href="https://carto.com/attribution">CARTO</a>'
}).addTo(mymap);

// Call out to API Gateway, ask for current locations
var xhr = new XMLHttpRequest();
xhr.open('GET', apiUrl);
xhr.send(null);

// Add locations to map
xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) { // readyState 4 means the request is finished
        if (xhr.status === 200) {
            var featureCollection = JSON.parse(xhr.responseText);
            L.geoJSON(featureCollection, {
                pointToLayer: function (feature, latlng) {
                        var t = feature.properties.temperature;
                        var c = "#ffffff";
                        if (t > 90) {
                            c = "#ff0000";
                        } else if (t > 65) {
                            c = "#00ff00";
                        } else {
                            c = "#0000ff";
                        }
                        var options = {color: c};
                        return L.circleMarker(latlng, options);
                    },
                onEachFeature: function (feature, layer) {
                        if (feature.properties && feature.properties.temperature) {
                            layer.bindTooltip(feature.properties.temperature.toFixed().toString(),
                            {permanent: true, opacity: 0.7}).openTooltip();
                        }
                }}).addTo(mymap);
        } else {
            console.log('Error: ' + xhr.status);
        }
    }
};
