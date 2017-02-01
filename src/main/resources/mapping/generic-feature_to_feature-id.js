/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
 * Generic WFS Feature to Id mapping
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param featureNode A Node instance, that defines the input
 * @param xPathUtils A de.ingrid.utils.xpath.XPathUtils instance
 * @param log A Log instance
 * @return String
 */
if (javaVersion.indexOf( "1.8" ) === 0) {
    load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.w3c.dom);

if (log.isDebugEnabled()) {
	log.debug("Extracting wfs feature id");
}

// get the gml:id attribute value of the root element
// last evaluated expression is the return value
var rootNode = xPathUtils.getNode(featureNode, "/*");
var idAttr = rootNode.getAttributes().getNamedItem("gml:id");
if (idAttr == null) {
	log.error("Id Attribute 'gml:id' not set in feature " + rootNode);
}
idAttr.getNodeValue();
