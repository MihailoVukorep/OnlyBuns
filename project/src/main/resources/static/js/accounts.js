let div_accounts = document.getElementById("accounts");

function accounts_clear(div_accounts) {
    
    while (div_accounts.firstChild) {
        div_accounts.removeChild(div_accounts.lastChild);
    }
}

function accounts_populate_makeitem_row(clm1, clm2) {
    let tr = document.createElement("tr");
    let th = document.createElement("th"); th.innerHTML = clm1; tr.append(th);
    let td = document.createElement("td"); td.innerHTML = clm2; tr.append(td);
    return tr
}

function accounts_populate_makeitem(json) {

    let a = document.createElement("a");
    a.className = "user_link";
    a.href = "/accounts/" + json.id;

    let span = document.createElement("span");
    span.className = "account";
    
    let img = document.createElement("img");
    img.src = json.avatar;
    span.append(img);

    let table = document.createElement("table");
    table.append(accounts_populate_makeitem_row("EMAIL: ",         json.email));
    table.append(accounts_populate_makeitem_row("USERNAME: ",      json.userName));
    table.append(accounts_populate_makeitem_row("FIRST NAME: ",    json.firstName));
    table.append(accounts_populate_makeitem_row("LAST NAME: ",     json.lastName));
    table.append(accounts_populate_makeitem_row("ADDRESS: ",       json.address));
    table.append(accounts_populate_makeitem_row("BIO: ",           json.bio));
    table.append(accounts_populate_makeitem_row("ACCOUNT ROLE: ",  json.accountRole));
    span.append(table);

    a.append(span);
    return a;
}

function accounts_populate(div_accounts, items) {

    for (let i = 0; i < items.length; i++) {
        div_accounts.append(accounts_populate_makeitem(items[i]));
    }
}


async function load() {
    accounts_clear(div_accounts);

    const response = await fetch("/api/accounts");
    const json = await response.json();

    console.log(json)
    accounts_populate(div_accounts, json);
}

load();

