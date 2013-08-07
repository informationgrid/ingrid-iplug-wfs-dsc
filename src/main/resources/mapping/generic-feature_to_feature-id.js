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
