function loadScript(url) {
    var script = document.createElement('script'); // Create a new <script> tag
    script.src = url; // Set the source to the external JS file URL
    script.type = 'text/javascript'; // Set the script type
    script.async = true; // Optional: makes the script load asynchronously

    document.head.appendChild(script);
    
    script.onload = function() { console.log(`'loaded: ${url}`); };
    script.onerror = function() { console.log(`error loading script: ${url}`); };
}

// remove children
function prune(div) {
    while (div.firstChild) {
        div.removeChild(div.lastChild);
    }
}