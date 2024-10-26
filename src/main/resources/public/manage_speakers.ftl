<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Speaker Management</title>
    <script>
        function updateInputType() {
            const selectedOption = document.getElementById("field").value;
            const changeToInput = document.getElementById("change_to");

            if (selectedOption === "geburtsDatum") {
                changeToInput.type = "date";
            } else {
                changeToInput.type = "text";
            }
        }
    </script>
</head>
<body>
<h1>Speaker Management</h1>
<button onclick=window.location.href="http://localhost:4567/dashboard">Dashboard</button>
<h2>Create Speaker</h2>
<form action="/manage/create_speaker" method="POST">
    <label for="ID">Id<span style="color: red;">*</span>:</label>
    <input type="text" id="ID" name="ID" required><br>

    <label for="Name">Name<span style="color: red;">*</span>:</label>
    <input type="text" id="Name" name="Name" required><br>

    <label for="Vorname">Vorname<span style="color: red;">*</span>:</label>
    <input type="text" id="Vorname" name="Vorname" required><br>

    <label for="Geschlecht">Geschlecht<span style="color: red;">*</span>:</label>
    <input type="text" id="Geschlecht" name="Geschlecht" required><br>

    <label for="Anrede">Anrede:</label>
    <input type="text" id="Anrede" name="Anrede"><br>

    <label for="Akadtitel">Akadtitel:</label>
    <input type="text" id="Akadtitel" name="Akadtitel"><br>

    <label for="Beruf">Beruf<span style="color: red;">*</span>:</label>
    <input type="text" id="Beruf" name="Beruf" required><br>

    <label for="Partei">Partei<span style="color: red;">*</span>:</label>
    <input type="text" id="Partei" name="Partei" required><br>

    <label for="Vita">Vita:</label>
    <input type="text" id="Vita" name="Vita"><br>

    <label for="Geburtsdatum">Geburtsdatum<span style="color: red;">*</span>:</label>
    <input type="date" id="Geburtsdatum" name="Geburtsdatum" required><br>

    <label for="Geburtsort">Geburtsort<span style="color: red;">*</span>:</label>
    <input type="text" id="Geburtsort" name="Geburtsort" required><br>

    <label for="Ortszusatz">Ortszusatz:</label>
    <input type="text" id="Ortszusatz" name="Ortszusatz"><br>

    <label for="Religion">Religion<span style="color: red;">*</span>:</label>
    <input type="text" id="Religion" name="Religion" required><br>

    <label for="Adelssuffix">Adelssuffix:</label>
    <input type="text" id="Adelssuffix" name="Adelssuffix"><br>


    <button type="submit">Create Speaker</button>
</form>
<p style="color: red;">Fields marked with an asterisk (*) are required.</p>

<h2>Edit Speaker</h2>
<form action="/manage/edit_speaker" method="POST">
    <label for="id_edit">Id:</label>
    <input type="text" id="id_edit" name="id_edit" required>

    <label for="field">Choose a column to change:</label>
    <select id="field" name="field" onchange="updateInputType()">
        <option value="_id">ID</option>
        <option value="nachname">Name</option>
        <option value="vorname">Vorname</option>
        <option value="geschlecht">Geschlecht</option>
        <option value="akadTitel">Titel</option>
        <option value="anrede">Anrede</option>
        <option value="beruf">Beruf</option>
        <option value="partei">Partei</option>
        <option value="vita">Vita</option>
        <option value="geburtsDatum">Geburtsdatum</option>
        <option value="geburtsort">Geburtsort</option>
        <option value="ortszusatz">Ortszusatz</option>
        <option value="religion">Religion</option>
        <option value="adelssuffix">Adelssuffix</option>
    </select>

    <label for="change_to">Change to:</label>
    <input type="text" id="change_to" name="change_to" required>

    <button type="submit">Edit Speaker</button>
</form>

<h2>Delete Speaker</h2>
<form action="/manage/delete_speaker" method="POST">
    <label for="id_delete">Id:</label>
    <input type="text" id="id_delete" name="id_delete" required>
    <button type="submit" >Delete Speaker</button>
</form>
</body>
</html>
