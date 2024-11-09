function loadjs(url) {
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = url;
        script.type = 'text/javascript';
        script.async = true;
        script.onload = () => { console.log(`loadjs: ${url}`); resolve(); };
        script.onerror = () => { console.error(`error loadjs: ${url}`); reject(new Error(`Failed to load script: ${url}`)); };
        document.head.appendChild(script);
    });
}

function loadcss(url) {
    return new Promise((resolve, reject) => {
        const link = document.createElement('link');
        link.href = url;
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.onload = () => { console.log(`loadcss: ${url}`); resolve(); };
        link.onerror = () => { console.error(`error loadcss: ${url}`); reject(new Error(`Failed to load CSS: ${url}`)); };
        document.head.appendChild(link);
    });
}

// remove children
function prune(div) {
    while (div.firstChild) {
        div.removeChild(div.lastChild);
    }
}
