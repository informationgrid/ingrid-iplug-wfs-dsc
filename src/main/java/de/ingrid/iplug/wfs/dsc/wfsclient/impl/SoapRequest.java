/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSConstants;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRequest;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Namespace;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

public class SoapRequest implements WFSRequest {

	final protected static Log log = LogFactory.getLog(WFSRequest.class);

	/**
	 * WFSRequest implementation
	 */

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.7
	 */
	@Override
	public Document doGetCapabilities(String serverURL) throws Exception {

		// create the request
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace wfsNs = fac.createOMNamespace(Namespace.WFS.getQName().getNamespaceURI(),
				Namespace.WFS.getQName().getPrefix());

		// create method
		OMElement method = fac.createOMElement(Operation.GET_CAPABILITIES.toString(), wfsNs);
		method.addAttribute("service", WFSConstants.SERVICE_TYPE, null);
		method.addAttribute("version", WFSConstants.PREFERRED_VERSION, null);

		// send the request
		try {
			return this.sendRequest(serverURL, method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.2
	 */
	@Override
	public Document doDescribeFeatureType(String serverURL, WFSQuery query) throws Exception {

		// create the request
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace wfsNs = fac.createOMNamespace(Namespace.WFS.getQName().getNamespaceURI(),
				Namespace.WFS.getQName().getPrefix());

		// create method
		OMElement method = fac.createOMElement(Operation.DESCRIBE_FEATURE_TYPE.toString(), wfsNs);

		// add the default parameters
		method.addAttribute("service", WFSConstants.SERVICE_TYPE, null);
		method.addAttribute("version", query.getVersion(), null);

		// add the query specific parameters
		method.addAttribute("outputFormat", query.getOutputFormat().toString(), null);

		// create TypeName element
		OMElement typeName = fac.createOMElement("TypeName", wfsNs);
		typeName.setText(query.getTypeName());
		method.addChild(typeName);

		// send the request
		try {
			return this.sendRequest(serverURL, method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.3
	 */
	@Override
	public Document doGetFeature(String serverURL, WFSQuery query) throws Exception {

		// create the request
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace wfsNs = fac.createOMNamespace(Namespace.WFS.getQName().getNamespaceURI(),
				Namespace.WFS.getQName().getPrefix());
		@SuppressWarnings("unused")
        OMNamespace ogcNs = fac.createOMNamespace(Namespace.OGC.getQName().getNamespaceURI(),
				Namespace.OGC.getQName().getPrefix()); // needed for filter definition

		// create method
		OMElement method = fac.createOMElement(Operation.GET_FEATURE.toString(), wfsNs);

		// add the default parameters
		method.addAttribute("service", WFSConstants.SERVICE_TYPE, null);
		method.addAttribute("version", query.getVersion(), null);

		// add the query specific parameters
		method.addAttribute("outputFormat", query.getOutputFormat().toString(), null);
		method.addAttribute("resultType", query.getResultType().toString(), null);
		if (query.getMaxFeatures() != null) {
			method.addAttribute("maxFeatures", query.getMaxFeatures().toString(), null);
		}
		if (query.getStartIndex() != null) {
			method.addAttribute("startIndex", query.getStartIndex().toString(), null);
		}

		// create Query element
		OMElement queryElem = fac.createOMElement("Query", wfsNs);
		queryElem.addAttribute("typeName", query.getTypeName().toString(), null);

		// add the Filter
		if (query.getFilter() != null) {
			OMElement filter = XMLUtils.toOM(query.getFilter().getDocumentElement());
			queryElem.addChild(filter);
		}

		method.addChild(queryElem);

		// send the request
		try {
			return this.sendRequest(serverURL, method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Helper methods
	 */

	/**
	 * Create a soap client for the given server url
	 * @param serverURL
	 * @return ServiceClient
	 * @throws AxisFault
	 */
	protected ServiceClient createClient(String serverURL) throws AxisFault, Exception {
		// set up the client
		ConfigurationContext configContext =
				ConfigurationContextFactory.createConfigurationContextFromFileSystem((new ClassPathResource("axis2.xml")).getURI().getPath());
		ServiceClient serviceClient = new ServiceClient(configContext, null);

		Options opts = new Options();
		opts.setTo(new EndpointReference(serverURL));
		opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, false);
		opts.setProperty(org.apache.axis2.Constants.Configuration.CHARACTER_SET_ENCODING, "UTF-8");
		/*
		opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
			org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_10);
		 */
		opts.setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		//opts.setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		opts.setAction("urn:anonOutInOp");
		serviceClient.setOptions(opts);

		return serviceClient;
	}

	/**
	 * Send the given request to the server.
	 * @param serverURL
	 * @param payload
	 * @return Document
	 * @throws Exception
	 */
	protected Document sendRequest(String serverURL, OMElement payload) throws Exception {
		// set up the client
		ServiceClient serviceClient;
		try {
			serviceClient = this.createClient(serverURL);
		} catch (AxisFault e) {
			throw new RuntimeException(e);
		}

		// send the request
		if (log.isDebugEnabled()) {
			log.debug("Request: "+this.serializeElement(payload.cloneOMElement()));
		}
		OMElement result = null;
		result = serviceClient.sendReceive(payload);
		if (log.isDebugEnabled()) {
			log.debug("Response: "+this.serializeElement(result.cloneOMElement()));
		}
		Document doc = this.convertToDOM(result);
		serviceClient.cleanupTransport();
		serviceClient.cleanup();
		serviceClient.getServiceContext().getConfigurationContext().terminate();
		return doc;
	}

	/**
	 * Get a string representation for an OMElement
	 * @param element
	 * @return String
	 * @throws XMLStreamException
	 */
	protected String serializeElement(OMElement element) throws XMLStreamException {
		return element.toStringWithConsume();
	}

	/**
	 * Convert an OMElement to a w3c DOM Document
	 * TODO: possible performance bottleneck
	 * @param element
	 * @return Document
	 * @throws Exception
	 */
	protected Document convertToDOM(OMElement element) throws Exception {
		String xmlString = this.serializeElement(element);
		return StringUtils.stringToDocument(xmlString);
	}
}
