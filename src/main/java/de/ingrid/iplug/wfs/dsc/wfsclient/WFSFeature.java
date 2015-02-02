/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
