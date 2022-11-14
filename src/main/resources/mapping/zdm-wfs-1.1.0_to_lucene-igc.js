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
/**
 * ZDM WFS to Lucene Document mapping
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 * 
 * The following global variable are passed from the application:
 * 
 * @param wfsRecord
 *			A WFSFeature/WFSFeatureType instance, that defines the input
 * @param document
 *			A lucene Document instance, that defines the output
 * @param xPathUtils
 * 			  A de.ingrid.utils.xpath.XPathUtils instance
 * @param log
 *			A Log instance
 */

let WFSFeature = Java.type("de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature");
let WFSFeatureType = Java.type("de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType");

log.debug("Mapping wfs record "+wfsRecord.getId()+" of type "+wfsRecord.getClass().getName()+" to lucene document");

//
// WFSFeature mapping
//
if (wfsRecord instanceof WFSFeature) {
	// get the xml content of the record
	var recordNode = wfsRecord.getOriginalResponse().get(0);

	// add id field
	addToDoc(document, "t01_object.obj_id", wfsRecord.getId(), true);

	//add the title
	mapFeatureTitle(recordNode);

	//add the summary
	mapFeatureSummary(recordNode);

	//add the bounding box
	mapFeatureBoundingBox(recordNode, "//gml:boundedBy/gml:Envelope", "gml:lowerCorner", "gml:upperCorner");

	//add the map preview
	mapFeaturePreview(recordNode);

	// add details (content of all child nodes)
	var detailNodes = recordNode.getChildNodes();
	for (var i=0, count=detailNodes.length; i<count; i++) {
		var detailNode = detailNodes.item(i);
		var nodeName = detailNode.getLocalName();
		if (hasValue(nodeName)) {
			addToDoc(document, nodeName.toLowerCase(), detailNode.getTextContent(), true);
		}
	}
}
//
// WFSFeatureType mapping
//
else if (wfsRecord instanceof WFSFeatureType) {
	// get the xml content of the record
	var recordNode = wfsRecord.getOriginalResponse().get(0);
	var detailNode = wfsRecord.getOriginalResponse().get(1);

	// add id field
	addToDoc(document, "t01_object.obj_id", wfsRecord.getId(), true);

	//add the title
	mapFeatureTypeTitle(recordNode);

	//add the summary
	mapFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures());

    //add the bounding box
    mapFeatureBoundingBox(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox", "ows:LowerCorner", "ows:UpperCorner");

	// add number of features
	addToDoc(document, "number_of_features", wfsRecord.getNumberOfFeatures(), true);
}

//
// WFSFeature functions
//

function mapFeatureTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "/*/@gml:id");
	addToDoc(document, "title", title, true);
}

function mapFeatureSummary(recordNode) {
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
	addToDoc(document, "summary", result, true);
}

function mapFeatureBoundingBox(recordNode, xpathBoundingBox, xpathLowerCorner, xpathUpperCorner) {
  var gmlEnvelope = xPathUtils.getNode(recordNode, xpathBoundingBox);
  if (hasValue(gmlEnvelope)) {
      var lowerCoords = xPathUtils.getString(gmlEnvelope, xpathLowerCorner).split(" ");
      var upperCoords = xPathUtils.getString(gmlEnvelope, xpathUpperCorner).split(" ");
      // Latitude first (Breitengrad = y), longitude second (L�ngengrad = x)
      addNumericToDoc(document, "y1", lowerCoords[1], false); // south
      addNumericToDoc(document, "x1", lowerCoords[0], false); // west
      addNumericToDoc(document, "y2", upperCoords[1], false); // north
      addNumericToDoc(document, "x2", upperCoords[0], false); // east
  }
}

function mapFeaturePreview(recordNode) {
	var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
	if (hasValue(gmlEnvelope)) {
		// BBOX
		var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
		var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
		// Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
		var S = Number(lowerCoords[1]); // SOUTH y1
		var E = Number(upperCoords[0]); // EAST, x2
		// NOTICE: 

		// transform "WGS 84 (EPSG:4326)" to "ETRS89 / UTM zone 32N (EPSG:25832)"
		var transfCoords = CoordTransformUtil.getInstance().transform(
				E, S,
				CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"),
				CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"));
		var E_4326 = transfCoords[0];
		var S_4326 = transfCoords[1];

		//  Fields for link
//		var BWSTR = xPathUtils.getString(recordNode, "//ms:BWSTR");
//		var KM_ANF_D = xPathUtils.getString(recordNode, "//ms:KM_ANF_D");

		// lowerCorner and upperCorner have same coordinates !? -> BBOX is a POINT !
		var BBOX = "" + (E_4326 - 0.048) + "," + (S_4326 - 0.012) + "," + (E_4326 + 0.048) + "," + (S_4326 + 0.012);

		var addHtml = "" + 
//			"<a href=\"http://wsvmapserv.wsv.bvbs.bund.de/ol_bwastr/index.html?bwastr=" + BWSTR + "&kmwert=" + KM_ANF_D + "&abstand=0&zoom=15\" target=\"_blank\" style=\"padding: 0 0 0 0;\">" +
			"<div style=\"background-image: url(https://sgx.geodatenzentrum.de/wms_topplus_open?VERSION=1.3.0&amp;REQUEST=GetMap&amp;CRS=CRS:84&amp;BBOX=" + BBOX +
			"&amp;LAYERS=web&amp;FORMAT=image/png&amp;STYLES=&amp;WIDTH=480&amp;HEIGHT=120); left: 0px; top: 0px; width: 480px; height: 120px; margin: 10px 0 0 0;\">" +
			"</div>";
//			+ "</a>";

		log.debug("Mapping field \"additional_html_1\": " + addHtml);

		addToDoc(document, "additional_html_1", addHtml, false);
	}
}

//
// WFSFeatureType functions
//

function mapFeatureTypeTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Title");
	addToDoc(document, "title", title, true);
}

function mapFeatureTypeSummary(recordNode, numFeatures) {
	var summary = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Abstract");
	var featureSummary = numFeatures+" Feature(s)";
	addToDoc(document, "summary", (hasValue(summary) ? summary+" - " : "") + featureSummary, true);
}

function mapFeatureTypeBoundingBox(recordNode) {
  var gmlEnvelope = xPathUtils.getNode(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox");
  if (hasValue(gmlEnvelope)) {
      var lowerCoords = xPathUtils.getString(gmlEnvelope, "ows:LowerCorner").split(" ");
      var upperCoords = xPathUtils.getString(gmlEnvelope, "ows:UpperCorner").split(" ");
      // Latitude first (Breitengrad = y), longitude second (L�ngengrad = x)
      addNumericToDoc(document, "y1", lowerCoords[1], false); // south
      addNumericToDoc(document, "x1", lowerCoords[0], false); // west
      addNumericToDoc(document, "y2", upperCoords[1], false); // north
      addNumericToDoc(document, "x2", upperCoords[0], false); // east
  }
}
