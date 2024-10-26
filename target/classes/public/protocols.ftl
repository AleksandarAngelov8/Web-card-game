<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
</head>
<body>
<h1>Protocols</h1>
<ul>
    <#list protocols as protocolDate, protocolId>
        <a href="http://localhost:4567/export_protocol/${protocolId}">
            <li>${protocolDate} - ${protocolId}</li>
        </a>
    </#list>
</ul>
</body>
</html>
