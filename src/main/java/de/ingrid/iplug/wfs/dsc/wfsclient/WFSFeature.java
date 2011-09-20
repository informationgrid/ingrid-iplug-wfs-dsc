/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.io.File;

import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;

/**
 * Representation of a feature returned by a WFS server.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSFeature {

	/**
	 * Configure the feature
	 * @param factory
	 */
	public void configure(WFSFactory factory);

	/**
	 * Initialize the feature.
	 * @param node The DOM Node describing the feature
	 */
	public void initialize(Node node) throws Exception;

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
	 * Set the file of the mapping script for mapping the feature to an id
	 * @param idMappingScript
	 */
	public void setIdMappingScript(File idMappingScript);

	/**
	 * Get the file of the mapping script for mapping the feature to an id
	 * @return File
	 */
	public File getIdMappingScript();

	/**
	 * Get the namespace context for this feature
	 * @return NamespaceContext
	 */
	public WfsNamespaceContext getNamespaceContext();
}
