/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
 * ZDM WFS to IDF Document mapping
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 * 
 * The following global variable are passed from the application:
 * 
 * @param wfsRecord
 *			A WFSFeature/WFSFeatureType instance, that defines the input
 * @param document
 *			A IDF Document (XML-DOM) instance, that defines the output
 * @param xPathUtils
 * 			  A de.ingrid.utils.xpath.XPathUtils instance
 * @param log
 *			A Log instance
 */
if (javaVersion.indexOf( "1.8" ) === 0) {
	load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.tools);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.index);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.wfsclient);
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.geo.utils.transformation);

if (log.isDebugEnabled()) {
	log.debug("Mapping wfs record "+wfsRecord.getId()+" of type "+wfsRecord.getClass().getName()+" to idf document");
}

//
//WFSFeature mapping
//
if (wfsRecord instanceof WFSFeature) {
	// get the xml content of the record
	var recordNode = wfsRecord.getOriginalResponse().get(0);

	//---------- <idf:body> ----------
	var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

	// header
	var header = addDetailHeaderWrapper(idfBody);

	// add the title
	addOutput(header, "h1", getFeatureTitle(recordNode));

	//add the summary
	addOutput(header, "p", getFeatureSummary(recordNode));

	//add the bounding box
	var boundingBox = getFeatureBoundingBox(recordNode);
	addOutput(header, "h2", "Ort:");
	var coordList = addOutput(header, "ul");
	addOutput(coordList, "li", "Nord: "+boundingBox.y2);
	addOutput(coordList, "li", "West: "+boundingBox.x1);
	addOutput(coordList, "li", "Ost: "+boundingBox.x2);
	addOutput(coordList, "li", "S&uuml;d: "+boundingBox.y1);

	// add the map preview
	addOutput(header, "div", getFeatureMapPreview(recordNode));
	addOutput(header, "br");

	// details
	var details = addDetailDetailsWrapper(idfBody);

	// add details (content of all child nodes)
	addOutput(details, "h2", "Details:");
	var detailList = addOutput(details, "ul");
	var detailNodes = recordNode.getChildNodes();
	for (var i=0, count=detailNodes.length; i<count; i++) {
		var detailNode = detailNodes.item(i);
		var nodeName = detailNode.getLocalName();
		if (hasValue(nodeName)) {
			addOutputWithLinks(detailList, "li", detailNode.getLocalName()+": "+detailNode.getTextContent());
		}
	}
	addOutput(details, "br");
}
//
// WFSFeatureType mapping
//
else if (wfsRecord instanceof WFSFeatureType) {
	// get the xml content of the record
	var recordNode = wfsRecord.getOriginalResponse().get(0);
	var detailNode = wfsRecord.getOriginalResponse().get(1);

	//---------- <idf:body> ----------
	var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

	// header
	var header = addDetailHeaderWrapper(idfBody);

	// add the title
	addOutput(header, "h1", getFeatureTypeTitle(recordNode));

	//add the summary
	addOutput(header, "p", getFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures()));

	//add the bounding box
	var boundingBox = getFeatureTypeBoundingBox(recordNode);
	addOutput(header, "h2", "Ort:");
	var coordList = addOutput(header, "ul");
	addOutput(coordList, "li", "Nord: "+boundingBox.y2);
	addOutput(coordList, "li", "West: "+boundingBox.x1);
	addOutput(coordList, "li", "Ost: "+boundingBox.x2);
	addOutput(coordList, "li", "S&uuml;d: "+boundingBox.y1);

	// add the map preview
	addOutput(header, "div", getFeatureTypeMapPreview(recordNode));
	addOutput(header, "br");

	// details
	var details = addDetailDetailsWrapper(idfBody);

	// add details (feature attributes)
	addOutput(details, "h2", "Details:");
	addOutput(details, "h4", "Feature Attribute:");
	var detailList = addOutput(details, "ul");
	// select elements ignoring the namespace
	var attributeNodes = xPathUtils.getNodeList(detailNode, "//*/*[local-name()='extension'][@base='gml:AbstractFeatureType']/*[local-name()='sequence']/*[local-name()='element']");
	if (attributeNodes) {
		for (var i=0, count=attributeNodes.getLength(); i<count; i++) {
			var attributeNode = attributeNodes.item(i);
			var name = attributeNode.getAttributeNode("name");
			if (hasValue(name)) {
				addOutput(detailList, "li", name.getTextContent());
			}
		}
	}
	addOutput(header, "br");
	
	// show features, if loaded
	if (hasValue(wfsRecord.getFeatures())) {
		addOutput(details, "h2", "Features:");
		var features = wfsRecord.getFeatures();
		for (var j=0; j<features.size(); j++) {
			var recordNode = features.get(j).getOriginalResponse().get(0);

			// add the title
			addOutput(details, "h4", getFeatureTitle(recordNode));

			//add the summary
			addOutput(details, "p", getFeatureSummary(recordNode));

			//add the bounding box
			var boundingBox = getFeatureBoundingBox(recordNode);
			addOutput(details, "h6", "Ort:");
			var coordList = addOutput(details, "ul");
			addOutput(coordList, "li", "Nord: "+boundingBox.y2);
			addOutput(coordList, "li", "West: "+boundingBox.x1);
			addOutput(coordList, "li", "Ost: "+boundingBox.x2);
			addOutput(coordList, "li", "S&uuml;d: "+boundingBox.y1);

			// add the map preview
			addOutput(details, "div", getFeatureMapPreview(recordNode));
			addOutput(details, "br");

			// add details (content of all child nodes)
			var detailList = addOutput(details, "ul");
			var detailNodes = recordNode.getChildNodes();
			for (var i=0, count=detailNodes.length; i<count; i++) {
				var detailNode = detailNodes.item(i);
				var nodeName = detailNode.getLocalName();
				if (hasValue(nodeName)) {
					addOutputWithLinks(detailList, "li", detailNode.getLocalName()+": "+detailNode.getTextContent());
				}
			}
			addOutput(details, "br");
		}
	}
}

