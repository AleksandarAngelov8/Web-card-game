<!-- Erstellt von Angelov -->
<!DOCTYPE html>
<head>
    <script src="https://d3js.org/d3.v4.min.js"></script>
</head>
<body>
<svg></svg>
<div id="tooltip" style="position: absolute; opacity: 0;"></div>
</body>

<script>
    var nodeData = {
        "name": "Topics",
        "children": [
            <#list data?keys as key>
            { "name": "${key}", "size": ${data[key]?c} }<#sep>, </#sep>
            </#list>
        ]
    };

    var width = 500;
    var height = 500;
    var radius = Math.min(width, height) / 2;
    var color = d3.scaleOrdinal(d3.schemeCategory20b);

    var g = d3.select('svg')
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')');

    var partition = d3.partition()
        .size([2 * Math.PI, radius]);

    var root = d3.hierarchy(nodeData)
        .sum(function (d) { return d.size});

    partition(root);
    var arc = d3.arc()
        .startAngle(function (d) { return d.x0 })
        .endAngle(function (d) { return d.x1 })
        .innerRadius(function (d) { return d.y0 })
        .outerRadius(function (d) { return d.y1 });

    var path = g.selectAll('path')
        .data(root.descendants())
        .enter().append('path')
        .attr("display", function (d) { return d.depth ? null : "none"; })
        .attr("d", arc)
        .style('stroke', '#fff')
        .style("fill", function (d) { return color((d.children ? d : d.parent).data.name); });

    // Add hover functionality
    path.on("mouseover", function(d) {
        d3.select(this).style("fill", "lightgray");
        d3.select("#tooltip")
            .style("left", d3.event.pageX + 10 + "px")
            .style("top", d3.event.pageY + 10 + "px")
            .style("opacity", 1)
            .text("Topic: " + d.data.name);
    })
        .on("mouseout", function(d) {
            d3.select(this).style("fill", function (d) { return color((d.children ? d : d.parent).data.name); });
            d3.select("#tooltip").style("opacity", 0);
        });
</script>
