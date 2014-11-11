/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/**
 * PEGELONLINE Web Feature Service Aktuell (WFS Aktuell) to IDF Document mapping
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 * 
 * The following global variable are passed from the application:
 * 
 * @param wfsRecord
 *            A WFSFeature instance, that defines the input
 * @param document
 *            A IDF Document (XML-DOM) instance, that defines the output
 * @param xPathUtils
 * 			  A de.ingrid.utils.xpath.XPathUtils instance
 * @param log
 *            A Log instance
 */
importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.tools);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.index);
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);

if (log.isDebugEnabled()) {
	log.debug("Mapping wfs record "+wfsRecord.getId()+" to idf document");
}

// get the xml content of the record
var recordNode = wfsRecord.getOriginalResponse();

//---------- <idf:body> ----------
var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

// add the title
addOutput(idfBody, "h1", getTitle(recordNode));

//add the summary
addOutput(idfBody, "p", getSummary(recordNode));

//add the bounding box
var boundingBox = getBoundingBox(recordNode);
addOutput(idfBody, "h2", "Ort:");
var coordList = addOutput(idfBody, "ul");
addOutput(coordList, "li", "Nord: "+boundingBox.y2);
addOutput(coordList, "li", "West: "+boundingBox.x1);
addOutput(coordList, "li", "Ost: "+boundingBox.x2);
addOutput(coordList, "li", "Süd: "+boundingBox.y1);
addOutput(idfBody, "br");

// add details
addOutput(idfBody, "h2", "Details:");
var detailList = addOutput(idfBody, "ul");
addOutput(detailList, "li", "Gewässer: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:water"));
addOutput(detailList, "li", "Station: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:station"));
addOutput(detailList, "li", "Station ID: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:station_id"));
addOutput(detailList, "li", "Kilometer: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:kilometer"));
addOutput(detailList, "li", "Datum: "+DateUtil.formatDate(xPathUtils.getString(recordNode, "/gk:waterlevels/gk:date")));
addOutput(detailList, "li", "Wert: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:value"));
addOutput(detailList, "li", "Einheit: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:unit"));
var chartElement = addOutput(detailList, "li", "Chart: ");
var chartUrl = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:chart_url");
addLink(chartElement, chartUrl, chartUrl);
addOutput(detailList, "li", "Trend: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:trend"));
addOutput(detailList, "li", "Status: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:status"));
addOutput(detailList, "li", "Kommentar: "+xPathUtils.getString(recordNode, "/gk:waterlevels/gk:comment"));
addOutput(idfBody, "br");

// functions
function getTitle(recordNode) {
	var part1 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:water");
	var part2 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:station");
	var part3 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:kilometer");
	return part1+" "+part2+" (km "+part3+")";
}

function getSummary(recordNode) {
	var part1 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:date");
	var part2 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:value");
	var part3 = xPathUtils.getString(recordNode, "/gk:waterlevels/gk:unit");
	return DateUtil.formatDate(part1)+": "+part2+""+part3;
}

function getBoundingBox(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "/gk:waterlevels/gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		return {
            // Latitude first (Breitengrad = y), longitude second (Längengrad = x)
			y1: lowerCoords[0], // south
			x1: lowerCoords[1], // west
			y2: upperCoords[0], // north
			x2: upperCoords[1]  // east
		}
	}
}
