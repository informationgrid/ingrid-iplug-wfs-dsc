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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSConstants;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRequest;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

public class KVPGetRequest implements WFSRequest {

	final protected static Log log = LogFactory.getLog(KVPGetRequest.class);

	/**
	 * WFSRequest implementation
	 */

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.7
	 */
	@Override
	public Document doGetCapabilities(String serverURL) throws Exception {

		// add the GetCapability request parameters,
		// @note Parameters must be treated in case-insensitive manner on the server side
		String requestURL = serverURL+
				"?SERVICE="+WFSConstants.SERVICE_TYPE+
				"&REQUEST="+Operation.GET_CAPABILITIES+
				"&VERSION="+WFSConstants.PREFERRED_VERSION;

		Document result = this.sendRequest(requestURL);
		return result;
	}

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.2
	 */
	@Override
	public Document doDescribeFeatureType(String serverURL, WFSQuery query) throws Exception {

		// add the GetFeatureType request parameters,
		// @note Parameters must be treated in case-insensitive manner on the server side
		String requestURL = serverURL+
				"?SERVICE="+WFSConstants.SERVICE_TYPE+
				"&REQUEST="+Operation.GET_FEATURE+
				"&VERSION="+query.getVersion()+
				"&TYPENAME="+query.getTypeName()+
				"&OUTPUTFORMAT="+query.getOutputFormat().toString();

		Document result = this.sendRequest(requestURL);
		return result;
	}

	/**
	 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.7.3
	 */
	@Override
	public Document doGetFeature(String serverURL, WFSQuery query) throws Exception {

		// add the GetFeature request parameters,
		// @note Parameters must be treated in case-insensitive manner on the server side
		String requestURL = serverURL;
		if (!requestURL.endsWith("?")) {
			requestURL += "?";
		}
		requestURL += "SERVICE="+WFSConstants.SERVICE_TYPE+
				"&REQUEST="+Operation.GET_FEATURE+
				"&VERSION="+query.getVersion()+
				"&TYPENAME="+query.getTypeName()+
				"&RESULTTYPE="+query.getResultType();
		if (query.getOutputFormat() != null) {
			requestURL += "&OUTPUTFORMAT="+query.getOutputFormat().toString().replace(" ", "%20");			
		}
		if (query.getFilter() != null) {
			requestURL += "&FILTER="+query.getFilterAsString();
		}
		if (query.getMaxFeatures() != null) {
			requestURL += "&MAXFEATURES="+query.getMaxFeatures();
		}
		if (query.getStartIndex() != null) {
			requestURL += "&STARTINDEX="+query.getStartIndex();
		}

		Document result = this.sendRequest(requestURL);
		return result;
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
	protected Document sendRequest(String requestURL) throws Exception {

		log.debug("Sending GET request: "+requestURL);

		// and make the call
		Document result = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(requestURL);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setAllowUserInteraction(false);
			conn.setReadTimeout(300000); // 5 minutes
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type", "text/xml");
			//conn.connect();

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
			if (conn != null)
				conn.disconnect();
		}
		return result;
	}
}
