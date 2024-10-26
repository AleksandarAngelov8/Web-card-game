<!-- Erstellt von Angelov -->
function loadResults(keyword) {
    fetch('/api/search/' + keyword)
        .then(response => response.text())
        .then(result => fillContainer(result))
}

function fillContainer(text) {
    const redeListElement = document.getElementById("rede_list");
    console.log(text.substring(1,text.length-1));
    const speechIds = text.substring(1,text.length-1).split(", ");

    redeListElement.innerHTML = "";

    if (speechIds.length === 0){
        return;
    }

    speechIds.forEach(id => {
        const listItem = document.createElement("li");
        const link = document.createElement("a");
        link.href = `http://localhost:4567/rede/${id}`;
        link.textContent = id;
        link.style.marginRight = "10px";
        listItem.appendChild(link);
        redeListElement.appendChild(listItem);
    });
}
