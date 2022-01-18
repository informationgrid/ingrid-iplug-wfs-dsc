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

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.NodeUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Implementation of a WFSRecord that is the base interface for features and feature types.
 *
 * Each record (feature or feature type) is created from one or more XML documents that the
 * WFS sends as response to the appropriate description requests.
 *
 * Since records are defined in an arbitrary namespace and potentially several XML documents,
 * it is necessary to provide a feature/feature type specific rule for extracting the id of the instance.
 * This is done using a javascript mapping file.
 *
 * @author ingo@wemove.com
 */
public abstract class GenericRecord implements WFSRecord {

	protected File idMappingScript = null;
	protected boolean compile = true;

	protected WFSFactory factory = null;
	protected XPathUtils xPathUtils = null;
	protected String id = null;
	protected List<Node> nodes = null;
	protected WfsNamespaceContext namespaceContext = null;

	@Override
	public void configure(WFSFactory factory) {
		this.factory = factory;
	}

	@Override
	public void initialize(Node... nodes) throws Exception {
		if (this.factory == null) {
			throw new RuntimeException(this.getClass().getName() + " is not configured properly. Make sure to call the configure method.");
		}
		int numExpectedSourceNodes = this.getNumberOfSourceNodes();
		if (nodes == null || nodes.length != numExpectedSourceNodes) {
			throw new IllegalArgumentException("Error initializing " + this.getClass().getName() + ". Expecting " + numExpectedSourceNodes + " source node(s), but got " +
					(nodes == null ? 0 : nodes.length) + ".");
		}

		// detach nodes from whole documents including all namespace definitions
		this.nodes = new ArrayList<>();
		for (Node node : nodes) {
			while (node instanceof Comment) {
				node = node.getNextSibling();
			}
			this.nodes.add(NodeUtils.detachWithNameSpaces(node));
		}

		// create namespace context for this record
		this.namespaceContext = new WfsNamespaceContext();
		for (Node node : this.nodes) {
			this.namespaceContext.addNamespace(node.getPrefix(), node.getNamespaceURI());
		}
		this.xPathUtils = new XPathUtils(this.namespaceContext);

		// get the record id
		String recordId = this.extractRecordId(this.nodes);
		this.id = encodeId(this.factory.getServiceUrl()+":"+recordId);
	}

	@Override
	public String getId() {
		if (this.id == null) {
			throw new RuntimeException(this.getClass().getName() + " is not initialized properly. Make sure to call the initialize method.");
		}
		return this.id;
	}

	@Override
	public List<Node> getOriginalResponse() {
		if (this.nodes == null) {
			throw new RuntimeException(this.getClass().getName() + " is not initialized properly. Make sure to call the initialize method.");
		}
		return this.nodes;
	}

	@Override
	public void setIdMappingScript(File idMappingScript) {
		this.idMappingScript = idMappingScript;
	}

	@Override
	public File getIdMappingScript() {
		return this.idMappingScript;
	}

	@Override
	public WfsNamespaceContext getNamespaceContext() {
		return this.namespaceContext;
	}

	/**
	 * Get the number of Node instances required for this record
	 * @return int
	 */
	protected abstract int getNumberOfSourceNodes();

	/**
	 * Extract the record id from the given Node instances
	 * @param nodes The Node instances representing the record
	 * @return String
	 * @throws Exception
	 */
	protected abstract String extractRecordId(List<Node> nodes) throws Exception;

	/**
	 * Create a MD5 hash for the given id value
	 * @param id
	 * @return String
	 * @throws Exception
	 */
	private static String encodeId(String id) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(id.getBytes());
		byte[] result = md5.digest();

		StringBuffer hexString = new StringBuffer();
		for (byte element : result) {
			hexString.append(Integer.toHexString(0xFF & element));
		}
		return hexString.toString();
	}
}
