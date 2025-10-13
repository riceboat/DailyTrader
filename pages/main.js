
// set the dimensions and margins of the graph
divId = 0;
function displayMarketGraph(graphNum){
var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 1000 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

// append the svg object to the body of the page

var svg = d3.select("#marketGraphContainer")
  .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
	.attr("id", "tickerGraph")
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");
d3.json("../data/market.json",
 
  function readData(data){
	var rawData  = data.market.map((obj) => Object.values(obj)[0]);
	
	const parsed = rawData[graphNum].map(d => ({
			            ...d,
			            t: d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(d.t), // Convert date string to Date object
			            o: +d.o,
			            h: +d.h,
			            l: +d.l,
			            c: +d.c
			        }));
    // Add X axis --> it is a date format
    var x = d3.scaleTime()
      .domain(d3.extent(parsed, function(d) { return d.t; }))
      .range([ 0, width ]);
    svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

    // Add Y axis
    var y = d3.scaleLinear()
      .domain([d3.min(parsed, function(d) { return +d.c; }), d3.max(parsed, function(d) { return +d.c; })])
      .range([ height, 0 ]);
    svg.append("g")
      .call(d3.axisLeft(y));
	  // Add the line
	  	svg.append("path")
	  	      .datum(parsed)
	  	      .attr("fill", "none")
	  	      .attr("stroke", "steelblue")
	  	      .attr("stroke-width", 1.5)
	  	      .attr("d", d3.line()
	  	        .x(function(d) { return x(d.t) })
	  	        .y(function(d) { return y(d.c) })
	  	        )
	
	  var tickerSelect = document.getElementById("ticker");
	  var option = document.createElement("option");
	  option.value = graphNum;
	  option.text = Object.keys(data.market[graphNum]);
	  tickerSelect.appendChild(option);
	  		for (var i = 0; i < rawData.length; i++) {
				if (i != graphNum){
					var option = document.createElement("option");
	  		    	option.value = i;
	  		    	option.text = Object.keys(data.market[i]);
	  		    	tickerSelect.appendChild(option);
				}
	  		}
})
}
function displayPortfolioGraph(graphNum){
var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 1000 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

// append the svg object to the body of the page

var svg = d3.select("#portfolioGraphContainer")
  .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
	.attr("id", "tickerGraph")
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");
d3.json("../data/portfolio.json",
 
  function readData(data){
	console.log(data);
	
	const parsed = data.history.map(d => ({
			            ...d,
			            t: d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(d.t), // Convert date string to Date object
			            o: +d.o,
			            h: +d.h,
			            l: +d.l,
			            c: +d.c
			        }));
    // Add X axis --> it is a date format
    var x = d3.scaleTime()
      .domain(d3.extent(parsed, function(d) { return d.t; }))
      .range([ 0, width ]);
    svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

    // Add Y axis
    var y = d3.scaleLinear()
      .domain([d3.min(parsed, function(d) { return +d.c; }), d3.max(parsed, function(d) { return +d.c; })])
      .range([ height, 0 ]);
    svg.append("g")
      .call(d3.axisLeft(y));
	  // Add the line
	  	svg.append("path")
	  	      .datum(parsed)
	  	      .attr("fill", "none")
	  	      .attr("stroke", "steelblue")
	  	      .attr("stroke-width", 1.5)
	  	      .attr("d", d3.line()
	  	        .x(function(d) { return x(d.t) })
	  	        .y(function(d) { return y(d.c) })
	  	        )
	
	  var positionSelect = document.getElementById("positionSelect");
	  var option = document.createElement("option");
	  option.value = graphNum;
	  option.text = Object.keys(data.history[graphNum]);
	  positionSelect.appendChild(option);
	  		for (var i = 0; i < data.history.length; i++) {
				if (i != graphNum){
					var option = document.createElement("option");
	  		    	option.value = i;
	  		    	option.text = Object.keys(data.history[i]);
	  		    	positionSelect.appendChild(option);
				}
	  		}
})
}

displayMarketGraph(0);
displayPortfolioGraph(0);
document.querySelector('#ticker').addEventListener("change", function() {
  graphNum=this.value;
  d3.selectAll('svg').remove();
  d3.selectAll('option').remove();
  displayMarketGraph(graphNum);
  });