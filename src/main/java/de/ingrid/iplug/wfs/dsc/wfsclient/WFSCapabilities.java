/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

/**
 * Representation of a WFS Server's getCapabilities response that
 * describes the service metadata.
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSCapabilities {

	/**
	 * Initialize the WFSCapabilities instance.
	 * @param capDoc The capabilities document received from a WFS server
	 * @param factory The WFSFactory instance
	 */
	public void initialize(Document capDoc, WFSFactory factory);

	/**
	 * Check if the WFS server supports the given operations.
	 * @param operations An array of operation names
	 * @return boolean
	 */
	public boolean isSupportingOperations(String[] operations);

	/**
	 * Get the SOAP URL for a given operation from the appropriate OperationsMetadata/Operation/DCP/HTTP/POST
	 * element if defined in the capabilities document.
	 * @param op The operation
	 * @return The URL String or null of no SOAP URL is defined
	 */
	public String getOperationUrl(Operation op);

	/**
	 * Get the names of the feature types provided by the WFS server.
	 * @return Array of String instances
	 */
	public String[] getFeatureTypeNames();

	/**
	 * Get a String representation of the Capabilities document
	 * @return String
	 */
	@Override
	public String toString();
}