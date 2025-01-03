let stompClient = null;

const msgsContainer = document.getElementById("msgs");
const chat_id_element = document.getElementById("chat_id");
const chatToken_element = document.getElementById("chatToken");
const userToken_element = document.getElementById("userToken");


function sendMessage(event) {
    event.preventDefault();
    const input = document.getElementById("txt_text");
    const content = input.value.trim();

    if (content && stompClient) {
        const message = {
            userToken: userToken_element.value.toString(),
            content: content.toString()
        };
        stompClient.send(`/app/send/${chatToken_element.value}`, {}, JSON.stringify(message));
        input.value = "";
    }
}

function displayMessage(message) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");

    messageElement.innerHTML = `
        <div class="message_date">${message.createdDateStr}</div>
        <div>|</div>
        <div class="message_account">
            <a class="account_link" href="/accounts/${message.account.id}" target="_top">
                <img class="account_image" src="${message.account.avatar}" />
                <span class="account_userName">${message.account.userName}</span>
            </a>
        </div>
        <div>:</div>
        <div class="message_content message_type_${message.type}">
            ${message.content}
        </div>
    `;

    if (message.type != "MESSAGE") { window.location.reload(true); } // wow very secure... -.-

    msgsContainer.appendChild(messageElement);
    msgsContainer.scrollTop = msgsContainer.scrollHeight;
}

function connect_onConnect(messageOutput) {
    const message = JSON.parse(messageOutput.body);
    displayMessage(message);
}

function connect_onError() {
    console.log("Can't connnect.")
}


function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function() {
        console.log("Connected to WebSocket");

        // Subscribe to receive messages for the current chat
        stompClient.subscribe(`/topic/messages/${chatToken_element.value}`, connect_onConnect, connect_onError);
    });

    const msgsContainer = document.getElementById("msgs");
    msgsContainer.scrollTop = msgsContainer.scrollHeight;
}


if (chat_id_element != null) {
    // Connect to WebSocket when the page loads
    window.addEventListener('load', () => { connect(); });
}

// adding people
const div_popup = document.getElementById("addAccount_popup");

function addAccount_popup() {
    div_popup.style.display = div_popup.style.display == "block" ? "none" : "block";
}