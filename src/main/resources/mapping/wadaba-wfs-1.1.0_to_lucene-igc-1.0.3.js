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
		addNumericToDoc(document, "x1", lowerCoords[0], false); // west
		addNumericToDoc(document, "x2", upperCoords[0], false); // east
		addNumericToDoc(document, "y1", lowerCoords[1], false); // south
		addNumericToDoc(document, "y2", lowerCoords[1], false); // north
	}
}
