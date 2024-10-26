<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <style>
        /* Add custom styling for highlighting */
        .highlightable {
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .highlightable:hover {
            background-color: #598c5c; /* Change to your desired highlight color */
        }
    </style>
</head>
<body>
<ul id="speeches">
    <#list speeches as speech>
        <li class="highlightable">
            <p><a href="http://localhost:4567/rede/${speech.ID}">
                    Id: ${speech.ID}
            </p>
            <p>
                <a href="http://localhost:4567/rede/${speech.ID}">${speech.text}</a>
            </p>
        </li>
    </#list>
</ul>
</body>
</html>
