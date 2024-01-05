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
 * Generic WFS Feature to Id mapping
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param featureNode A Node instance, that defines the input as received from the WFS GetFeature operation
 * @param xPathUtils A de.ingrid.utils.xpath.XPathUtils instance
 * @param log A Log instance
 * @return String
 */

log.debug("Extracting wfs feature id");

// get the gml:id attribute value of the root element
// last evaluated expression is the return value
var rootNode = xPathUtils.getNode(featureNode, "/*");
var idAttr = rootNode.getAttributes().getNamedItem("gml:id");
if (idAttr == null) {
	log.error("Id Attribute 'gml:id' not set in feature " + rootNode);
}
idAttr.getNodeValue();
