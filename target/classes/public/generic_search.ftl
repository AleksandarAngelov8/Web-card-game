<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results</title>
    <style>
        .search-panel {
            border: 1px solid #ccc;
            padding: 10px;
            margin: 20px;
        }
        .search-results li {
            margin-bottom: 5px;
        }
        #external-iframe {
            width: 100%;
            height: 100%;
            border: none;
        }
        #results-panel{
            height: auto;
        }
    </style>
</head>
<body>
<div class="search-panel">
    <input type="text" id="searchbar" placeholder="Search...">
    <button id="searchButton">Search</button>
</div>

<div id="results-panel" class="search-panel">
    <h2 id="search_query"></h2>
    <iframe id="external-iframe" src="" name="myIframe"></iframe>
</div>

<script>
    var data = [<#list data?keys as key>
        { name: "${key}", link: "${data[key]}" },
        </#list>
    ];

    const searchInput = document.getElementById('searchbar');
    const resultsList = document.getElementById('results-panel');
    const searchButton = document.getElementById('searchButton');
    const externalIframe = document.getElementById('external-iframe');

    searchButton.addEventListener('click', handleSearch);
    searchInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            handleSearch();
        }
    });

    function handleSearch() {
        document.getElementById("search_query").innerHTML = searchInput.value;
        const query = searchInput.value.toLowerCase();

        const url = data.find(term => term.name.toLowerCase().includes(query))?.link;
        if (url) {
            externalIframe.src = url;

            externalIframe.onload = function () {
                resultsList.style.height = externalIframe.contentWindow.document.body.scrollHeight + 80 + 'px';
            };
        } else {
            externalIframe.src = '';
            resultsList.style.height = 'auto';
        }
    }

</script>
</body>
</html>
