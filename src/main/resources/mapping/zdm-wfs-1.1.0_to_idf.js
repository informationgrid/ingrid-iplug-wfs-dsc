/**
 * ZDM WFS to IDF Document mapping
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
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
addOutput(coordList, "li", "S&uuml;d: "+boundingBox.y1);

// add the map preview
addOutput(idfBody, "div", getMapPreview(recordNode));
addOutput(idfBody, "br");

// add details (content of all child nodes)
addOutput(idfBody, "h2", "Details:");
var detailList = addOutput(idfBody, "ul");
var detailNodes = recordNode.getChildNodes();
for (var i=0, count=detailNodes.length; i<count; i++) {
	var detailNode = detailNodes.item(i);
	var nodeName = detailNode.getLocalName();
	if (hasValue(nodeName)) {
		addOutputWithLinks(detailList, "li", detailNode.getLocalName()+": "+detailNode.getTextContent());
	}
}
addOutput(idfBody, "br");

// functions
function getTitle(recordNode) {
    var title = xPathUtils.getString(recordNode, "/*/@gml:id");
	return title;
}

function getSummary(recordNode) {
    var result = "";
    var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        var srsName = xPathUtils.getString(gmlEnvelope, "@srsName");
        if (hasValue(srsName)) {
            result = result + srsName + ": ";
        }
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        result = result + lowerCoords[0] + ", " + lowerCoords[1] + " / " + upperCoords[0] + ", " + upperCoords[1];
    }
	return result;
}

function getBoundingBox(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		return {
			x1: lowerCoords[0],
			x2: upperCoords[0],
			y1: lowerCoords[1],
			y2: upperCoords[1]
		}
	}
}

function getMapPreview(recordNode) {
    var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        // BBOX
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        var W = Number(lowerCoords[0]); // WEST
        var N = Number(upperCoords[1]); // NORTH
        var BBOX = "" + (N - 0.048) + "," + (W - 0.012) + "," + (N + 0.048) + "," + (W + 0.012);

        //  Fields for link
//        var BWSTR = xPathUtils.getString(recordNode, "//ms:BWSTR");
//        var KM_ANF_D = xPathUtils.getString(recordNode, "//ms:KM_ANF_D");

        var addHtml = "" +
//            "<a href=\"http://wsvmapserv.wsv.bvbs.bund.de/ol_bwastr/index.html?bwastr=" + BWSTR + "&kmwert=" + KM_ANF_D + "&abstand=0&zoom=15\" target=\"_blank\" style=\"padding: 0 0 0 0;\">" +
            "<div style=\"background-image: url(http://wsvmapserv.ilmenau.baw.de/cgi-bin/wmstk?VERSION=1.1.1&amp;REQUEST=GetMap&amp;SRS=EPSG:4326&amp;BBOX=" + BBOX +
            "&amp;LAYERS=TK1000,TK500,TK200,TK100,TK50,TK25&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px; margin: 10px 0 0 0;\">" +
            "<div style=\"background-image: url(http://wsvmapserv.wsv.bund.de/ienc?VERSION=1.1.1&amp;REQUEST=GetMap&amp;SRS=EPSG:4326&amp;Transparent=True&amp;BBOX=" + BBOX +
            "&amp;Layers=Harbour&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px;\">" +
            "<img src=\"/ingrid-portal-apps/images/map_punkt.png\" alt=\"\">" +
            "</div></div>";
//            + "</a>";

        if (log.isDebugEnabled()) {
            log.debug("MapPreview Html: " + addHtml);
        }

        return addHtml;
    }
}
