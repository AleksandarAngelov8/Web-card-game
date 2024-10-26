<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="/search.js"></script>
</head>
<body>
<h1>Abgeordneter</h1>
<input type="text" id="searchInput" placeholder="Suche nach ID/Namen..">
<ul id="representativesList">
    <#list representatives as representative>
        <li>
            <p>Id: ${representative.ID}</p>
            <p>Vorname: ${representative.vorname}</p>
            <p>Name: ${representative.name}</p>
            <p>Ortszusatz: ${representative.ortszusatz}</p>
            <p>Adelssuffix: ${representative.adelssuffix}</p>
            <p>Anrede: ${representative.anrede}</p>
            <p>GeburtsDatum: ${representative.geburtsDatum}</p>
            <p>GeburtsOrt: ${representative.geburtsOrt!}</p>
            <p>SterbeDatum: ${representative.sterbeDatum!}</p>
            <p>Geschlecht: ${representative.geschlecht}</p>
            <p>Religion: ${representative.religion}</p>
            <p>Beruf: ${representative.beruf}</p>
            <p>Vita: ${representative.vita}</p>
            <p>Partei: ${representative.partei}</p>
            <p>AkadTitel: ${representative.akadTitel!}</p>
        </li>
    </#list>
</ul>
</body>
</html>
