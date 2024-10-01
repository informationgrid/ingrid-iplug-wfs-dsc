/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * WInD WFS to IDF Document mapping
 * Copyright (c) 2024 wemove digital solutions. All rights reserved.
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

log.debug("Mapping wfs record "+wfsRecord.getId()+" to idf document");

// get the xml content of the record
var recordNode = wfsRecord.getOriginalResponse().get(0);

var plugDescrDataSourceName = "";
var plugDescrOrganisation = "";

if(wfsRecord.getFactory() && wfsRecord.getFactory().getPlugDescription()) {
    plugDescrDataSourceName = wfsRecord.getFactory().getPlugDescription().getDataSourceName();
    plugDescrOrganisation = wfsRecord.getFactory().getPlugDescription().getOrganisation();
}

//---------- <idf:body> ----------
var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

var detail = addOutputWithAttributes(idfBody, "section", ["class", "id"], ["detail", "detail"]);

// header
var header = addDetailHeaderWrapperNewLayout(detail);

// header back to search
addDetailHeaderWrapperNewLayoutBackSearch(header);

// header title
addDetailHeaderWrapperNewLayoutTitle(header, getTitle(recordNode));

// detail content

var detailNavContent = addOutputWithAttributes(detail, "section", ["class"], ["row nav-content search-filtered"]);

// navigation
addDetailHeaderWrapperNewLayoutDetailNavigation(detailNavContent, getSummary(recordNode), recordNode.getChildNodes(), undefined, plugDescrDataSourceName, plugDescrOrganisation)

// content
addOutputWithAttributes(detailNavContent, "a", ["class", "id"], ["anchor", "detail_overview"]);

detailNavContent = addOutputWithAttributes(detailNavContent, "div", ["class"], ["xsmall-24 large-18 xlarge-18 columns"]);

var detailNavContentData = addOutputWithAttributes(detailNavContent, "div", ["class"], ["data"]);
detailNavContentData = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["teaser-data search row is-active"]);

var detailNavContentDataLeft = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["xsmall-24 small-24 medium-14 large-14 xlarge-14 columns"]);
//add the bounding box
var boundingBox = getBoundingBox(recordNode);
if(boundingBox)  {
    addOutput(detailNavContentDataLeft, "h4", "Ort:");
    addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Nord", boundingBox.y2);
    addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "West", boundingBox.x1);
    addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Ost", boundingBox.x2);
    addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "S&uuml;d", boundingBox.y1);
}

if(getMapPreview(recordNode)) {
    detailNavContentData.appendChild(document.createTextNode(getMapPreview(recordNode)));
}

if(getSummary(recordNode)) {
    var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
    addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_description"]);
    addOutput(detailNavContentSection, "h3", "Beschreibung");
    var result = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["row columns"]);
    result = addOutput(result, "p", getSummary(recordNode));
}

var detailNodes = recordNode.getChildNodes();
if(detailNodes.getLength() > 0) {
    var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
    addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_details"]);
    addOutput(detailNavContentSection, "h3", "Details");
    for (var i=0, count=detailNodes.getLength(); i<count; i++) {
        var detailNode = detailNodes.item(i);
        var nodeName = detailNode.getLocalName();
        if (hasValue(nodeName)) {
            addDetailTableRowWrapperNewLayout(detailNavContentSection, detailNode.getLocalName(), detailNode.getTextContent());
        }
    }
}

if(hasValue(plugDescrDataSourceName) || hasValue(plugDescrOrganisation)) {
    var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
    addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "metadata_info"]);
    addOutput(detailNavContentSection, "h3", "Informationen zum Metadatensatz");
    var result = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["table table--lined"]);
    result = addOutput(result, "table", "");
    result = addOutput(result, "tbody", "");
    result = addOutput(result, "tr", "");
    addOutput(result, "th", "Metadatenquelle");
    result = addOutput(result, "td", "");
    if(hasValue(plugDescrDataSourceName)) {
        addOutput(result, "p", plugDescrDataSourceName);
        addOutput(result, "span", "&nbsp;");
    }
    if(hasValue(plugDescrOrganisation)) {
        addOutput(result, "p", plugDescrOrganisation);
    }
}

