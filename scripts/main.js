// set the dimensions and margins of the graph
divId = 0;

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
            // Add the line
            svg.append("path")
                .datum(parsed)
                .attr("fill", "none")
                .attr("stroke", "steelblue")
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
	if (parameterNames){
		for (let i = 0; i < parameterNames.length; i++) {
	 		requestString += "&"+ parameterNames[i] + "=" + parameterValues[i];
		}
	}
	console.log(requestString);
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
                switch (d[0].side) {
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
            };
            var symbols = function(d) {
                var result = "";

                for (let i = 0; i < Object.keys(d).length; i++) {
                    if (d[i].side != "HOLD") {
                        result += d[i].symbol + " ";
                    }
                }
                return result;
            }
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
            for (var obj in data.actions) {
                svg.append('circle')
                    .attr('cx', x(parsed[obj].t))
                    .attr('cy', y(parsed[obj].c))
                    .attr('r', 3)
                    .attr('fill', rgb(data.actions[obj]));
                svg.append("text")
                    .attr("x", x(parsed[obj].t))
                    .attr('y', y(parsed[obj].c))
                    .attr('fill', rgb(data.actions[obj]))
                    .text(symbols(data.actions[obj]));

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
    });
}
addTicker("SPY");
displayPortfolioGraph(0);
displayStrategyGraph("BuyAndHoldEverything");
displayStrategyParameters("BuyAndHoldEverything");
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
	console.log(parameterValues);
    displayStrategyGraph(strategyName, parameterNames, parameterValues);
});
document.querySelector('#tickerRemoveButton').addEventListener("click", function() {
    var ticker = document.querySelector("#tickerInput").value;
    removeTicker(ticker);
});