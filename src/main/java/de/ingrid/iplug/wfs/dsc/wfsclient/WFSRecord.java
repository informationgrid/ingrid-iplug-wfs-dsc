/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
 * Copyright (c) 2020 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.io.File;
import java.util.List;

import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;

/**
 * Representation of a feature or feature type returned by a WFS server.
 *
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSRecord {

	/**
	 * Configure the record
	 * @param factory
	 */
	public void configure(WFSFactory factory);

	/**
	 * Initialize the record.
	 * @param nodes The DOM Node instances describing the record
	 */
	public void initialize(Node... nodes) throws Exception;

	/**
	 * Get the id of the record
	 * @return String
	 */
	public String getId();

	/**
	 * Get the original DOM Node instances describing the record
	 * @return List<Node>
	 */
	public List<Node> getOriginalResponse();

	/**
	 * Set the file of the mapping script for mapping the record to an id
	 * @param idMappingScript
	 */
	public void setIdMappingScript(File idMappingScript);

	/**
	 * Get the file of the mapping script for mapping the record to an id
	 * @return File
	 */
	public File getIdMappingScript();

	/**
	 * Get the namespace context for this record
	 * @return NamespaceContext
	 */
	public WfsNamespaceContext getNamespaceContext();
	
	public WFSFactory getFactory();
}
