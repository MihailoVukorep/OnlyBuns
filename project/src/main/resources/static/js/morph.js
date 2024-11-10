function morph(element, fromText, toText) {
    const morphTime = 0.5;       // Duration of morphing (in seconds)
    const cooldownTime = 0.25; // Cooldown time after morphing (in seconds)

    let morph = 0;             // Morph progress variable
    let startTime = null;      // Start time for animation

    // Create two child spans to hold the texts for the morph effect
    element.innerHTML = '';    // Clear existing content
    const text1 = document.createElement("span");
    const text2 = document.createElement("span");

    // Style the container and spans
    element.style.position = "relative";          // Position context for absolute spans
    element.style.display = "flex";       // Container fits content size

    // Style text spans for overlay and centering
    [text1, text2].forEach((text) => {
        text.style.position = "absolute";
        text.style.top = "50%";
        text.style.left = "50%";
        text.style.transform = "translate(-50%, -50%)"; // Center text horizontally and vertically
        text.style.whiteSpace = "nowrap";               // Prevent line wrapping
    });
    
    // Set initial text and opacity
    text1.textContent = fromText;
    text2.textContent = toText;
    text1.style.opacity = "1";
    text2.style.opacity = "0";

    element.appendChild(text1);
    element.appendChild(text2);

    function doMorph(timestamp) {
        if (!startTime) startTime = timestamp;
        morph = (timestamp - startTime) / 1000 / morphTime; // Calculate morph fraction

        if (morph > 1) morph = 1; // Clamp fraction at 1 when complete

        setMorph(morph);

        if (morph < 1) {
            requestAnimationFrame(doMorph); // Continue morphing
        } else {
            setTimeout(doCooldown, cooldownTime * 1000); // Cooldown phase
        }
    }

    function setMorph(fraction) {
        text2.style.filter = `blur(${Math.min(8 / fraction - 8, 100)}px)`;
        text2.style.opacity = `${Math.pow(fraction, 0.4) * 100}%`;

        fraction = 1 - fraction;
        text1.style.filter = `blur(${Math.min(8 / fraction - 8, 100)}px)`;
        text1.style.opacity = `${Math.pow(fraction, 0.4) * 100}%`;
    }

    function doCooldown() {
        text1.style.opacity = "0";
        text1.style.filter = "";
        text2.style.opacity = "1";
        text2.style.filter = "";
    }

    // Start the morphing animation
    requestAnimationFrame(doMorph);
}