//
// WFSFeature functions
//

function getFeatureTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "/*/@gml:id");
	return title;
}

function getFeatureSummary(recordNode) {
	var result = "";
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var srsName = xPathUtils.getString(gmlEnvelope, "@srsName");
		if (hasValue(srsName)) {
			result = result + srsName + ": ";
		}
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		// west, south / east, north
		result = result + lowerCoords[1] + ", " + lowerCoords[0] + " / " + upperCoords[1] + ", " + upperCoords[0];
	}
	return result;
}

function getFeatureBoundingBox(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		return getBoundingBox(lowerCoords, upperCoords);
	}
}

function getFeatureMapPreview(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		// BBOX
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");

		//  Fields for link
//		var BWSTR = xPathUtils.getString(recordNode, "//ms:BWSTR");
//		var KM_ANF_D = xPathUtils.getString(recordNode, "//ms:KM_ANF_D");
//		var linkUrl = "http://wsvmapserv.wsv.bvbs.bund.de/ol_bwastr/index.html?bwastr=" + BWSTR + "&kmwert=" + KM_ANF_D + "&abstand=0&zoom=15";

		return getMapPreview(lowerCoords, upperCoords, false/*, linkUrl*/);
	}
}

//
// WFSFeatureType functions
//

function getFeatureTypeTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Title");
	return title;
}

function getFeatureTypeSummary(recordNode, numFeatures) {
	var summary = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Abstract");
	var featureSummary = numFeatures+" Feature(s)";
	return summary + "<br>" + featureSummary;
}

function getFeatureTypeBoundingBox(recordNode) {
	var owsBoundingBox = xPathUtils.getNode(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox");
	if (hasValue(owsBoundingBox)) {
		var lowerCoords = xPathUtils.getString(owsBoundingBox, "ows:LowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(owsBoundingBox, "ows:UpperCorner").split(" ");

		return getBoundingBox(lowerCoords, upperCoords);
	}
}

function getFeatureTypeMapPreview(recordNode) {
	var owsBoundingBox = xPathUtils.getNode(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox");
	if (hasValue(owsBoundingBox)) {
		// BBOX
		var lowerCoords = xPathUtils.getString(owsBoundingBox, "ows:LowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(owsBoundingBox, "ows:UpperCorner").split(" ");

		return getMapPreview(lowerCoords, upperCoords, true);
	}
}

//
// Common functions
//

function getBoundingBox(lowerCoords, upperCoords) {
	return {
		// Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
		y1: lowerCoords[0], // south
		x1: lowerCoords[1], // west
		y2: upperCoords[0], // north
		x2: upperCoords[1]  // east
	}
}

function getMapPreview(lowerCoords, upperCoords, isWGS84, linkUrl) {
	// Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
	var S = Number(lowerCoords[1]); // SOUTH y1
	var E = Number(upperCoords[0]); // EAST, x2

	if (!isWGS84) {
		// transform "ETRS89 / UTM zone 32N (EPSG:25832)" to "WGS 84 (EPSG:4326)"
		var transfCoords = CoordTransformUtil.getInstance().transform(
				E, S,
				CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"),
				CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"));
	}
	var E_4326 = isWGS84 ? E : transfCoords[0];
	var S_4326 = isWGS84 ? S : transfCoords[1];

	// lowerCorner and upperCorner have same coordinates !? -> BBOX is a POINT !
	var BBOX = "" + (E_4326 - 0.048) + "," + (S_4326 - 0.012) + "," + (E_4326 + 0.048) + "," + (S_4326 + 0.012);

	var addHtml = "" + 
		(linkUrl ? "<a href=\"" + linkUrl + "\" target=\"_blank\" style=\"padding: 0 0 0 0;\">" : "") +
		"<div style=\"background-image: url(https://sgx.geodatenzentrum.de/wms_topplus_open?VERSION=1.3.0&amp;REQUEST=GetMap&amp;CRS=CRS:84&amp;BBOX=" + BBOX +
		"&amp;LAYERS=web&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px; margin: 10px 0 0 0;\">" +
		"</div>";
		+ (linkUrl ? "</a>" : "");

	if (log.isDebugEnabled()) {
		log.debug("MapPreview Html: " + addHtml);
	}

	return addHtml;
}