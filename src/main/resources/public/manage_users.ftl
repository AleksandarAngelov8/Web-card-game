<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management</title>
</head>
<body>
<h1>User Management</h1>
<button onclick=window.location.href="http://localhost:4567/dashboard">Dashboard</button>
<h2>Create User</h2>
<form action="/admin/create_user" method="POST">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required>
    <label for="create_password">Password:</label>
    <input type="password" id="create_password" name="create_password" required>

    <input type="radio" id="normal_user" name="rights" value="Normal user" checked="checked">
    <label for="normal_user">Normal user</label>
    <input type="radio" id="manager" name="rights" value="Manager">
    <label for="manager">Manager</label>
    <input type="radio" id="admin" name="rights" value="Admin">
    <label for="admin">Admin</label>

    <button type="submit">Create User</button>
</form>

<h2>Edit User</h2>
<form action="/admin/edit_user" method="POST">
    <label for="edit_user">Username:</label>
    <input type="text" id="edit_user" name="edit_user" required>

    <label for="new_username">Username:</label>
    <input type="text" id="new_username" name="new_username" required>
    <label for="edit_password">Password:</label>
    <input type="password" id="edit_password" name="edit_password" required>
    <input type="radio" id="normal_user" name="rights" value="Normal user" checked="checked">
    <label for="normal_user">Normal user</label>
    <input type="radio" id="manager" name="rights" value="Manager">
    <label for="manager">Manager</label>
    <input type="radio" id="admin" name="rights" value="Admin">
    <label for="admin">Admin</label>

    <button type="submit">Edit User</button>

</form>

<h2>Delete User</h2>
<form action="/admin/delete_user" method="POST">
    <label for="delete_user">Username:</label>
    <input type="text" id="delete_user" name="delete_user" required>
    <button type="submit" >Delete User</button>
</form>

<h2>Current Users</h2>
<table>
    <tr>
        <th>Username</th>
        <th>Password</th>
        <th>Rights</th>
    </tr>
    <#list users as user>
    <tr>
        <td>${user.username}</td>
        <td>${user.password}</td>
        <td>${user.rights}</td>
    </tr>
    </#list>
</table>
</body>
</html>
