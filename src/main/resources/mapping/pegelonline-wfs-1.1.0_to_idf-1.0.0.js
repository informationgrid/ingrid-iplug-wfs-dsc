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
 *            A de.ingrid.utils.xpath.XPathUtils instance
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

if (log.isDebugEnabled()) {
    log.debug("Mapping wfs record "+wfsRecord.getId()+" to idf document");
}

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
    var dataMap = "<div class=\"xsmall-24 small-24 medium-10 columns\">";
    dataMap += "<h4 class=\"text-center\">Vorschau</h4>";
    dataMap += "<div class=\"swiper-container-background\"><div class=\"swiper-slide\"><div class=\"caption\"><div class=\"preview_image\">";
    dataMap += getMapPreview(recordNode);
    dataMap += "</div></div></div></div></div>";
    detailNavContentData.appendChild(document.createTextNode(dataMap));
}

if(getSummary(recordNode)) {
    var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
    addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_description"]);
    addOutput(detailNavContentSection, "h3", "Beschreibung");
    var result = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["row columns"]);
    result = addOutput(result, "p", getSummary(recordNode));
}

var detailNodes = recordNode.getChildNodes();
if(detailNodes.length > 0) {
    var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
    addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_details"]);
    addOutput(detailNavContentSection, "h3", "Details");
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Gewässer", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:water"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Station", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:station"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Station ID", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:station_id"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Kilometer", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:kilometer"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Datum", DateUtil.formatDate(xPathUtils.getString(recordNode, "/gk:waterlevels/gk:date")));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Wert", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:value"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Einheit", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:unit"));
    addDetailTableLinkRowWrapperNewLayout(detailNavContentSection, "Chart", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:chart_url") , xPathUtils.getString(recordNode, "/gk:waterlevels/gk:chart_url"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Trend", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:trend"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Status", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:status"));
    addDetailTableRowWrapperNewLayout(detailNavContentSection, "Kommentar", xPathUtils.getString(recordNode, "/gk:waterlevels/gk:comment"));
}

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
    return DateUtil.formatDate(part1)+": "+part2+""+part3;
}

function getBoundingBox(recordNode) {
    var gmlEnvelope = xPathUtils.getNode(recordNode, "/gk:waterlevels/gml:boundedBy/gml:Envelope");
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
    var addHtml = '';
    var gmlEnvelope = xPathUtils.getNode(recordNode, "/gk:waterlevels/gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        // Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
        var y1 = Number(lowerCoords[0]);
        var x1 = Number(lowerCoords[1]);
        var y2 = Number(upperCoords[0]);
        var x2 = Number(upperCoords[1]);

        var marker = '';

        if (x1 === x2 && y1 === y2) {
            marker = 'L.marker(['+ y1 +', ' + x1 +'])';
        }
        var BBOX = '[' + y1 + ',' + x1 + '],[' + y2 + ',' + x2 + ']';

        var height = 280;

        addHtml += '' +
        ' <div id="map" style="height: '+ height + 'px;"></div>' +
        ' <script>' + 
        'var map = addLeafletMapWithId(\'map\', getOSMLayer(\'\'), [ ' + BBOX + ' ], null , 10);';

        if(marker !== '') {
            addHtml = addHtml +  marker + 
                '.bindTooltip("'+ getTitle(recordNode) + '", {direction: "center"})' +
                '.addTo(map);'
        } else {
            addHtml = addHtml + 'map.addLayer(L.rectangle([ ' + BBOX + ' ], {color: "#156570", weight: 1})' +
                '.bindTooltip("'+ getTitle(recordNode) + '", {direction: "center"}));';
        }
        addHtml = addHtml + 'map.gestureHandling.enable();' +
            'addLeafletHomeControl(map, \'Zoom auf initialen Kartenausschnitt\', \'topleft\', \'ic-ic-center\', [ ' + BBOX + ' ], \'\', \'23px\');' 
        addHtml = addHtml + '</script>';

        if (log.isDebugEnabled()) {
            log.debug("MapPreview Html: " + addHtml);
        }
    }
    return addHtml;
}