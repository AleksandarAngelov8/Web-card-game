//Erstellt von Angelov
window.onload = function() {
    var id = document.getElementById("rede_id").innerHTML;
    var speech = document.getElementById("text");

    fetch('/api/sentiment/' + id)
        .then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                document.getElementById('sentimentButton').disabled = true;
                document.getElementById('sentimentButton').classList.add('disabledButton');
                document.getElementById('namedEntitiesButton').disabled = true;
                document.getElementById('namedEntitiesButton').classList.add('disabledButton');
                document.getElementById('nounsButton').disabled = true;
                document.getElementById('nounsButton').classList.add('disabledButton');
            }
        })
    document.getElementById('sentimentButton').addEventListener('click', function() {
        fetch('/api/sentiment/' + id)
            .then(response => response.json())
            .then(data => colorSentences(data, speech));
    });

    document.getElementById('namedEntitiesButton').addEventListener('click', function() {
            fetch('/api/namedentities/' + id)
                .then(response => response.json())
                .then(data => colorNamedEntities(data, speech));
    });
};

function colorSentences(data, textContainer) {
    if (data.length === 0){
        return;
    }
    var text = removeEmojis(textContainer.textContent);
    var coloredText = "";

    data.forEach(function(sentiment) {
        var sentence = text.substring(
            sentiment.begin,
            sentiment.end);
        var spaceAfterSentence = false;
        if (sentiment.end+1<text.length){
            spaceAfterSentence = (text.substring(sentiment.end,sentiment.end+1))==' ';
        }
        var emoji;
        if (sentiment.sentiment >= 0.33) {
            emoji = "😄"; // Happy face
        } else if (sentiment.sentiment >= -0.3) {
            emoji = "😐"; // Neutral face
        } else {
            emoji = "😢"; // Unhappy face
        }
        var tooltip = "Sentiment: " + sentiment.sentiment;
        var sentenceWithIcon = sentence + (spaceAfterSentence ? " " : "") + "<span class=\"sentiment-icon\" title=\"" + tooltip + "\">" + emoji + "</span>";
        coloredText += sentenceWithIcon;
    });

    textContainer.innerHTML = coloredText;
}

function removeEmojis(text) {
    const emojiRegex = /[\uD800-\uDBFF][\uDC00-\uDFFF]|[\u2600-\u27FF]/g;

    const cleanedText = text.replace(emojiRegex, '');

    return cleanedText;
}

function colorNamedEntities(data, textContainer) {
    if (data.length === 0){
        return;
    }
    var existingEmojis = textContainer.querySelectorAll(".sentiment-icon");
    existingEmojis.forEach(function(icon) {
        icon.parentNode.removeChild(icon);
    });

    var text = textContainer.textContent;
    var lastIndex = 0;
    var coloredText = "";

    data.forEach(function(entity) {
        var namedEntity = text.substring(entity.begin, entity.end);
        var color;
        switch (entity.value) {
            case 'PER':
                color = "red";
                break;
            case 'LOC':
                color = "orange";
                break;
            case 'ORG':
                color = "yellow ";
                break;
            case 'MISC':
                color = "blue";
                break;
        }
        var tooltip = "NamedEntity: " + entity.value;
        coloredText += text.substring(lastIndex, entity.begin) +
            "<span style=\"color:" + color + "\" title=\"" + tooltip + "\">" +
            namedEntity +
            "</span>";
        lastIndex = entity.end;
    });

    coloredText += text.substring(lastIndex);
    textContainer.innerHTML = coloredText;
}
function showInfo() {
    document.getElementById('red').style.display = 'block';
    document.getElementById('orange').style.display = 'block';
    document.getElementById('yellow').style.display = 'block';
    document.getElementById('blue').style.display = 'block';
}
function hideInfo() {
    document.getElementById('red').style.display = 'none';
    document.getElementById('orange').style.display = 'none';
    document.getElementById('yellow').style.display = 'none';
    document.getElementById('blue').style.display = 'none';
}