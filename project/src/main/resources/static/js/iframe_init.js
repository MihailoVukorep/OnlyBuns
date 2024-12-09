function iframe_resize(iframe) { iframe.style.height = iframe.contentWindow.document.body.scrollHeight + "px"; }

function iframe_init(iframe_id) {
    const iframe = document.getElementById(iframe_id);
    iframe.onload = function() {
        const iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
        iframeDoc.querySelectorAll('a').forEach(link => {
            link.setAttribute('target', '_top');
        });

        iframe_resize(iframe)
    };
}
