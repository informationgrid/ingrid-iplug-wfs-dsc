/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSConstants;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRequest;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

public class KVPGetRequest implements WFSRequest {

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
		String requestURL = serverURL+
				"?SERVICE="+WFSConstants.SERVICE_TYPE+
				"&REQUEST="+Operation.GET_FEATURE+
				"&VERSION="+query.getVersion()+
				"&TYPENAME="+query.getTypeName()+
				"&OUTPUTFORMAT="+query.getOutputFormat().toString()+
				"&RESULTTYPE="+query.getResultType()+
				"&FILTER="+query.getFilterAsString();

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
			if (code >= 200 && code < 300) {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
				domFactory.setNamespaceAware(true);
				DocumentBuilder builder = domFactory.newDocumentBuilder();
				result = builder.parse(conn.getInputStream());
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
