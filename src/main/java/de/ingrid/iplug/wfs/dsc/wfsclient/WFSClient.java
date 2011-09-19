/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;


/**
 * Interface definition for a WFS server.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSClient {

	/**
	 * Configure the WFSClient
	 * @param factory
	 */
	public void configure(WFSFactory factory);

	/**
	 * Get the WFSFactory
	 * @return A WFSFactory instance
	 */
	public WFSFactory getFactory();

	/**
	 * Do the OGCWebService.getCapabilities request
	 * @note The request url is the service url
	 * @return A WFSCapabilities instance
	 */
	public WFSCapabilities getCapabilities() throws Exception;

	/**
	 * Do the WebFeatureService.describeFeatureType request
	 * @note The request url is the taken from the capabilities document
	 * and defaults to service url, if not defined there
	 * @return A WFSFeatureType instance
	 */
	public WFSFeatureType describeFeatureType() throws Exception;

	/**
	 * Do the WebFeatureService.GetFeature request using a
	 * given WFSQuery instance
	 * @note The request url is the taken from the capabilities document
	 * and defaults to service url, if not defined there
	 * @param query
	 * @return A WFSQueryResult instance
	 */
	public WFSQueryResult getFeature(WFSQuery query) throws Exception;
}
