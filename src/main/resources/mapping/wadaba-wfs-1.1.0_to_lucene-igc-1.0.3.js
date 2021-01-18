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
 * Wasserstrassendatenbank WFS to Lucene Document mapping according to
 * mapping IGC 1.0.3 
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 * 
 * The following global variable are passed from the application:
 * 
 * @param wfsRecord
 *            A WFSFeature instance, that defines the input
 * @param document
 *            A lucene Document instance, that defines the output
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
importPackage(Packages.de.ingrid.geo.utils.transformation);

if (log.isDebugEnabled()) {
	log.debug("Mapping wfs record "+wfsRecord.getId()+" to lucene document");
}

// get the xml content of the record
var recordNode = wfsRecord.getOriginalResponse().get(0);

// add id field
addToDoc(document, "t01_object.obj_id", wfsRecord.getId(), true);

//add the title
mapTitle(recordNode);

//add the summary
mapSummary(recordNode);

//add the bounding box
mapBoundingBox(recordNode);

//add the map preview
mapPreview(recordNode);

// add details (content of all child nodes)
var detailNodes = recordNode.getChildNodes();
for (var i=0, count=detailNodes.length; i<count; i++) {
	var detailNode = detailNodes.item(i);
	var nodeName = detailNode.getLocalName();
	if (hasValue(nodeName)) {
		addToDoc(document, nodeName.toLowerCase(), detailNode.getTextContent(), true);
	}
}

function mapTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//ms:BENENNUNG");
	addToDoc(document, "title", title, true);
}

function mapSummary(recordNode) {
	var result = xPathUtils.getString(recordNode, "//ms:LAYERNAME");
	var desc = xPathUtils.getString(recordNode, "//ms:KURZBEZ");
	if (hasValue(desc)) {
		result += " - "+desc;
	}
	addToDoc(document, "summary", result, true);
}

function mapBoundingBox(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        // Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
		addNumericToDoc(document, "y1", lowerCoords[0], false); // south
		addNumericToDoc(document, "x1", lowerCoords[1], false); // west
		addNumericToDoc(document, "y2", upperCoords[0], false); // north
		addNumericToDoc(document, "x2", upperCoords[1], false); // east
	}
}

function mapPreview(recordNode) {
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
            log.debug("Mapping field \"additional_html_1\": " + addHtml);
        }

        addToDoc(document, "additional_html_1", addHtml, false);
    }
}
