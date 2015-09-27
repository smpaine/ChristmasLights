var sendCommand = function(command) {
	var xmlhttp;
	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for older browsers
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			document.getElementById("console").value = xmlhttp.responseText + "\n" + document.getElementById("console").value;
		}
	}
	xmlhttp.open("GET", "http://192.168.0.74/arduino/" + command, true);
	xmlhttp.send();
}

var handleClick = function() {
	var buttonName = this.textContent || this.innerText;
	document.getElementById("console").value = "Button, " + buttonName + ", has been clicked.\n" + document.getElementById("console").value;
	if (buttonName == "All Red") {
		sendCommand("group/red/255");
	} else if (buttonName == "All Blue") {
		sendCommand("group/blue/255");
	} else if (buttonName == "All Green") {
		sendCommand("group/green/255");
	} else if (buttonName == "All Yellow") {
		sendCommand("group/yellow/255");
	} else if (buttonName == "All ON") {
		sendCommand("allOn");
	} else if (buttonName == "All OFF") {
		sendCommand("clear");
	} else if (buttonName == "Demo") {
		sendCommand("demo");
	} else {
		sendCommand("led/" + buttonName + "/255");
	}
}

var createButton = function(divid, buttonText) {
	var button = document.createElement("BUTTON");
	var text = document.createTextNode(buttonText);
	button.appendChild(text);
	button.addEventListener("click", handleClick);
	document.getElementById(divid).appendChild(button);
}

var createGroupButtons = function(divid) {
	createButton(divid, 'All Red');
	createButton(divid, 'All Blue');
	createButton(divid, 'All Green');
	createButton(divid, 'All Yellow');
	createButton(divid, 'All ON');
	createButton(divid, 'All OFF');
	createButton(divid, 'Demo');
}

var createIndividualLEDButtons = function(divid) {
	var i = 0;
	var numLights = 108;
	for (i=1; i<=numLights; i++) {
		createButton(divid, i);
	}
}

createGroupButtons('groupFunctions');
createIndividualLEDButtons('indvLeds');

sendCommand("refresh");

