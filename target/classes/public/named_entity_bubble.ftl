<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<head>
    <script src="https://d3js.org/d3.v4.min.js"></script>
</head>
<body>
<svg width="800" height="600"></svg>
</body>

<script>
    var data = [<#list data?keys as key>
        { name: "${key}", value: ${data[key]?c} },
        </#list>
    ];

    var svg = d3.select("svg"),
        width = +svg.attr("width"),
        height = +svg.attr("height");

    var pack = d3.pack()
        .size([width, height])
        .padding(1.5);

    var root = d3.hierarchy({children: data})
        .sum(function(d) { return d.value; });

    var color = d3.scaleOrdinal(d3.schemeCategory10); // Add color scale

    var node = svg.selectAll(".node")
        .data(pack(root).leaves())
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

    node.append("circle")
        .attr("r", function(d) { return d.r; })
        .style("fill", function(d) { return color(d.data.name); }); // Add color to bubbles

    node.append("text")
        .attr("dy", ".3em")
        .style("text-anchor", "middle")
        .text(function(d) { return d.data.name; });
</script>
