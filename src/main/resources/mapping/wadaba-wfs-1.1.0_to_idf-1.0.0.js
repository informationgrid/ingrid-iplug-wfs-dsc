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
/**
 * Wasserstrassendatenbank WFS to IDF Document mapping
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
if (javaVersion.indexOf( "1.8" ) === 0) {
    load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.tools);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.index);
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.geo.utils.transformation);

if (log.isDebugEnabled()) {
	log.debug("Mapping wfs record "+wfsRecord.getId()+" to idf document");
}

// get the xml content of the record
var recordNode = wfsRecord.getOriginalResponse().get(0);

//---------- <idf:body> ----------
var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

// header
var header = addDetailHeaderWrapper(idfBody);

// add the title
addOutput(header, "h1", getTitle(recordNode));

//add the summary
addOutput(header, "p", getSummary(recordNode));

//add the bounding box
var boundingBox = getBoundingBox(recordNode);
addOutput(header, "h2", "Ort:");
var coordList = addOutput(header, "ul");
addOutput(coordList, "li", "Nord: "+boundingBox.y2);
addOutput(coordList, "li", "West: "+boundingBox.x1);
addOutput(coordList, "li", "Ost: "+boundingBox.x2);
addOutput(coordList, "li", "S&uuml;d: "+boundingBox.y1);

// add the map preview
addOutput(header, "div", getMapPreview(recordNode));

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

// functions
function getTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//ms:BENENNUNG");
	return title;
}

function getSummary(recordNode) {
	var result = xPathUtils.getString(recordNode, "//ms:LAYERNAME");
	var desc = xPathUtils.getString(recordNode, "//ms:KURZBEZ");
	if (hasValue(desc)) {
		result += " - "+desc;
	}
	return result;
}

function getBoundingBox(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		return {
			// Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
			y1: lowerCoords[0], // south
			x1: lowerCoords[1], // west
			y2: upperCoords[0], // north
			x2: upperCoords[1]  // east
		}
	}
}

function getMapPreview(recordNode) {
    var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        // BBOX
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        // Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
        var S = Number(lowerCoords[0]); // SOUTH y1
        var E = Number(upperCoords[1]); // EAST, x2
        // NOTICE: 
        // lowerCorner and upperCorner have same coordinates in Wadaba !? -> BBOX is a POINT !
        var BBOX = "" + (E - 0.048) + "," + (S - 0.012) + "," + (E + 0.048) + "," + (S + 0.012);

        // transform "WGS 84 (EPSG:4326)" to "ETRS89 / UTM zone 32N (EPSG:25832)"
        var transfCoords = CoordTransformUtil.getInstance().transform(
                E, S,
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"),
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"));
        var E_25832 = transfCoords[0];
        var S_25832 = transfCoords[1];
//        log.warn("transfCoords: IN(" + E + "," + S + "), OUT(" + E_25832 + "," + S_25832 + ")");

        var addHtml = "<a href=\"/kartendienste?lang=de&topic=favoriten&bgLayer=wmts_topplus_web&layers=bwastr_vnetz&layers_opacity=0.4&E=" + E_25832 + "&N=" + S_25832 + "&zoom=15&crosshair=marker\" style=\"padding: 0 0 0 0;\">" +
            "<div style=\"background-image: url(http://sgx.geodatenzentrum.de/wms_topplus_web_open?VERSION=1.3.0&amp;REQUEST=GetMap&amp;CRS=CRS:84&amp;BBOX=" + BBOX +
            "&amp;LAYERS=web&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px; margin: 10px 0 0 0;\">" +
            "<img src=\"/ingrid-portal-apps/images/map_punkt.png\" alt=\"\">" +
            "</div></a>";

        if (log.isDebugEnabled()) {
            log.debug("MapPreview Html: " + addHtml);
        }

        return addHtml;
    }
}
