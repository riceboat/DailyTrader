
// set the dimensions and margins of the graph
divId = 0;
function displayMarketGraph(symbolString){
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
d3.request("api").post("bars="+symbolString,
  function readData(data){
	data = JSON.parse(data.response);
	const parsed = data[symbolString].map(d => ({
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
			  .attr("id", "marketLine")
	  	      .attr("d", d3.line()
	  	        .x(function(d) { return x(d.t) })
	  	        .y(function(d) { return y(d.c) })
	  	        )
	
	  updateTickerOptions(symbolString);
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
	.attr("id", "portfolioGraph")
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");
d3.request("api").post("portfolio=1",
  function readData(data){
	data = JSON.parse(data.response);
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
			  .attr("id", "portfolioLine")
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

function displayStrategyGraph(strategyName){
var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 1000 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

// append the svg object to the body of the page

var svg = d3.select("#strategyGraphContainer")
  .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
	.attr("id", "strategyGraph")
  .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");

d3.request("api").post("strategy="+strategyName,
  function readData(error, data){
	if (error) throw error;
	var selectedStrategy = "";
	data = JSON.parse(data.response);
	selectedStrategy = Object.keys(data)[0];
	d3.request("api").post("strategyNames=1",
			function readData(data){
				data = JSON.parse(data.response);
				var names = data.strategyNames;
				var strategySelect = document.getElementById("strategySelect");
				names.forEach(function readName(name, i, a){
					var option = document.createElement("option");
						  		    	option.value = name;
						  		    	option.text = name;
										option.setAttribute("id", "strategyOption");
						  		    	strategySelect.appendChild(option);
										if (name == selectedStrategy){
											option.setAttribute("selected", "");
										}
				});
			}
		);
	const parsed = data[Object.keys(data)[0]].map(d => ({
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
			  .attr("id", "portfolioLine")
	  	      .attr("stroke", "steelblue")
	  	      .attr("stroke-width", 1.5)
	  	      .attr("d", d3.line()
	  	        .x(function(d) { return x(d.t) })
	  	        .y(function(d) { return y(d.c) })
	  	        )
	})	
}

function updateTickerOptions(ticker){
	d3.request("api").post("savedTickers=30",
					function readData(data){
					data = JSON.parse(data.response);
					var names = data.tickers;
					var tickerSelect = document.getElementById("tickerSelect");
					tickerSelect.innerHTML = "";
					names.forEach(function readName(name, i, a){
						var option = document.createElement("option");
						option.value = name;
						option.text = name;
						option.setAttribute("id", "tickerOption");
						if (name == ticker){
							option.setAttribute("selected", "");
						}
						tickerSelect.appendChild(option);
					});
				});	
	
}
function addTicker(ticker){
	d3.request("api").post("addTicker="+ticker, function add(){
		updateTickerOptions(ticker);
		d3.selectAll('#tickerGraph').remove();
		d3.selectAll('#tickerOption').remove();
		displayMarketGraph(ticker);
		});
	
}
function removeTicker(ticker){
	d3.request("api").post("removeTicker="+ticker, function add(){
		var newTicker = "";
		var currentOptions = document.querySelectorAll('[id=tickerOption]');
		currentOptions.forEach(function getOption(option, i, a){
			if (option.value != ticker){
				newTicker = option.value;
			}
		});
		d3.selectAll('#tickerGraph').remove();
		d3.selectAll('#tickerOption').remove();
		updateTickerOptions(ticker);
		console.log(newTicker);
		displayMarketGraph(newTicker);
		});
}
addTicker("SPY");
addTicker("NVDA");
addTicker("AAPL");
displayPortfolioGraph(0);
displayStrategyGraph("RandomActions");
document.querySelector('#tickerSelect').addEventListener("change", function() {
  ticker=this.value;
  d3.selectAll('#tickerGraph').remove();
  d3.selectAll('#tickerOption').remove();
  updateTickerOptions(ticker);
  displayMarketGraph(ticker);
  });
document.querySelector('#strategySelect').addEventListener("change", function() {
	strategyName = this.value;
	d3.selectAll('#strategyGraph').remove();
	d3.selectAll('#strategyOption').remove();
    displayStrategyGraph(strategyName);
});
document.querySelector('#tickerAddButton').addEventListener("click", function() {
	var ticker = document.querySelector("#tickerInput").value;
	addTicker(ticker);
});
document.querySelector('#tickerRemoveButton').addEventListener("click", function() {
	var ticker = document.querySelector("#tickerInput").value;
	removeTicker(ticker);
});