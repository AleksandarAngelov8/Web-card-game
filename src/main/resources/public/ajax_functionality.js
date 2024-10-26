var person_name = "nigelf";
document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission

        const formData = new FormData(form);


        // Send a POST request to /raise_hand
        fetch('/raise_hand', {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    person_name = formData.get("name");
                    updateDashboard();
                } else {
                    // Display an error message
                    console.error(data.message);
                    alert("Error: " + data.message); // Or show it in the page
                }})
            .catch(error => console.error('Error:', error));
    });


});
// Function to update the dashboard
function updateDashboard() {
    fetch('/dashboard')
        .then(response => response.text())
        .then(html => {
            document.getElementById('dashboard').innerHTML += person_name;
        })
        .catch(error => console.error('Error fetching dashboard:', error));
}