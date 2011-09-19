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
	 * Initialize the result.
	 * @param document
	 * @param query
	 * @param factory
	 */
	public void initialize(Document document, WFSQuery query, WFSFactory factory) throws Exception;

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
