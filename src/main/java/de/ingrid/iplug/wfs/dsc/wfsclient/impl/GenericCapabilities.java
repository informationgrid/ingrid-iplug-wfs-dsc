/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class GenericCapabilities implements WFSCapabilities {

	final protected static Log log = LogFactory.getLog(GenericCapabilities.class);

	protected static final XPathUtils xPathUtils = new XPathUtils(new WfsNamespaceContext());

	protected Document capDoc = null;

	@Override
	public void initialize(Document capDoc) {

		// check if capDoc is a valid capabilities document
		Node rootNode = xPathUtils.getNode(capDoc, "/wfs:WFS_Capabilities");
		//Node rootNode = XPathUtils.getNodeList(capDoc, "/*[local-name() = 'WFS_Capabilities']").item(0);
		if (rootNode != null && rootNode.getLocalName().equals("WFS_Capabilities")) {
			this.capDoc = capDoc;
		}
		else {
			// check if capDoc is an ExceptionReport
			String exStr = "The returned document is not a Capabilities document. Node 'WFS_Capabilities' could not be found.";
			Node exNode = xPathUtils.getNode(capDoc, "ows:ExceptionReport/ows:Exception/ows:ExceptionText");
			if (exNode != null) {
				exStr += ": "+exNode.getTextContent();
			}
			throw new RuntimeException(exStr);
		}
	}

	@Override
	public boolean isSupportingOperations(String[] operations) {

		int supportingOperations = 0;

		NodeList operationNodes = xPathUtils.getNodeList(this.capDoc, "/wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation[@name]");
		// compare supported operations with requested
		if (operationNodes != null) {
			Collection<String> requestedOperations = Arrays.asList(operations);
			for (int i=0; i<operationNodes.getLength(); i++) {
				String curName = operationNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				if (requestedOperations.contains(curName))
					supportingOperations++;
			}
		}
		return supportingOperations == operations.length;
	}

	@Override
	public String getOperationUrl(Operation op) {

		// extract the operation url from the OperationsMetadata element
		NodeList postNodes = xPathUtils.getNodeList(this.capDoc, "/wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation[@name='"+op+"']/ows:DCP/ows:HTTP/ows:Post");

		// if there are multiple POST nodes, we choose the one with the SOAP PostEncoding constraint
		Node postNode = null;
		if (postNodes.getLength() > 1) {
			postNode = xPathUtils.getNode(this.capDoc, "/wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation[@name='"+op+"']/ows:DCP/ows:HTTP/ows:Post[ows:Constraint[@name='PostEncoding']/ows:Value='SOAP']");
		}
		else {
			postNode = postNodes.item(0);
		}

		if (postNode != null) {
			// we search for an attribute named ...href (this seems to be the most robust way, because we can't be sure that
			// the ns prefix is always xlink and that the namespace is recognized correctly using the getNamedItemNS method)
			NamedNodeMap nodeAttributes = postNode.getAttributes();
			Node hrefNode = null;
			for (int i=0; i<nodeAttributes.getLength(); i++) {
				if (nodeAttributes.item(i).getNodeName().endsWith("href")) {
					hrefNode = nodeAttributes.item(i);
					break;
				}
			}
			if (hrefNode != null) {
				return hrefNode.getNodeValue();
			}
		}
		return null;
	}

	@Override
	public String[] getFeatureTypeNames() {

		List<String> types = new ArrayList<String>();
		NodeList featureTypeNameNodes = xPathUtils.getNodeList(this.capDoc, "/wfs:WFS_Capabilities/wfs:FeatureTypeList/wfs:FeatureType/wfs:Name");
		if (featureTypeNameNodes != null) {
			if (log.isInfoEnabled()) {
				log.info("Fetched Number of FeatureTypes from Capabilities: "+featureTypeNameNodes.getLength());
			}

			for (int i=0; i<featureTypeNameNodes.getLength(); i++) {
				String curName = featureTypeNameNodes.item(i).getTextContent();
				if (log.isDebugEnabled()) {
					log.debug("    " + curName);
				}
				types.add(curName);
			}
		}
		return types.toArray(new String[0]);
	}

	@Override
	public String toString() {
		return StringUtils.nodeToString(this.capDoc);
	}
}
