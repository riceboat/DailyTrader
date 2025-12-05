// set the dimensions and margins of the graph
divId = 0;

function stringToColor(string) {

    let hash = 0;
    for (const char of string) {
        hash = (hash << 5) - hash + (char.charCodeAt(0) - 65);
        hash |= 0;
    }
    hash = hash % 255;
    let c = d3.hsl("white");
    c.s = 1;
    c.l = 0.5;
    c.h = hash;
    return c;
}

function displayMarketGraph(symbolString) {
    var margin = {
            top: 10,
            right: 30,
            bottom: 30,
            left: 60
        },
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
    d3.request("api").post("bars=" + symbolString,
        function readData(data) {
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
                .domain(d3.extent(parsed, function(d) {
                    return d.t;
                }))
                .range([0, width]);
            svg.append("g")
                .attr("transform", "translate(0," + height + ")")
                .call(d3.axisBottom(x));

            // Add Y axis
            var y = d3.scaleLinear()
                .domain([d3.min(parsed, function(d) {
                    return +d.c * 0.9;
                }), d3.max(parsed, function(d) {
                    return +d.c * 1.1;
                })])
                .range([height, 0]);
            svg.append("g")
                .call(d3.axisLeft(y));
				var focus = svg
							    .append('g')
							    .append('circle')
							      .style("fill", "none")
							      .attr("stroke", "white")
							      .attr('r', 4)
							      .style("opacity", 0)
								  .style("color", "white");

							  // Create the text that travels along the curve of chart
							  var focusText = svg
							    .append('g')
							    .append('text')
							      .style("opacity", 0)
							      .attr("text-anchor", "left")
							      .attr("alignment-baseline", "middle")
								  .style("fill", "white");
							function mouseover() {
							    focus.style("opacity", 1);
							    focusText.style("opacity",1);
							  }

							  function mousemove() {
								
							    mX = d3.mouse(this)[0];
								mY = d3.mouse(this)[1];
								for (var i in data){
									bsX = d3.bisector((d) => x(d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(d.t))).left(data[i], mX);
									xVal = d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(data[i][bsX].t);
									yVal = data[i][bsX].c;
									focus
									      .attr("cx", x(xVal))
									      .attr("cy", y(yVal));
									    focusText
									      .html("$" + yVal.toFixed(2) + ": " + xVal.toLocaleDateString('en-US'))
									      .attr("x", x(xVal))
									      .attr("y", mY);
									    }
								}
							  function mouseout() {
							    focus.style("opacity", 0);
							    focusText.style("opacity", 0);
							  }

							svg.append('rect')
							    .style("fill", "none")
							    .style("pointer-events", "all")
							    .attr('width', width)
							    .attr('height', height)
							    .on('mouseover', mouseover)
							    .on('mousemove', mousemove)
							    .on('mouseout', mouseout);
            // Add the line
            let c = stringToColor(symbolString);
            svg.append("path")
                .datum(parsed)
                .attr("fill", "none")
                .attr("stroke", c)
                .attr("stroke-width", 1.5)
                .attr("id", "marketLine")
                .attr("d", d3.line()
                    .x(function(d) {
                        return x(d.t)
                    })
                    .y(function(d) {
                        return y(d.c)
                    })
                )

            updateTickerOptions(symbolString);
        })
}

function displayPortfolioGraph(graphNum) {
    var margin = {
            top: 10,
            right: 30,
            bottom: 30,
            left: 60
        },
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
        function readData(data) {
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
                .domain(d3.extent(parsed, function(d) {
                    return d.t;
                }))
                .range([0, width]);
            svg.append("g")
                .attr("transform", "translate(0," + height + ")")
                .call(d3.axisBottom(x));

            // Add Y axis
            var y = d3.scaleLinear()
                .domain([d3.min(parsed, function(d) {
                    return +d.c * 0.9;
                }), d3.max(parsed, function(d) {
                    return +d.c * 1.1;
                })])
                .range([height, 0]);
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
                    .x(function(d) {
                        return x(d.t)
                    })
                    .y(function(d) {
                        return y(d.c)
                    })
                )

            var positionSelect = document.getElementById("positionSelect");
            var option = document.createElement("option");
            option.value = graphNum;
            option.text = Object.keys(data.history[graphNum]);
            positionSelect.appendChild(option);
            for (var i = 0; i < data.history.length; i++) {
                if (i != graphNum) {
                    var option = document.createElement("option");
                    option.value = i;
                    option.text = Object.keys(data.history[i]);
                    positionSelect.appendChild(option);
                }
            }
        })
}

