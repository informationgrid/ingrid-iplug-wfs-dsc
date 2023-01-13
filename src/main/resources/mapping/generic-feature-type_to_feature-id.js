/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
 * Generic WFS Feature Type to Id mapping
 * Copyright (c) 2020 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param featureTypeNode A Node instance, that defines the input extracted from the WFS GetCapabilities response
 * @param featureTypeDescNode A Node instance, that defines the input as received from the WFS DescribeFeatureType operation
 * @param xPathUtils A de.ingrid.utils.xpath.XPathUtils instance
 * @param log A Log instance
 * @return String
 */

log.debug("Extracting wfs feature type id");

// get the wfs:FeatureType/wfs:Name text value of the root element
// last evaluated expression is the return value
var nameNode = xPathUtils.getNode(featureTypeNode, "/wfs:FeatureType/wfs:Name");
if (nameNode == null) {
	log.error("Name node 'wfs:Name' not set in feature type " + featureTypeNode);
}
nameNode.getTextContent();
