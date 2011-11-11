
function addOutput(parent, elementName, textContent) {
	var element = document.createElement(elementName);
	if (textContent != undefined) {
		element.appendChild(document.createTextNode(textContent));
	}
	parent.appendChild(element);
	return element;
}

function addLink(parent, name, url) {
	var link = document.createElement("a");
	link.setAttribute("href", url);
	link.setAttribute("target", "_blank");
	link.appendChild(document.createTextNode(name));
	parent.appendChild(link);
	return link;
}