function displayStrategyParameters(strategyName) {
    d3.request("api").post("strategyNames=1",
        function readData(data) {
            data = JSON.parse(data.response);
            var strategies = data.strategies;
            strategies.forEach(function readParameters(strategies, i, a) {
                if (strategies.name == strategyName) {
                    strategies.parameters.forEach(function eachParameter(parameter, i, a) {
                        var paramInput = document.createElement("input");
                        paramInput.setAttribute("type", "number");
                        paramInput.setAttribute("id", "strategyParameter");
                        paramInput.setAttribute("class", "strategyParameterInput");
                        paramInput.setAttribute("name", parameter);
                        paramInput.value = 0.5;
                        var paramLabel = document.createElement("label");
                        paramLabel.setAttribute("id", "strategyParameter");
                        paramLabel.setAttribute("class", "strategyParameterLabel");
                        paramLabel.innerHTML = parameter + ": ";
                        document.getElementById("strategyParameterContainer").appendChild(paramLabel);
                        document.getElementById("strategyParameterContainer").appendChild(paramInput);
                    });
                }
            });
        }
    );
}

function displayStrategyGraph(strategyName, parameterNames, parameterValues) {
    var margin = {
            top: 10,
            right: 30,
            bottom: 30,
            left: 60
        },
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
    var requestString = "strategy=" + strategyName;
    if (parameterNames) {
        for (let i = 0; i < parameterNames.length; i++) {
            requestString += "&" + parameterNames[i] + "=" + parameterValues[i];
        }
    }
    d3.request("api").post(requestString,
        function readData(error, data) {
            if (error) throw error;
            var selectedStrategy = "";
            data = JSON.parse(data.response);

            selectedStrategy = Object.keys(data)[0];
            d3.request("api").post("strategyNames=1",
                function readData(data) {
                    data = JSON.parse(data.response);
                    var strategies = data.strategies;
                    var strategySelect = document.getElementById("strategySelect");
                    strategies.forEach(function readName(strategies, i, a) {
                        var option = document.createElement("option");
                        option.value = strategies.name;
                        option.text = strategies.name;
                        option.setAttribute("id", "strategyOption");
                        strategySelect.appendChild(option);
                        if (strategies.name == selectedStrategy) {
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
                .domain(d3.extent(parsed, function(d) {
                    return d.t;
                }))
                .range([0, width]);
            svg.append("g")
                .attr("transform", "translate(0," + height + ")")
                .call(d3.axisBottom(x));

            // Add Y axis
            var y = d3.scaleLinear()
                .domain([d3.min(parsed, function(d) {
                    return +d.c * 0.9;
                }), d3.max(parsed, function(d) {
                    return +d.c * 1.1;
                })])
                .range([height, 0]);
            var rgb = function(d) {
                for (let i = 0; i < d.length; i++) {
                    switch (d[i].side) {
                        case "SHORT":
                            return "#ff0000";
                            break;
                        case "LONG":
                            return "#00ff00";
                            break;
                        case "HOLD":
                            return "#fffb00";
                            break;
                        case "SELL":
                            return "#0004ff";
                            break;
                    }
                }
            };
            var symbols = function(d) {
                var result = "";
                for (let i = 0; i < d.length; i++) {
                    if (d[i].side != "HOLD") {
                        result += d[i].symbol + " ";
                    }
                }
                return result;
            }
			var focus = svg
			    .append('g')
			    .append('circle')
			      .style("fill", "none")
			      .attr("stroke", "white")
			      .attr('r', 4)
			      .style("opacity", 0)
				  .style("color", "white");

			  // Create the text that travels along the curve of chart
			  var focusText = svg
			    .append('g')
			    .append('text')
			      .style("opacity", 0)
			      .attr("text-anchor", "left")
			      .attr("alignment-baseline", "middle")
				  .style("fill", "white");
			function mouseover() {
			    focus.style("opacity", 1);
			    focusText.style("opacity",1);
			  }

			  function mousemove() {
				
			    mX = d3.mouse(this)[0];
				mY = d3.mouse(this)[1];
				for (var i in data){
					
					bsX = d3.bisector((d) => x(d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(d.t))).left(data[i], mX);
					
					xVal = d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(data[i][bsX].t);
					yVal = data[i][bsX].c;
					focus
					      .attr("cx", x(xVal))
					      .attr("cy", y(yVal));
					    focusText
					      .html("$" + yVal.toFixed(2) + ": " + xVal.toLocaleDateString('en-US'))
					      .attr("x", x(xVal))
					      .attr("y", mY);
					var positionData = data[i][bsX].positionsHeld;
					var dataDict = [];
					for (var posIndex in positionData){
						var position = positionData[posIndex];
						var symbol = position.symbol;
						var qty = position.qty;
						var pnl = position.pnl;
						var entryPrice = position.entry_price
						var currentValue = (qty * entryPrice) + pnl;
						dataDict.push({"symbol" : symbol, "value": currentValue});
					}
					updateBarPlot(dataDict);
					    }
				}
			  function mouseout() {
			    focus.style("opacity", 0);
			    focusText.style("opacity", 0);
			  }

			svg.append('rect')
			    .style("fill", "none")
			    .style("pointer-events", "all")
			    .attr('width', width)
			    .attr('height', height)
			    .on('mouseover', mouseover)
			    .on('mousemove', mousemove)
			    .on('mouseout', mouseout);
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
                    .x(function(d) {
                        return x(d.t)
                    })
                    .y(function(d) {
                        return y(d.c)
                    })
                );
            for (var key in data) {
                for (var obj in data[key]) {
                    dataPoint = data[key][obj];
                    if (dataPoint.tradingActions.length > 0) {
                        timeStamp = d3.timeParse("%Y-%m-%dT%H:%M:%S.%L%Z")(dataPoint.t);
                        closePrice = dataPoint.c;
                        svg.append("text")
                            .attr("x", x(timeStamp))
                            .attr('y', y(closePrice))
                            .attr('fill', rgb(dataPoint.tradingActions))
                            .text(symbols(dataPoint.tradingActions))
                            .attr("id", x(timeStamp))
                            .style("text-anchor", "right")
                            .attr("display", "none");
                        svg.append('circle')
                            .attr('cx', x(timeStamp))
                            .attr('cy', y(closePrice))
                            .attr('r', 3)
                            .attr('fill', rgb(dataPoint.tradingActions))
                            .attr('onmouseover', "document.getElementById(" + x(timeStamp) + ").style.display = 'block';")
                            .attr('onmouseout', "document.getElementById(" + x(timeStamp) + ").style.display = 'none';");
                    }
                }
            }
        })
}


function updateTickerOptions(ticker) {
    d3.request("api").post("savedTickers=30",
        function readData(data) {
            data = JSON.parse(data.response);
            var names = data.tickers;
            var tickerSelect = document.getElementById("tickerSelect");
            tickerSelect.innerHTML = "";
            names.forEach(function readName(name, i, a) {
                var option = document.createElement("option");
                option.value = name;
                option.text = name;
                option.style.color = stringToColor(name);
                option.setAttribute("id", "tickerOption");
                if (name == ticker) {
                    option.setAttribute("selected", "");
                }
                tickerSelect.appendChild(option);
            });
        });

}

function addTicker(ticker) {
    d3.request("api").post("addTicker=" + ticker, function add() {
        updateTickerOptions(ticker);
        d3.selectAll('#tickerGraph').remove();
        d3.selectAll('#tickerOption').remove();
        displayMarketGraph(ticker);
		document.querySelector('#runStrategyButton').click();
    });

}

function removeTicker(ticker) {
    d3.request("api").post("removeTicker=" + ticker, function add() {
        var newTicker = "";
        var currentOptions = document.querySelectorAll('[id=tickerOption]');
        currentOptions.forEach(function getOption(option, i, a) {
            if (option.value != ticker) {
                newTicker = option.value;
            }
        });
        d3.selectAll('#tickerGraph').remove();
        d3.selectAll('#tickerOption').remove();
        updateTickerOptions(ticker);
        displayMarketGraph(newTicker);
		document.querySelector('#runStrategyButton').click();
    });
}

	var margin = {top: 30, right: 30, bottom: 70, left: 60},
	    width = 460 - margin.left - margin.right,
	    height = 400 - margin.top - margin.bottom;

	// append the svg object to the body of the page
	var svg = d3.select("#portfolioBarplotContainer")
	  .append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	  .append("g")
	    .attr("transform",
	          "translate(" + margin.left + "," + margin.top + ")");

	// Initialize the X axis
	var x = d3.scaleBand()
	  .range([ 0, width ])
	  .padding(0.2);
	var xAxis = svg.append("g")
	  .attr("transform", "translate(0," + height + ")")

	// Initialize the Y axis
	var y = d3.scaleLinear()
	  .range([ height, 0]);
	var yAxis = svg.append("g")
	  .attr("class", "myYaxis")
	  // A function that create / update the plot for a given variable:
	  function updateBarPlot(data) {

	    // Update the X axis
	    x.domain(data.map(function(d) { return d.symbol; }))
	    xAxis.call(d3.axisBottom(x))

	    // Update the Y axis
	    y.domain([0, d3.max(data, function(d) { return d.value }) ]);
	    yAxis.transition().duration(10).call(d3.axisLeft(y));

	    // Create the u variable
	    var u = svg.selectAll("rect")
	      .data(data)

	    u
	      .enter()
	      .append("rect") // Add a new rect for each new elements
	      .merge(u) // get the already existing elements as well
	      .transition() // and apply changes to all of them
	      .duration(10)
	        .attr("x", function(d) { return x(d.symbol); })
	        .attr("y", function(d) { return y(d.value); })
	        .attr("width", x.bandwidth())
	        .attr("height", function(d) { return height - y(d.value); })
	        .attr("fill", function(d) { return stringToColor(d.symbol); })

	    // If less group in the new dataset, I delete the ones not in use anymore
	    u
	      .exit()
	      .remove()
	  }

displayPortfolioGraph(0);
displayStrategyGraph("BuyAndHoldEverything");
displayStrategyParameters("BuyAndHoldEverything");
displayMarketGraph("SPY");
document.querySelector('#tickerSelect').addEventListener("change", function() {
    ticker = this.value;
    d3.selectAll('#tickerGraph').remove();
    d3.selectAll('#tickerOption').remove();
    updateTickerOptions(ticker);
    displayMarketGraph(ticker);
});

document.querySelector('#strategySelect').addEventListener("change", function() {
    strategyName = this.value;
    d3.selectAll('#strategyParameter').remove();
    displayStrategyParameters(strategyName);

});
document.querySelector('#tickerAddButton').addEventListener("click", function() {
    var ticker = document.querySelector("#tickerInput").value;
    addTicker(ticker);
});
document.querySelector('#runStrategyButton').addEventListener("click", function() {
    strategyName = document.querySelector('#strategySelect').value;
    const parameterValues = Array.from(document.querySelectorAll('.strategyParameterInput')).map(input => input.value)
    const parameterNames = Array.from(document.querySelectorAll('.strategyParameterInput')).map(input => input.name)
    d3.selectAll('#strategyOption').remove();
    d3.selectAll('#strategyGraph').remove();
    displayStrategyGraph(strategyName, parameterNames, parameterValues);
});
document.querySelector('#tickerRemoveButton').addEventListener("click", function() {
    var ticker = document.querySelector("#tickerInput").value;
    removeTicker(ticker);
});