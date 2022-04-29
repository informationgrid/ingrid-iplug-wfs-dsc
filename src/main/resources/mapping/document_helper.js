/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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

/* New portal layout */ 

// add elements/styles for correct display in portal (header)
function addDetailHeaderWrapperNewLayout(parent) {
    var result = addOutputWithAttributes(parent, "div", ["class"], ["banner-noimage m-filter"]);
    result = addOutputWithAttributes(result, "div", ["class", "style"], ["page-wrapper", "background-image: url('/decorations/layout/uvp/images/template/drops-subpage.svg');"]);
    result = addOutputWithAttributes(result, "div", ["class"], ["row"]);
    return result;
}

function addDetailHeaderWrapperNewLayoutBackSearch(parent) {
    var result = addOutputWithAttributes(parent, "section", ["class"], ["xsmall-24 large-6 xlarge-6 columns"]);
    var a = addOutputWithAttributes(result, "a", ["class", "href", "title"], ["helper icon", "/freitextsuche", "Alle Suchergebnisse"]);
    addOutputWithAttributes(a, "span", ["class"], ["ic-ic-arrow-left"]);
    var text = addOutputWithAttributes(a, "span", ["class"], ["text text-normal"]);
    text.appendChild(document.createTextNode("Alle Suchergebnisse"));
}

function addDetailHeaderWrapperNewLayoutTitle(parent, title) {
    var result = addOutputWithAttributes(parent, "section", ["class"], ["xsmall-24 large-18 xlarge-18 columns"]);
    addOutput(result, "h2", title);
}

function addDetailHeaderWrapperNewLayoutDetailNavigation(parent, summary, detail, source, organisation) {
    var result = addOutputWithAttributes(parent, "div", ["class"], ["xsmall-24 large-6 xlarge-6 columns"]);
    result = addOutputWithAttributes(result, "div", ["class", "data-accordion", "data-allow-all-closed", "role"], ["accordion accordion-filter-group filter", "", "true", "tablist"]);
    var filter = addOutputWithAttributes(result, "div", ["class", "data-accordion-item"], ["accordion-item accordion-item-filter-group", ""]);
    var a = addOutputWithAttributes(filter, "a", ["class", "href", "role", "id", "aria-expanded", "aria-selected", "aria-controls"], ["accordion-title accordion-title-filter-group hide-for-large", "#", "tab", "detail-content-accordion-label", "false", "false", "detail-content-accordion"]);
    addOutput(a, "span", "Inhalt");
    var filterContent = addOutputWithAttributes(filter, "div", ["class", "data-tab-content", "aria-hidden", "role", "aria-labelledby", "id", "tabindex"], ["accordion-content filter-wrapper", "", "true", "tabpanel", "detail-content-accordion-label", "detail-content-accordion", "1"]);
    var filterList = addOutputWithAttributes(filterContent, "ul", ["class", "data-accordion", "data-allow-all-closed", "role"], ["accordion filter-group nav-group", "", "true", "tablist"]);
    
    var filterEntry = addOutputWithAttributes(filterList, "li", ["class", "data-accordion-item"], ["accordion-item ", ""]);
    var filterEntryHref = addOutputWithAttributes(filterEntry, "a", ["class", "href", "role", "id", "aria-expanded", "aria-selected", "aria-controls"], ["accordion-title js-anchor-target", "#detail_overview", "tab", "detail_overview-accordion-label", "false", "false", "detail_overview-accordion"]);
    addOutput(filterEntryHref, "span", "Übersicht");
    var filterEntrySub = addOutputWithAttributes(filterEntry, "div", ["class", "data-tab-content", "role", "id", "aria-hidden", "aria-labelledby"], ["accordion-content is-hidden", "", "tab", "detail_overview-accordion", "true", "detail_overview-accordion-label"]);
    addOutputWithAttributes(filterEntrySub, "div", ["class"], ["boxes"]);
    
    if(summary.length > 0) {
        var filterEntry = addOutputWithAttributes(filterList, "li", ["class", "data-accordion-item"], ["accordion-item ", ""]);
        var filterEntryHref = addOutputWithAttributes(filterEntry, "a", ["class", "href", "role", "id", "aria-expanded", "aria-selected", "aria-controls"], ["accordion-title js-anchor-target", "#detail_description", "tab", "detail_description-accordion-label", "false", "false", "detail_description-accordion"]);
        addOutput(filterEntryHref, "span", "Beschreibung");
        var filterEntrySub = addOutputWithAttributes(filterEntry, "div", ["class", "data-tab-content", "role", "id", "aria-hidden", "aria-labelledby"], ["accordion-content is-hidden", "", "tab", "detail_description-accordion", "true", "detail_description-accordion-label"]);
        addOutputWithAttributes(filterEntrySub, "div", ["class"], ["boxes"]);
    }

    if(detail.length > 0) {
        var filterEntry = addOutputWithAttributes(filterList, "li", ["class", "data-accordion-item"], ["accordion-item ", ""]);
        var filterEntryHref = addOutputWithAttributes(filterEntry, "a", ["class", "href", "role", "id", "aria-expanded", "aria-selected", "aria-controls"], ["accordion-title js-anchor-target", "#detail_details", "tab", "detail_details-accordion-label", "false", "false", "detail_details-accordion"]);
        addOutput(filterEntryHref, "span", "Details");
        var filterEntrySub = addOutputWithAttributes(filterEntry, "div", ["class", "data-tab-content", "role", "id", "aria-hidden", "aria-labelledby"], ["accordion-content is-hidden", "", "tab", "detail_details-accordion", "true", "detail_details-accordion-label"]);
        addOutputWithAttributes(filterEntrySub, "div", ["class"], ["boxes"]);
    }
    
    if(source.length > 0 || organisation.length > 0) {
        var filterEntry = addOutputWithAttributes(filterList, "li", ["class", "data-accordion-item"], ["accordion-item ", ""]);
        var filterEntryHref = addOutputWithAttributes(filterEntry, "a", ["class", "href", "role", "id", "aria-expanded", "aria-selected", "aria-controls"], ["accordion-title js-anchor-target", "#metadata_info", "tab", "metadata_info-accordion-label", "false", "false", "metadata_info-accordion"]);
        addOutput(filterEntryHref, "span", "Metadatensatz");
        var filterEntrySub = addOutputWithAttributes(filterEntry, "div", ["class", "data-tab-content", "role", "id", "aria-hidden", "aria-labelledby"], ["accordion-content is-hidden", "", "tab", "metadata_info-accordion", "true", "metadata_info-accordion-label"]);
        addOutputWithAttributes(filterEntrySub, "div", ["class"], ["boxes"]);
    }
}

function addDetailTableRowWrapperNewLayout (parent, title, content) {
    var result = addOutputWithAttributes(parent, "div", ["class"], ["table table--lined"]);
    result = addOutput(result, "table", "");
    result = addOutput(result, "tbody", "");
    result = addOutput(result, "tr", "");
    addOutput(result, "th", title);
    result = addOutput(result, "td", "");
    addOutput(result, "p", content);
}
