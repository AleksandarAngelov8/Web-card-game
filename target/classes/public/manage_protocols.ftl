<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Protocol Management</title>
    <script>
        function updateInputType() {
            const selectedOption = document.getElementById("field").value;
            const changeToInput = document.getElementById("change_to");

            if (selectedOption === "Datum") {
                changeToInput.type = "date";
            } else {
                changeToInput.type = "text";
            }
        }
    </script>
</head>
<body>
<h1>Protocol Management</h1>
<button onclick=window.location.href="http://localhost:4567/dashboard">Dashboard</button>
<h2>Create Protocol</h2>
<form action="/manage/create_protocol" method="POST">
    <label for="ID">Id<span style="color: red;">*</span>:</label>
    <input type="text" id="ID" name="ID" required><br>

    <label for="Sitzungsnummer">Sitzungsnummer<span style="color: red;">*</span>:</label>
    <input type="text" id="Sitzungsnummer" name="Sitzungsnummer" required><br>

    <label for="Titel">Titel<span style="color: red;">*</span>:</label>
    <input type="text" id="Titel" name="Titel" required><br>

    <label for="Ort">Ort<span style="color: red;">*</span>:</label>
    <input type="text" id="Ort" name="Ort" required><br>

    <label for="Datum">Datum<span style="color: red;">*</span>:</label>
    <input type="date" id="Datum" name="Datum" required><br>

    <label for="Wahlperiode">Wahlperiode<span style="color: red;">*</span>:</label>
    <input type="text" id="Wahlperiode" name="Wahlperiode" required><br>

    <label for="Start">Startzeit<span style="color: red;">*</span>:</label>
    <input type="text" id="Start" name="Start" required><br>


    <label for="Ende">Endezeit<span style="color: red;">*</span>:</label>
    <input type="text" id="Ende" name="Ende" required><br>

    <label for="Reden">Reden (seperated by a space)<span style="color: red;">*</span>:</label>
    <input type="text" id="Reden" name="Reden" required><br>

    <button type="submit">Create Protocol</button>
</form>
<p style="color: red;">Fields marked with an asterisk (*) are required.</p>

<h2>Edit Protocol</h2>
<form action="/manage/edit_protocol" method="POST">
    <label for="id_edit">Id:</label>
    <input type="text" id="id_edit" name="id_edit" required>

    <label for="field">Choose a column to change:</label>
    <select id="field" name="field" onchange="updateInputType()">
        <option value="_id">Rede ID</option>
        <option value="Sitzungsnummer">Sitzungsnummer</option>
        <option value="Titel">Titel</option>
        <option value="Ort">Ort</option>
        <option value="Wahlperiode">Wahlperiode</option>
        <option value="Start">Startzeit</option>
        <option value="Ende">Endezeit</option>
        <option value="Datum">Datum</option>
        <option value="Reden">Reden</option>
    </select>

    <label for="change_to">Change to:</label>
    <input type="text" id="change_to" name="change_to" required>

    <button type="submit">Edit Protocol</button>
</form>

<h2>Delete Protocol</h2>
<form action="/manage/delete_protocol" method="POST">
    <label for="id_delete">Id:</label>
    <input type="text" id="id_delete" name="id_delete" required>
    <button type="submit" >Delete Protocol</button>
</form>
</body>
</html>
