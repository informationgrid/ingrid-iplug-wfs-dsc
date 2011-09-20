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
var title = document.createElement("h1")
title.appendChild(document.createTextNode(getTitle(recordNode)));
idfBody.appendChild(title);

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
	return part1+": "+part2+""+part3;
}
