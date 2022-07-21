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
 * ZDM WFS to IDF Document mapping
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 * 
 * The following global variable are passed from the application:
 * 
 * @param wfsRecord
 *            A WFSFeature/WFSFeatureType instance, that defines the input
 * @param document
 *            A IDF Document (XML-DOM) instance, that defines the output
 * @param xPathUtils
 *               A de.ingrid.utils.xpath.XPathUtils instance
 * @param log
 *            A Log instance
 */
if (javaVersion.indexOf( "1.8" ) === 0) {
    load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.tools);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.index);
importPackage(Packages.de.ingrid.iplug.wfs.dsc.wfsclient);
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.geo.utils.transformation);
importPackage(Packages.de.ingrid.geo.utils.boundingbox);

if (log.isDebugEnabled()) {
    log.debug("Mapping wfs record "+wfsRecord.getId()+" of type "+wfsRecord.getClass().getName()+" to idf document");
}

var plugDescrDataSourceName = "";
var plugDescrOrganisation = "";

if(wfsRecord.getFactory() && wfsRecord.getFactory().getPlugDescription()) {
    plugDescrDataSourceName = wfsRecord.getFactory().getPlugDescription().getDataSourceName();
    plugDescrOrganisation = wfsRecord.getFactory().getPlugDescription().getOrganisation();
}

