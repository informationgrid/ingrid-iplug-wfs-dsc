/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

/**
 * Representation of a feature returned by a WFS server.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSFeature {

	/**
	 * Initialize the feature.
	 * @param node The DOM Node describing the feature
	 * @param factory The WFSFactory instance
	 */
	public void initialize(Node node, WFSFactory factory) throws Exception;

	/**
	 * Get the id of the feature
	 * @return String
	 */
	public String getId();

	/**
	 * Get the original DOM Node describing the feature
	 * @return Node
	 */
	public Node getOriginalResponse();

	/**
	 * Set the mapping script for mapping the feature to an id
	 * @param mappingScript
	 */
	public void setIdMappingScript(Resource mappingScript);
}
