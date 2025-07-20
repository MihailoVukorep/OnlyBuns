let stompClient = null;

const msgsContainer = document.getElementById("msgs");
const chat_id_element = document.getElementById("chat_id");
const chatToken_element = document.getElementById("chatToken");
const userToken_element = document.getElementById("userToken");

let allOtherAccounts = [];
const selectedUserIds = new Set();
let sortedIds = [];

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

    if (message.type != "MESSAGE") { window.location.reload(true); } // force refresh chat token has changed

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

function hideChatCreationPopup(){
    // hide popup form for chat creation
    const popup = document.getElementById("createChatPopup");
    const overlay = document.getElementById("overlay");
    popup.classList.remove("show");
    overlay.classList.remove("show");
}

async function createNewChat() {
    const accountsResponse = await fetch("/api/accounts");
    allOtherAccounts = await accountsResponse.json();

    renderUsersChecklist(allOtherAccounts);

    const popup = document.getElementById("createChatPopup");
    const overlay = document.getElementById("overlay");
    popup.classList.add("show");
    overlay.classList.add("show");

    // Attach search listener
    const searchInput = document.getElementById("searchUsers");
    searchInput.value = "";
    searchInput.oninput = () => {
        const term = searchInput.value.trim().toLowerCase();
        const filtered = allOtherAccounts.filter(f =>
            f.userName.toLowerCase().includes(term)
        );
        renderUsersChecklist(filtered);
    };
}

function renderUsersChecklist(otherAccounts){
    const container = document.getElementById("userList");
    container.innerHTML = "";

    otherAccounts.forEach(account => {
        const label = document.createElement("label");
        label.style.display = "block";

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.name = "userIds";
        checkbox.value = account.id;

        // Restore checked state
        if (selectedUserIds.has(account.id)) {
            checkbox.checked = true;
        }

        // Track state changes
        checkbox.addEventListener("change", () => {
            if (checkbox.checked) {
                selectedUserIds.add(account.id);
                sortedIds = [...selectedUserIds].sort();
            } else {
                selectedUserIds.delete(account.id);
                sortedIds = [...selectedUserIds].sort();
            }
        });

        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(" " + account.userName));
        container.appendChild(label);
    });
}

async function submitNewChat(){
    // proveravamo da li je barem jedan korisnik selektovan za chat
    if (selectedUserIds.size === 0) {
        const validationError = document.getElementById('validationError');
        validationError.classList.add('show-error');
        return;
    }

    const form = document.getElementById("createChatForm");
    const chatName = form.chatName.value;

    const formData = new FormData();
    formData.append("chatName", chatName);
    sortedIds.forEach(id => formData.append("userIds", id));

    const response = await fetch('/api/create-chat', {
        method: 'POST',
        body: formData
    });

    if (response.ok) {
        // redirect to newly created chat
        const chatId = await response.json();
        window.location.href = `/chats/${chatId}`;
    } else {
        alert("Failed to create chat.");
    }

    // hide popup form for chat creation
    hideChatCreationPopup();
}