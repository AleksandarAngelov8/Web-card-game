<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script src="/text_analysis.js"></script>
</head>
<body>
<p id="redner" style="display:none">${speaker_id}</p>
<p id="rede_id" style="display:none">${speech_id}</p>
<h1 id="redner_info">${speaker_name} von Partei ${party} und Fraktion ${fraction}</h1>
<div class="button-container">
    <button id="sentimentButton" onclick="hideInfo()">Sentiment</button>
    <button id="nounsButton" onclick="window.location.href='http://localhost:4567/generic_search/search';">Diagrammen</button>
    <button id="namedEntitiesButton" onclick="showInfo()">NamedEntities</button>
    <p id="red">Person</p>
    <p id="orange">Location</p>
    <p id="yellow">Organization</p>
    <p id="blue">Miscellaneous</p>
</div>
<p id="text">${text}</p>
<h2>Kommentare</h2>
<p id="text">${comments}</p>
</body>
</html>
