<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Speech Management</title>
    <script>
        function updateInputType() {
            const selectedOption = document.getElementById("field").value;
            const changeToInput = document.getElementById("change_to");

            if (selectedOption === "datum") {
                changeToInput.type = "date";
            } else {
                changeToInput.type = "text";
            }
        }
    </script>
</head>
<body>
<h1>Speech Management</h1>
<button onclick=window.location.href="http://localhost:4567/dashboard">Dashboard</button>
<h2>Create Speech</h2>
<form action="/manage/create_speech" method="POST">
    <label for="ID">Id<span style="color: red;">*</span>:</label>
    <input type="text" id="ID" name="ID" required><br>

    <label for="Redner">Redner ID<span style="color: red;">*</span>:</label>
    <input type="text" id="Redner" name="Redner" required><br>

    <label for="Text">Text<span style="color: red;">*</span>:</label>
    <input type="text" id="Text" name="Text" required><br>

    <label for="Kommentare">Kommentare:</label>
    <input type="text" id="Kommentare" name="Kommentare"><br>

    <label for="Datum">Datum<span style="color: red;">*</span>:</label>
    <input type="date" id="Datum" name="Datum" required><br>

    <label for="Fraktion">Fraktion:</label>
    <input type="text" id="Fraktion" name="Fraktion"><br>

    <label for="Sitzung">Sitzung<span style="color: red;">*</span>:</label>
    <input type="text" id="Sitzung" name="Sitzung" required><br>

    <label for="Wahlperiode">Wahlperiode<span style="color: red;">*</span>:</label>
    <input type="text" id="Wahlperiode" name="Wahlperiode" required><br>

    <button type="submit">Create Speech</button>
</form>
<p style="color: red;">Fields marked with an asterisk (*) are required.</p>

<h2>Edit Speech</h2>
<form action="/manage/edit_speech" method="POST">
    <label for="id_edit">Id:</label>
    <input type="text" id="id_edit" name="id_edit" required>

    <label for="field">Choose a column to change:</label>
    <select id="field" name="field" onchange="updateInputType()">
        <option value="_id">Rede ID</option>
        <option value="ID">Redner ID</option>
        <option value="rede">rede</option>
        <option value="kommentare">kommentare</option>
        <option value="fraktion">fraktion</option>
        <option value="Sitzung">Sitzung</option>
        <option value="wahlperiode">wahlperiode</option>
        <option value="datum">datum</option>
    </select>

    <label for="change_to">Change to:</label>
    <input type="text" id="change_to" name="change_to" required>

    <button type="submit">Edit Speech</button>
</form>

<h2>Delete Speech</h2>
<form action="/manage/delete_speech" method="POST">
    <label for="id_delete">Id:</label>
    <input type="text" id="id_delete" name="id_delete" required>
    <button type="submit" >Delete Speech</button>
</form>
</body>
</html>
