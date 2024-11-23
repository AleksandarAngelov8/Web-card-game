document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(form);

        fetch('/raise_hand', {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    updateUserDiv(formData.get("name"),data.username);
                } else {
                    console.error(data.message);
                    alert("Error: " + data.message);
                }})
            .catch(error => console.error('Error:', error));
    });
});
function updateUserDiv(person_name,username) {
    fetch('/dashboard')
        .then(response => response.text())
        .then(html => {
            var div = document.getElementById(username);
            div.appendChild(document.createTextNode(person_name));
        })
        .catch(error => console.error('Error fetching dashboard:', error));
}