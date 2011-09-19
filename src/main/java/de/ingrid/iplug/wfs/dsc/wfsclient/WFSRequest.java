/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import org.w3c.dom.Document;

/**
 * Representation of a request sent to a WFS server.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSRequest {

	/**
	 * Do the GetCapabilities request
	 * @param serverURL
	 * @return The response DOM Document
	 */
	public Document doGetCapabilities(String serverURL) throws Exception;

	/**
	 * Do the DescribeFeatureType request
	 * @param serverURL
	 * @param query
	 * @return The response DOM Document
	 */
	public Document doDescribeFeatureType(String serverURL, WFSQuery query) throws Exception;

	/**
	 * Do the GetFeature request
	 * @param serverURL
	 * @param query
	 * @return The response DOM Document
	 */
	public Document doGetFeature(String serverURL, WFSQuery query) throws Exception;
}
