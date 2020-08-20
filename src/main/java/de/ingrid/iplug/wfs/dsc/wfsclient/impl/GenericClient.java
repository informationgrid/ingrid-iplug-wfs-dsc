/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class GenericClient implements WFSClient {

	final protected static Log log = LogFactory.getLog(GenericClient.class);

	protected WFSFactory factory;
	protected WFSCapabilities capabilities;

	@Override
	public void configure(WFSFactory factory) {
		this.factory = factory;
	}

	@Override
	public WFSFactory getFactory() {
		return this.factory;
	}

	@Override
	public WFSCapabilities getCapabilities() throws Exception {
		if (this.capabilities == null) {
			if (this.factory != null) {
				this.capabilities = this.factory.createCapabilities();

				String serviceUrl = this.factory.getServiceUrl();
				Document capDoc = this.factory.createRequest(Operation.GET_CAPABILITIES).doGetCapabilities(serviceUrl);
				if (log.isDebugEnabled()) {
					log.debug("Initializing capabilities document for '" + serviceUrl + "' with documents:");
					log.debug("- GetCapabilities response: " + StringUtils.nodeToString(capDoc));
				}
				this.capabilities.initialize(capDoc);
			}
			else {
				throw new RuntimeException("WFSClient is not configured properly. Make sure to call WFSClient.configure.");
			}
		}
		return this.capabilities;
	}

	@Override
	public WFSFeatureType describeFeatureType(WFSQuery query) throws Exception {
		if (this.factory != null) {
			String opUrl = this.getOperationUrl(Operation.DESCRIBE_FEATURE_TYPE);

			// NOTE: we construct the featureType from multiple documents, because information about feature types is provided in several WFS responses
			// 1. the getCapabilities document contains a list of <FeatureType> nodes consisting of name, title, abstract, ...
			// 2. the describeFeatureType document contains the fields of features of the feature type

			// 1. extract <FeatureType> node from capabilities document
			XPathUtils xPathUtils = new XPathUtils(new WfsNamespaceContext());
			String xPath = "/wfs:WFS_Capabilities/wfs:FeatureTypeList/wfs:FeatureType[./wfs:Name='" + query.getTypeName() + "']";
			Node featureTypeNode = xPathUtils.getNode(this.getCapabilities().getOriginalResponse(), xPath);
			if (featureTypeNode == null) {
				throw new RuntimeException("FeatureType with name '" + query.getTypeName() + "' does not exist in capabilities document.");
			}

			// 2. get describe feature response
			Document describeFeatureDoc = this.factory.createRequest(Operation.DESCRIBE_FEATURE_TYPE).doDescribeFeatureType(opUrl, query);

			if (log.isDebugEnabled()) {
				log.debug("Initializing feature type '" + query.getTypeName() + "' with documents:");
				log.debug("- Capabilities extract: " + StringUtils.nodeToString(featureTypeNode));
				log.debug("- DescribeFeatureType response: " + StringUtils.nodeToString(describeFeatureDoc));
			}

			WFSFeatureType type = this.factory.createFeatureType();
			type.initialize(featureTypeNode, describeFeatureDoc.getFirstChild());
			return type;
		}
		else {
			throw new RuntimeException("WFSClient is not configured properly. Make sure to call WFSClient.configure.");
		}
	}

	@Override
	public WFSQueryResult getFeature(WFSQuery query) throws Exception {
		if (this.factory != null) {
			String opUrl = this.getOperationUrl(Operation.GET_FEATURE);
			Document getFeatureDoc = this.factory.createRequest(Operation.GET_FEATURE).doGetFeature(opUrl, query);

			if (log.isDebugEnabled()) {
				log.debug("Initializing feature '" + query.getFilterAsString() + "' with documents:");
				log.debug("- GetFeature response: " + StringUtils.nodeToString(getFeatureDoc));
			}
			WFSQueryResult result = this.factory.createQueryResult();
			result.initialize(getFeatureDoc, query);
			return result;
		}
		else {
			throw new RuntimeException("WFSClient is not configured properly. Make sure to call WFSClient.configure.");
		}
	}

	/**
	 * Get the SOAP URL for a given operation
	 * @param operation
	 * @return The URL String
	 */
	protected String getOperationUrl(Operation operation) throws Exception {
		WFSCapabilities cap = this.getCapabilities();
		String opUrl = cap.getOperationUrl(operation);
		if (opUrl == null) {
			opUrl = this.factory.getServiceUrl();
		}
		return opUrl;
	}
}
