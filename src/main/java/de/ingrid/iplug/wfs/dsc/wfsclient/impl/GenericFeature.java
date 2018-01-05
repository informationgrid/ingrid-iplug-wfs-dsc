/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.io.File;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.NodeUtils;
import de.ingrid.iplug.wfs.dsc.tools.ScriptEngine;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Implementation of a WFSFeature. Since feature types are defined
 * in an arbitrary namespace, it is necessary to provide a feature specific rule
 * for extracting the id of the feature instance. This is done using a javascript
 * mapping file.
 * @author ingo@wemove.com
 */
public class GenericFeature implements WFSFeature {


	protected File idMappingScript = null;
	protected boolean compile = true;

	private static final Logger log = Logger.getLogger(GenericFeature.class);

	protected WFSFactory factory = null;
	protected XPathUtils xPathUtils = null;
	protected String id = null;
	protected Node node = null;
	protected WfsNamespaceContext namespaceContext = null;

	@Override
	public void configure(WFSFactory factory) {
		this.factory = factory;
	}

	/**
	 * Initializes the feature. The node will be detached (cloned) from it's owner document.
	 * 
	 * @param node The DOM Node describing the feature. The node will be detached (cloned).
	 */
	@Override
	public void initialize(Node node) throws Exception {
		if (this.factory == null) {
			throw new RuntimeException("WFSFeature is not configured properly. Make sure to call WFSFeature.configure.");
		}

		// detach node from whole document including all namespace definitions
		while (node instanceof Comment) {
			node = node.getNextSibling();
		}
		this.node = NodeUtils.detachWithNameSpaces(node);

		// create namespace context for this feature
		this.namespaceContext = new WfsNamespaceContext();
		this.namespaceContext.addNamespace(node.getPrefix(), node.getNamespaceURI());
		this.xPathUtils = new XPathUtils(this.namespaceContext);

		// get the feature id
		String featureId = this.extractFeatureId(this.node);
		this.id = encodeId(this.factory.getServiceUrl()+":"+featureId);
	}

	@Override
	public String getId() {
		if (this.id != null) {
			return this.id;
		}
		else {
			throw new RuntimeException("WFSFeature is not initialized properly. Make sure to call WFSFeature.initialize.");
		}
	}

	@Override
	public Node getOriginalResponse() {
		if (this.node != null) {
			return this.node;
		}
		else {
			throw new RuntimeException("WFSFeature is not initialized properly. Make sure to call WFSFeature.initialize.");
		}
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
	 * Create a MD5 hash for the given id value
	 * @param id
	 * @return String
	 * @throws Exception
	 */
	protected static String encodeId(String id) throws Exception {
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

	/**
	 * Extract the feature from the given feature Node instance
	 * @param wfsFeature The feature Node
	 * @return String
	 * @throws Exception
	 */
	protected String extractFeatureId(Node featureNode) throws Exception {
		if (this.idMappingScript == null) {
			throw new RuntimeException("GenericFeature is not configured properly. Parameter 'idMappingScript' is missing or wrong.");
		}
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("featureNode", featureNode);
		parameters.put("xPathUtils", this.xPathUtils);
		parameters.put("javaVersion", System.getProperty( "java.version" ));
		parameters.put("log", log);
		File[] scripts = new File[]{ this.idMappingScript };
		Map<String, Object> results = ScriptEngine.execute(scripts, parameters, this.compile);
		String id = (String)results.get(this.idMappingScript.getAbsolutePath());
		return id;
	}
}
