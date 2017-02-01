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