//
//WFSFeature mapping
//
if (wfsRecord instanceof WFSFeature) {
    // get the xml content of the record
    var recordNode = wfsRecord.getOriginalResponse().get(0);

    //---------- <idf:body> ----------
    var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

    var detail = addOutputWithAttributes(idfBody, "section", ["class", "id"], ["detail", "detail"]);

    // header
    var header = addDetailHeaderWrapperNewLayout(detail);

    // header back to search
    addDetailHeaderWrapperNewLayoutBackSearch(header);

    // header title
    addDetailHeaderWrapperNewLayoutTitle(header, getFeatureTitle(recordNode));

    // detail content

    var detailNavContent = addOutputWithAttributes(detail, "section", ["class"], ["row nav-content search-filtered"]);

    // navigation
    addDetailHeaderWrapperNewLayoutDetailNavigation(detailNavContent, getFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures()), recordNode.getChildNodes(), undefined, plugDescrDataSourceName, plugDescrOrganisation);

    // content
    addOutputWithAttributes(detailNavContent, "a", ["class", "id"], ["anchor", "detail_overview"]);

    detailNavContent = addOutputWithAttributes(detailNavContent, "div", ["class"], ["xsmall-24 large-18 xlarge-18 columns"]);

    var detailNavContentData = addOutputWithAttributes(detailNavContent, "div", ["class"], ["data"]);
    detailNavContentData = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["teaser-data search row is-active"]);

    var detailNavContentDataLeft = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["xsmall-24 small-24 medium-14 large-14 xlarge-14 columns"]);
    //add the bounding box
    var boundingBox = getFeatureBoundingBox(recordNode);
    if(boundingBox)  {
        addOutput(detailNavContentDataLeft, "h4", "Ort:");
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Nord", boundingBox.y2);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "West", boundingBox.x1);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Ost", boundingBox.x2);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "S&uuml;d", boundingBox.y1);
    }

    if(getFeatureMapPreview(recordNode, xPathUtils.getString(recordNode, "//ms:OBJEKT_ID"))) {
        var dataMap = "<div class=\"xsmall-24 small-24 medium-10 columns\">";
        dataMap += "<h4 class=\"text-center\">Vorschau</h4>";
        dataMap += "<div class=\"swiper-container-background\"><div class=\"swiper-slide\"><div class=\"caption\"><div class=\"preview_image\">";
        dataMap += getFeatureMapPreview(recordNode, xPathUtils.getString(recordNode, "//ms:OBJEKT_ID"));
        dataMap += "</div></div></div></div></div>";
        detailNavContentData.appendChild(document.createTextNode(dataMap));
    }

    if(getFeatureSummary(recordNode)) {
        var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
        addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_description"]);
        addOutput(detailNavContentSection, "h3", "Beschreibung");
        var result = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["row columns"]);
        addOutput(result, "p", getFeatureSummary(recordNode));
    }

    var detailNodes = recordNode.getChildNodes();
    if(detailNodes.length > 0) {
        var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
        addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_details"]);
        addOutput(detailNavContentSection, "h3", "Details");
        addDetailTableListWrapperNewLayout(detailNavContentSection, "Feature Attribute", detailNodes);
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
}
//
// WFSFeatureType mapping
//
else if (wfsRecord instanceof WFSFeatureType) {
    // get the xml content of the record
    var recordNode = wfsRecord.getOriginalResponse().get(0);
    var detailNode = wfsRecord.getOriginalResponse().get(1);

    //---------- <idf:body> ----------
    var idfBody = xPathUtils.getNode(document, "/idf:html/idf:body");

    var detail = addOutputWithAttributes(idfBody, "section", ["class", "id"], ["detail", "detail"]);

    // header
    var header = addDetailHeaderWrapperNewLayout(detail);

    // header back to search
    addDetailHeaderWrapperNewLayoutBackSearch(header);

    // header title
    addDetailHeaderWrapperNewLayoutTitle(header, getFeatureTypeTitle(recordNode));

    // detail content

    var detailNavContent = addOutputWithAttributes(detail, "section", ["class"], ["row nav-content search-filtered"]);

    // navigation
    addDetailHeaderWrapperNewLayoutDetailNavigation(detailNavContent, getFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures()), recordNode.getChildNodes(), wfsRecord.getFeatures(), plugDescrDataSourceName, plugDescrOrganisation);

    // content
    addOutputWithAttributes(detailNavContent, "a", ["class", "id"], ["anchor", "detail_overview"]);

    detailNavContent = addOutputWithAttributes(detailNavContent, "div", ["class"], ["xsmall-24 large-18 xlarge-18 columns"]);

    var detailNavContentData = addOutputWithAttributes(detailNavContent, "div", ["class"], ["data"]);
    detailNavContentData = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["teaser-data search row is-active"]);

    var detailNavContentDataLeft = addOutputWithAttributes(detailNavContentData, "div", ["class"], ["xsmall-24 small-24 medium-14 large-14 xlarge-14 columns"]);
    //add the bounding box
    var boundingBox = getFeatureTypeBoundingBox(recordNode);
    if(boundingBox) {
        addOutput(detailNavContentDataLeft, "h4", "Ort:");
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Nord", boundingBox.y2);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "West", boundingBox.x1);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "Ost", boundingBox.x2);
        addDetailTableRowWrapperNewLayout(detailNavContentDataLeft, "S&uuml;d", boundingBox.y1);
    }

    if(getFeatureTypeMapPreview(recordNode, wfsRecord.getName())) {
        var dataMap = "<div class=\"xsmall-24 small-24 medium-10 columns\">";
        dataMap += "<h4 class=\"text-center\">Vorschau</h4>";
        dataMap += "<div class=\"swiper-container-background\"><div class=\"swiper-slide\"><div class=\"caption\"><div class=\"preview_image\">";
        dataMap += getFeatureTypeMapPreview(recordNode, wfsRecord.getName());
        dataMap += "</div></div></div></div></div>";
        detailNavContentData.appendChild(document.createTextNode(dataMap));
    }
    if(getFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures())) {
        var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
        addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_description"]);
        addOutput(detailNavContentSection, "h3", "Beschreibung");
        var result = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["row columns"]);
        addOutput(result, "p", getFeatureTypeSummary(recordNode, wfsRecord.getNumberOfFeatures()));
    }

    var detailNodes = xPathUtils.getNodeList(detailNode, "//*/*[local-name()='extension'][@base='gml:AbstractFeatureType']/*[local-name()='sequence']/*[local-name()='element']");
    if(detailNodes.length > 0) {
        var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
        addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_details"]);
        addOutput(detailNavContentSection, "h3", "Details");
        addDetailTableListWrapperNewLayout(detailNavContentSection, "Feature Attribute", detailNodes);
    }

    // show features, if loaded
    var features = wfsRecord.getFeatures();
    if (hasValue(features)) {
        var detailNavContentSection = addOutputWithAttributes(detailNavContent, "div", ["class"], ["section"]);
        addOutputWithAttributes(detailNavContentSection, "a", ["class", "id"], ["anchor", "detail_features"]);
        addOutput(detailNavContentSection, "h3", "Features");
        var resultColumn = addOutputWithAttributes(detailNavContentSection, "div", ["class"], ["row columns"]);
        for (var j=0; j<features.size(); j++) {
            var recordNode = features.get(j).getOriginalResponse().get(0);
            var result = addOutputWithAttributes(resultColumn, "div", ["class"], ["sub-section"]);

            // add the title
            addOutput(result, "h3", getFeatureTitle(recordNode));

            //add the summary
            addOutput(result, "p", getFeatureSummary(recordNode));

            //add the bounding box
            var boundingBox = getFeatureBoundingBox(recordNode);
            result = addOutputWithAttributes(resultColumn, "div", ["class"], ["sub-section"]);
            addOutput(result, "h4", "Ort:");
            addDetailTableRowWrapperNewLayout(result, "Nord", boundingBox.y2);
            addDetailTableRowWrapperNewLayout(result, "West", boundingBox.x1);
            addDetailTableRowWrapperNewLayout(result, "Ost", boundingBox.x2);
            addDetailTableRowWrapperNewLayout(result, "S&uuml;d", boundingBox.y1);

            // add the map preview
            result = addOutputWithAttributes(resultColumn, "div", ["class"], ["sub-section"]);
            addOutput(result, "div", getFeatureMapPreview(recordNode, wfsRecord.getName() + "_" + j));

            // add details (content of all child nodes)
            var detailNodes = recordNode.getChildNodes();
            if (hasValue(detailNodes)) {
                result = addOutputWithAttributes(resultColumn, "div", ["class"], ["sub-section"]);
                for (var i=0, count=detailNodes.length; i<count; i++) {
                    var detailNode = detailNodes.item(i);
                    var nodeName = detailNode.getLocalName();
                    if (hasValue(nodeName)) {
                        addDetailTableRowWrapperNewLayout(result, nodeName, detailNode.getTextContent());
                    }
                }
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
}

//
// WFSFeature functions
//

function addDetailTableListWrapperNewLayout (parent, title, contentList) {
    if(contentList && contentList.length > 0) {
        var result = addOutputWithAttributes(parent, "div", ["class"], ["table list"]);
        result = addOutput(result, "table", "");
        result = addOutput(result, "tbody", "");
        result = addOutput(result, "tr", "");
        addOutput(result, "th", title);
        result = addOutput(result, "td", "");
        for (var i=0, count=contentList.length; i<count; i++) {
            var content = contentList.item(i);
            var contentName = content.getAttributeNode("name");
            if (hasValue(contentName)) {
                var contentEntry = addOutputWithAttributes(result, "span", ["class"], ["list_entry"]);
                contentEntry.appendChild(document.createTextNode(contentName.getTextContent()))
            }
        }
    }
}

function getFeatureTitle(recordNode) {
    var title = xPathUtils.getString(recordNode, "/*/@gml:id");
    return title;
}

function getFeatureSummary(recordNode) {
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
    return result;
}

function getFeatureBoundingBox(recordNode) {
    var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");
        return getBoundingBox(lowerCoords, upperCoords);
    }
}

function getFeatureMapPreview(recordNode, name) {
    var gmlEnvelope = xPathUtils.getNode(recordNode, "//gml:boundedBy/gml:Envelope");
    if (hasValue(gmlEnvelope)) {
        // BBOX
        var lowerCoords = xPathUtils.getString(gmlEnvelope, "gml:lowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(gmlEnvelope, "gml:upperCorner").split(" ");

        //  Fields for link
//        var BWSTR = xPathUtils.getString(recordNode, "//ms:BWSTR");
//        var KM_ANF_D = xPathUtils.getString(recordNode, "//ms:KM_ANF_D");
//        var linkUrl = "http://wsvmapserv.wsv.bvbs.bund.de/ol_bwastr/index.html?bwastr=" + BWSTR + "&kmwert=" + KM_ANF_D + "&abstand=0&zoom=15";

        return getMapPreview(name, getFeatureTitle(recordNode), lowerCoords, upperCoords, false/*, linkUrl*/);
    }
}

//
// WFSFeatureType functions
//

function getFeatureTypeTitle(recordNode) {
    var title = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Title");
    return title;
}

function getFeatureTypeSummary(recordNode, numFeatures) {
    var summary = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Abstract");
    var name = xPathUtils.getString(recordNode, "//wfs:FeatureType/wfs:Name");
    var portal = "Küstendaten";
    var featureSummary = "WebFeatureService (WFS) " + portal + ", FeatureType: " + name + "<br>";
    featureSummary += "Dieser FeatureType umfasst <b>" + numFeatures + "</b> Feature(s).<br>";
    if(hasValue(summary)) {
        featureSummary += summary + "<br>";
    }
    return featureSummary;
}

function getFeatureTypeBoundingBox(recordNode) {
    var owsBoundingBox = xPathUtils.getNode(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox");
    if (hasValue(owsBoundingBox)) {
        var lowerCoords = xPathUtils.getString(owsBoundingBox, "ows:LowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(owsBoundingBox, "ows:UpperCorner").split(" ");

        return getBoundingBox(lowerCoords, upperCoords);
    }
}

function getFeatureTypeMapPreview(recordNode, name) {
    var owsBoundingBox = xPathUtils.getNode(recordNode, "//wfs:FeatureType/ows:WGS84BoundingBox");
    if (hasValue(owsBoundingBox)) {
        // BBOX
        var lowerCoords = xPathUtils.getString(owsBoundingBox, "ows:LowerCorner").split(" ");
        var upperCoords = xPathUtils.getString(owsBoundingBox, "ows:UpperCorner").split(" ");

        return getMapPreview(name, getFeatureTypeTitle(recordNode), lowerCoords, upperCoords, true);
    }
}

//
// Common functions
//

function getBoundingBox(lowerCoords, upperCoords) {
    return {
        // Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
        y1: lowerCoords[1], // south
        x1: lowerCoords[0], // west
        y2: upperCoords[1], // north
        x2: upperCoords[0]  // east
    }
}

function getMapPreview(name, title, lowerCoords, upperCoords, isWGS84, linkUrl) {

    var srsName = "EPSG:25832";
    // Latitude first (Breitengrad = y), longitude second (Laengengrad = x)
    var y1 = Number(lowerCoords[0]);
    var x1 = Number(lowerCoords[1]);
    var y2 = Number(upperCoords[0]);
    var x2 = Number(upperCoords[1]);

    var marker = "";

    if (x1 === x2 && y1 === y2) {
        if (!isWGS84) {
            var transfCoords = CoordTransformUtil.getInstance().transform(
                y1, x1,
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"),
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"));

            y1 = transfCoords[0];
            x1 = transfCoords[1];
            y2 = transfCoords[0];
            x2 = transfCoords[1];

        }
        marker = 'L.marker(['+ transfCoords[1] +', ' + transfCoords[0] +'],' +
            '{ icon: L.icon({ iconUrl: "/DE/dienste/ingrid-webmap-client/frontend/prd/img/marker.png" }) })';
        y1 = y1 - 0.048;
        x1 = x1 - 0.012;
        y2 = y2 + 0.048;
        x2 = x2 + 0.012;
    } else {
        if (!isWGS84) {
            var transfCoords = CoordTransformUtil.getInstance().transform(
                y1, x1,
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"),
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"));
            y1 = transfCoords[0];
            x1 = transfCoords[1];

            transfCoords = CoordTransformUtil.getInstance().transform(
                y2, x2,
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("25832"),
                CoordTransformUtil.getInstance().getCoordTypeByEPSGCode("4326"));
            y2 = transfCoords[0];
            x2 = transfCoords[1];
        }
    }
    var BBOX = "[" + x1 + "," + y1 + "],[" + x2 + "," + y2 + "]";

    var height = 280;
    if(!isWGS84) {
        // Height of feature images
        height = 160;
    }

    var addHtml = '' +
    ' <div id="map_' + name + '" style="height: '+ height + 'px;"></div>' +
    ' <script>' + 
    'var map_' + name + ' = addLeafletMapWithId(\'map_' + name + '\', getOSMLayer(\'\'), [ ' + BBOX + ' ], null , 10);';

    if(marker !== '') {
        addHtml = addHtml +  marker + 
            '.bindTooltip("'+ title + '", {direction: "center"})' +
            '.addTo(map_' + name + ' );'
    } else {
        addHtml = addHtml + 'map_' + name + '.addLayer(L.rectangle([ ' + BBOX + ' ], {color: "#156570", weight: 1})' +
            '.bindTooltip("'+ title + '", {direction: "center"}));';
    }
    addHtml = addHtml + 'map_' + name + '.gestureHandling.enable();' +
        'addLeafletHomeControl(map_' + name + ', \'Zoom auf initialen Kartenausschnitt\', \'topleft\', \'ic-ic-center\', [ ' + BBOX + ' ], \'\', \'23px\');' 
    addHtml = addHtml + '</script>';

    if (log.isDebugEnabled()) {
        log.debug("MapPreview Html: " + addHtml);
    }

    return addHtml;
}
