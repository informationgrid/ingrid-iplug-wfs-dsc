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

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.util.List;

import org.w3c.dom.Document;

/**
 * Representation of a feature query result from a WFS server.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSQueryResult {

	/**
	 * Configure the WFSClient
	 * @param factory
	 */
	public void configure(WFSFactory factory);

	/**
	 * Initialize the result.
	 * @param document
	 * @param query
	 */
	public void initialize(Document document, WFSQuery query) throws Exception;

	/**
	 * Get the associated query
	 * @return WFSQuery
	 */
	public WFSQuery getQuery();

	/**
	 * Get the original response document
	 * @return Document
	 */
	public Document getOriginalResponse();

	/**
	 * Get the number of all features that matched the query
	 * @return int
	 */
	public int getNumberOfFeaturesTotal();

	/**
	 * Get the number of features contained in this result
	 * @return int
	 */
	public int getNumberOfFeatures();

	/**
	 * Get the feature list
	 * @return List<WFSFeature>
	 */
	public List<WFSFeature> getFeatureList();
}
