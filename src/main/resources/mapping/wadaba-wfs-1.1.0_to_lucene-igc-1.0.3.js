/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

if (log.isDebugEnabled()) {
	log.debug("Mapping wfs record "+wfsRecord.getId()+" to lucene document");
}

// get the xml content of the record
var recordNode = wfsRecord.getOriginalResponse();

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
        // Latitude first (Breitengrad = y), longitude second (L�ngengrad = x)
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
        var S = Number(lowerCoords[0]); // SOUTH
        var E = Number(upperCoords[1]); // EAST
        // NOTICE: 
        // lowerCorner and upperCorner have same coordinates in Wadaba !? -> BBOX is a POINT !
        var BBOX = "" + (E - 0.048) + "," + (S - 0.012) + "," + (E + 0.048) + "," + (S + 0.012);

        //  Fields for link
        var BWSTR = xPathUtils.getString(recordNode, "//ms:BWSTR");
        var KM_ANF_D = xPathUtils.getString(recordNode, "//ms:KM_ANF_D");

        var addHtml = "<a href=\"http://atlas.wsv.bvbs.bund.de/positionfinder/map/index.html?bwastr=" + BWSTR.trim() + "&kmwert=" + KM_ANF_D + "&abstand=0&zoom=15\" target=\"_blank\" style=\"padding: 0 0 0 0;\">" +
            "<div style=\"background-image: url(http://atlas.wsv.bvbs.bund.de/tk/wms?VERSION=1.1.1&amp;REQUEST=GetMap&amp;SRS=EPSG:4326&amp;BBOX=" + BBOX +
            "&amp;LAYERS=TK1000,TK500,TK200,TK100,TK50,TK25&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px; margin: 10px 0 0 0;\">" +
            "<div style=\"background-image: url(http://atlas.wsv.bund.de/ienc/group/wms?VERSION=1.1.1&amp;REQUEST=GetMap&amp;SRS=EPSG:4326&amp;Transparent=True&amp;BBOX=" + BBOX +
            "&amp;Layers=Harbour&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px;\">" +
            "<img src=\"/ingrid-portal-apps/images/map_punkt.png\" alt=\"\">" +
            "</div></div></a>";

        if (log.isDebugEnabled()) {
            log.debug("Mapping field \"additional_html_1\": " + addHtml);
        }

        addToDoc(document, "additional_html_1", addHtml, false);
    }
}
