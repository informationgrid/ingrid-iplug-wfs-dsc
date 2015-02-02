/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSConstants;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRequest;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Namespace;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

public class PostRequest implements WFSRequest {

	final protected static Log log = LogFactory.getLog(PostRequest.class);

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
			return this.sendRequest(serverURL, method.toStringWithConsume());
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
			return this.sendRequest(serverURL, method.toStringWithConsume());
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
			return this.sendRequest(serverURL, method.toStringWithConsume());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Helper methods
	 */

	/**
	 * Send the given request to the server.
	 * @param serverURL
	 * @param payload
	 * @return Document
	 * @throws Exception
	 */
	protected Document sendRequest(String requestURL, String payload) throws Exception {

		log.debug("Sending POST request to "+requestURL);
		log.debug("Body: "+payload);

		// and make the call
		Document result = null;
		HttpURLConnection conn = null;
		DataOutputStream os = null;
		try {
			URL url = new URL(requestURL);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", "" + Integer.toString(payload.getBytes().length));
			conn.setAllowUserInteraction(false);
			conn.setReadTimeout(300000); // 5 minutes
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type", "text/xml");

			os = new DataOutputStream(conn.getOutputStream());
			os.writeBytes(payload);
			os.flush();

			int code = conn.getResponseCode();
			log.debug("Response code: "+code);
			if (code >= 200 && code < 300) {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
				domFactory.setNamespaceAware(true);
				DocumentBuilder builder = domFactory.newDocumentBuilder();
				result = builder.parse(conn.getInputStream());
				if (log.isDebugEnabled()) {
					log.debug("Response: "+StringUtils.nodeToString(result));
				}
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (os != null) {
				os.close();
			}
		}
		return result;
	}
}