// functions
function getTitle(recordNode) {
	var title = xPathUtils.getString(recordNode, "//wind_wms:WINDBENENNUNG");
	return title;
}

function getSummary(recordNode) {
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
	return result;
}

function getBoundingBox(recordNode) {
	var myX1, myY1, myX2, myY2;

	var gmlPoint = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:Point");
	if (hasValue(gmlPoint)) {
		var point = xPathUtils.getString(gmlPoint, "gml:pos").split(" ");
log.debug("getBoundingBox: gmlPoint = "+point);
		myX1 = myX2 = point[0];
		myY1 = myY2 = point[1];

	} else {
		// no Point, Line ?
		var gmlLine = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:LineString");
		if (hasValue(gmlLine)) {
			var line = xPathUtils.getString(gmlLine, "gml:posList").split(" ");
log.debug("getBoundingBox: gmlLine = "+line);
			var x;
			for (let i = 0; i < line.length; i=i+2) {
				x = Number(line[i]);
				if (!hasValue(myX1) || x < myX1) {
					myX1 = x;
				}
				if (!hasValue(myX2) || x > myX2) {
					myX2 = x;
				}
			} 
			var y;
			for (let i = 1; i < line.length; i=i+2) {
				y = Number(line[i]);
				if (!hasValue(myY1) || y < myY1) {
					myY1 = y;
				}
				if (!hasValue(myY2) || y > myY2) {
					myY2 = y;
				}
			}
		} 
	}

log.debug("getBoundingBox: x1="+myX1+"/x2="+myX2+" // y1="+myY1+"/y2="+myY2);

	if (hasValue(myX1)) {
		// we have to convert to String again, otherwise we get errors in Java when converting big Double to String for Output
		myX1 = "" + myX1;
		myX2 = "" + myX2;
		myY1 = "" + myY1;
		myY2 = "" + myY2;

		return {
			// ??? Vice Versa ??? Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
			y1: myY1, // south
			x1: myX1, // west
			y2: myY2, // north
			x2: myX2  // east
		}
	}
}

function getMapPreview(recordNode) {
	var coordinates

	// Point ?
	var gmlGeom = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:Point");
	if (hasValue(gmlGeom)) {
		coordinates = xPathUtils.getString(gmlGeom, "gml:pos").split(" ");
	} else {
		// no Point, Line ?
		gmlGeom = xPathUtils.getNode(recordNode, "//wind_wms:GEOM/gml:LineString");
		if (hasValue(gmlGeom)) {
			coordinates = xPathUtils.getString(gmlGeom, "gml:posList").split(" ");
		}
	}

log.debug("mapPreview: coordinates="+coordinates);

	if (hasValue(coordinates)) {
		var E = Number(coordinates[0]); // EAST, x
		var S = Number(coordinates[1]); // SOUTH y

        // we already have targetEPSG "25832", no transformation necessary

        var targetE = E;
        var targetS = S;

        var addHtml = "<div class=\"xsmall-24 small-24 medium-10 columns\">";
        addHtml += "<h4 class=\"text-center\">Vorschau</h4>";
        addHtml += "<div class=\"swiper-container-background\"><div class=\"swiper-slide\"><div class=\"caption\"><div class=\"preview_image\">";
        addHtml += "<iframe src=\"/ingrid-webmap-client/frontend/prd/embed.html?lang=de&zoom=15&topic=favoriten&bgLayer=wmts_topplus_web&layers=bwastr_vnetz&layers_opacity=0.4&E=" + targetE + "&N=" + targetS + "&crosshair=marker\" style=\"height:320px\"></iframe>";
        addHtml += "</div></div></div></div></div>";
log.debug("MapPreview Html: " + addHtml);
        return addHtml;
    }
}
