// erstellt von Angelov
// such funktion die verwendet jquery
// ermöglicht suche nach namen und id
$(document).ready(function() {
    $("#searchInput").on("keyup", function() {
        var searchQuery = $(this).val().toLowerCase();
        $("#representativesList li").filter(function() {
            var listItemText = $(this).text().toLowerCase();
            var listItemID = $(this).find('p:first-child').text().toLowerCase();

            var isQueryFoundInText = listItemText.indexOf(searchQuery) > -1;
            var isQueryFoundInID = listItemID.indexOf(searchQuery) > -1;

            $(this).toggle(isQueryFoundInText || isQueryFoundInID);
        });
    });
});