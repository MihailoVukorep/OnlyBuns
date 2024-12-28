let stompClient = null;

const msgsContainer = document.getElementById("msgs");
const chat_id_element = document.getElementById("chat_id");
const user_userName = document.getElementById("user_userName");


function sendMessage(event, chatId) {
    event.preventDefault();
    const input = document.getElementById("txt_text");
    const content = input.value.trim();

    if (content && stompClient) {
        const message = {
            userName: user_userName.value,
            content: content
        };
        stompClient.send(`/app/send/${chatId}`, {}, JSON.stringify(message));
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
                <div class="message_content">${message.content}</div>
            `;

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


function connect(chatId) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function() {
        console.log("Connected to WebSocket");

        // Subscribe to receive messages for the current chat
        stompClient.subscribe(`/topic/messages/${chatId}`, connect_onConnect, connect_onError);
    });

    const msgsContainer = document.getElementById("msgs");
    msgsContainer.scrollTop = msgsContainer.scrollHeight;
}


if (chat_id_element != null) {
    // Connect to WebSocket when the page loads
    window.addEventListener('load', () => { connect(chat_id_element.value); } );
}


