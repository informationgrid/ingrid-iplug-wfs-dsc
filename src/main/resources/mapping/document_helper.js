/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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

function addOutputWithAttributes(parent, elementName, attrNames, attrValues) {
	var element = document.createElement(elementName);
	for (var i=0, count=attrNames.length; i<count; i++) {
		element.setAttribute(attrNames[i], attrValues[i]);
	}
	parent.appendChild(element);
	return element;
}

// add elements/styles for correct display in portal (header)
function addDetailHeaderWrapper(parent) {
	var result = addOutputWithAttributes(parent, "section", ["class"], ["block block--light block--pad-top"]);
	result = addOutputWithAttributes(result, "div", ["class"], ["ob-box-wide ob-box-padded ob-box-center"]);
	result = addOutputWithAttributes(result, "article", ["id", "class"], ["detail_meta_header", "content ob-container"]);
	result = addOutputWithAttributes(result, "form", ["class"], ["box box--medium"]);
	result = addOutputWithAttributes(result, "div", ["class"], ["box__content ob-container"]);
	return result;
}

//add elements/styles for correct display in portal (details)
function addDetailDetailsWrapper(parent) {
	var result = addOutputWithAttributes(parent, "section", ["id","class"], ["detail_meta","block"]);
	result = addOutputWithAttributes(result, "div", ["class"], ["ob-box-wide ob-box-padded ob-box-center ob-rel"]);
	result = addOutputWithAttributes(result, "article", ["class"], ["content ob-container ob-box-wide"]);
	result = addOutputWithAttributes(result, "form", ["class"], ["box box--medium"]);
	result = addOutputWithAttributes(result, "div", ["class"], ["box__content ob-container"]);
	return result;
}
