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
        margin = {top: 20, right: 20, bottom: 30, left: 100},
        width = +svg.attr("width") - margin.left - margin.right,
        height = +svg.attr("height") - margin.top - margin.bottom;

    var x = d3.scaleLinear().range([0, width]);
    var y = d3.scaleBand().range([height, 0]).padding(0.1);
    var color = d3.scaleOrdinal(d3.schemeCategory10); // Add color scale

    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    x.domain([0, d3.max(data, function(d) { return d.value; })]);
    y.domain(data.map(function(d) { return d.name; }));

    g.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    g.append("g")
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y));

    g.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("y", function(d) { return y(d.name); })
        .attr("height", y.bandwidth())
        .attr("x", 0)
        .attr("width", function(d) { return x(d.value); })
        .attr("fill", function(d) { return color(d.name); }); // Add color to bars
</script>
