function addOutput(parent, elementName, textContent) {
	var element = document.createElement(elementName);
	if (textContent != undefined) {
		element.appendChild(document.createTextNode(textContent));
	}
	parent.appendChild(element);
	return element;
}

function addOutputWithLinks(parent, elementName, textContent) {
	var element = document.createElement(elementName);
	if (textContent != undefined) {
		// tokenize string and create links if necessary
		var words = textContent.split(" ");
		for (var i=0, count=words.length; i<count; i++) {
			var text = words[i];
			
			// add a link for an url
			if (isUrl(text)) {
				addLink(element, text, text, "_blank");
			}
			// add a mailto link for an email address
			else if (isEmail(text)) {
				addLink(element, text, "mailto:"+text);
			}
			// default: add the plain text
			else {
				element.appendChild(document.createTextNode(text));
			}

			// add space
			if (i<count-1) {
				element.appendChild(document.createTextNode(" "));
			}
		}
	}
	parent.appendChild(element);
	return element;
}

function addLink(parent, name, url, target) {
	var link = document.createElement("a");
	link.setAttribute("href", url);
	if (target != undefined) {
		link.setAttribute("target", target);
	}
	link.appendChild(document.createTextNode(name));
	parent.appendChild(link);
	return link;
}
