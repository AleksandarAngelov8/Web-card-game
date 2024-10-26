<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<head>
    <script src="https://d3js.org/d3.v4.min.js"></script>
</head>
<body>
<svg width="500" height="500"></svg>
</body>

<script>
    var data = [<#list data?keys as key>
        { name: "${key}", value: ${data[key]} },
        </#list>
    ];

    var svg = d3.select("svg")
            .style("background", "gray"), // Add background color
        width = +svg.attr("width"),
        height = +svg.attr("height"),
        radius = Math.min(width, height) / 2,
        g = svg.append("g").attr("transform", "translate(" + width / 2 + "," + (height+70) / 2  + ")");

    var color = d3.scaleOrdinal(d3.schemeCategory10);

    var radarLine = d3.radialLine()
        .curve(d3.curveLinearClosed)
        .radius(function(d) { return radius * d.value; })
        .angle(function(d,i) { return i * 2 * Math.PI / data.length; });

    var gr = g.append("g")
        .attr("class", "r axis")
        .selectAll("g")
        .data(data)
        .enter().append("g");

    gr.append("line")
        .attr("y2", -radius)
        .attr("transform", function(d, i) { return "rotate(" + (360 * i / data.length) + ")"; })
        .style("stroke", "white") // Add radial lines
        .style("stroke-width", 0.5);

    gr.append("text")
        .attr("y", -radius - 20)
        .attr("transform", function(d, i) { return "rotate(" + (360 * i / data.length) + ")"; })
        .attr("text-anchor", "middle")
        .text(function(d,i) { return data[i].name; });

    g.append("path")
        .datum(data)
        .attr("class", "line")
        .attr("d", radarLine)
        .style("fill", function(d,i) { return color(i); })
        .style("opacity", 0.5);
</script>
