const text = document.getElementById('rays');
const range = 16;

function updateShadow(event) {
    const x = Math.round(event.pageX * range / window.innerWidth) - range / 2;
    const y = Math.round(event.pageY * range / window.innerHeight) - range / 2;
    gsap.to(text, {
        '--x': x,
        '--y': y,
    });
}

document.body.addEventListener('mousemove', updateShadow);
