/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
 * WInD WFS to Lucene Document mapping according to
 * mapping IGC 1.0.3 
 * Copyright (c) 2024 wemove digital solutions. All rights reserved.
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

log.debug("Mapping wfs record "+wfsRecord.getId()+" to lucene document");

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
for (var i=0, count=detailNodes.getLength(); i<count; i++) {
	var detailNode = detailNodes.item(i);
	var nodeName = detailNode.getLocalName();
	if (hasValue(nodeName)) {
		addToDoc(document, nodeName.toLowerCase(), detailNode.getTextContent(), true);
	}
}

function mapTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//wind_wms:WINDBENENNUNG");
	addToDoc(document, "title", title, true);
}

function mapSummary(recordNode) {
	var result = xPathUtils.getString(recordNode, "//wind_wms:WINDOART");
	result += " / " + xPathUtils.getString(recordNode, "//wind_wms:WINDTEILIDENTNR");
	result += " / " + xPathUtils.getString(recordNode, "//wind_wms:WINDWSA");
	result += " / " + xPathUtils.getString(recordNode, "//wind_wms:WINDAB");
	result += " / " + xPathUtils.getString(recordNode, "//wind_wms:WINDBUNDESLAND");
	result += " / " + xPathUtils.getString(recordNode, "//wind_wms:WINDBWASTRIDNR");
	var desc = xPathUtils.getString(recordNode, "//wind_wms:WINDBEMERKUNG");
	if (hasValue(desc) && desc != "null") {
		result += " / "+desc;
	}
	addToDoc(document, "summary", result, true);
}

function mapBoundingBox(recordNode) {
	var gmlPoint = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:Point");
	if (hasValue(gmlPoint)) {
		var point = xPathUtils.getString(gmlPoint, "gml:pos").split(" ");
log.debug("mapBoundingBox: gmlPoint = "+gmlPoint);
    // ??? Vice Versa ??? Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
		addNumericToDoc(document, "y1", point[1], false); // south
		addNumericToDoc(document, "x1", point[0], false); // west
		addNumericToDoc(document, "y2", point[1], false); // north
		addNumericToDoc(document, "x2", point[0], false); // east
	}
}

function mapPreview(recordNode) {
    var gmlPoint = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:Point");
    if (hasValue(gmlPoint)) {
    	  // BBOX
        var point = xPathUtils.getString(gmlPoint, "gml:pos").split(" ");
        var S = Number(point[1]); // SOUTH y
        var E = Number(point[0]); // EAST, x

        // we already have targetEPSG "25832", no transformation necessary
/*
        // NOTICE: 
        // lowerCorner and upperCorner have same coordinates in Wadaba !? -> BBOX is a POINT !
        var BBOX = "" + (E - 0.048) + "," + (S - 0.012) + "," + (E + 0.048) + "," + (S + 0.012);

        var sourceEPSG = "4326";
        var targetEPSG = "25832";
        var CoordTransformUtil = Java.type("de.ingrid.geo.utils.transformation.CoordTransformUtil");
        var transfCoords = CoordTransformUtil.getInstance().transform(
                E, S,
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode(sourceEPSG),
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode(targetEPSG));
        var targetE = transfCoords[0];
        var targetS = transfCoords[1];
*/
        var targetE = E;
        var targetS = S;

        var addHtml = "<iframe class=\"map-ingrid\" src=\"/ingrid-webmap-client/frontend/prd/embed.html?lang=de&zoom=15&topic=favoriten&bgLayer=wmts_topplus_web&layers=bwastr_vnetz&layers_opacity=0.4&E=" + targetE + "&N=" + targetS + "&crosshair=marker\" style=\"height:320px\"></iframe>";
log.debug("Mapping field \"additional_html_1\": " + addHtml);

        addToDoc(document, "additional_html_1", addHtml, false);
    }
}
