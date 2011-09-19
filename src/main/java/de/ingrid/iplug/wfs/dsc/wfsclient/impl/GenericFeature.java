/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.io.InputStreamReader;
import java.security.MessageDigest;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.NodeUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Implementation of a WFSFeature. Since feature types are defined
 * in an arbitrary namespace, it is necessary to provide a feature specific rule
 * for extracting the id of the feature instance. This is done using a javascript
 * mapping script.
 * @author ingo@wemove.com
 */
public class GenericFeature implements WFSFeature {

	protected static final XPathUtils xPathUtils = new XPathUtils(WfsNamespaceContext.INSTANCE);

	protected static ScriptEngine engine = null;
	protected static Resource mappingScript = null;
	protected static CompiledScript compiledScript = null;
	protected static boolean compile = true;

	private static final Logger log = Logger.getLogger(GenericFeature.class);

	protected String id = null;
	protected Node node = null;

	/**
	 * Initializes the feature. The node will be detached (cloned) from it's owner document.
	 * 
	 * @param node The DOM Node describing the feature. The node will be detached (cloned).
	 */
	@Override
	public void initialize(Node node, WFSFactory factory) throws Exception {
		// detach node from whole document including all namespace definitions
		while (node instanceof Comment) {
			node = node.getNextSibling();
		}
		this.node = NodeUtils.detachWithNameSpaces(node);

		// get the feature id
		this.id = encodeId(factory.getServiceUrl()+":"+extractFeatureId(this.node));
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
	public void setIdMappingScript(Resource mappingScript) {
		GenericFeature.mappingScript = mappingScript;
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
	protected static String extractFeatureId(Node featureNode) throws Exception {
		String id = null;
		Bindings bindings = getScriptingEngine().createBindings();
		bindings.put("featureNode", featureNode);
		bindings.put("xPathUtils", xPathUtils);
		bindings.put("log", log);
		if (compiledScript != null) {
			id = (String)compiledScript.eval(bindings);
		} else {
			id = (String)engine.eval(new InputStreamReader(mappingScript.getInputStream()), bindings);
		}
		return id;
	}

	/**
	 * Get the ScriptEngine instance
	 * @return ScriptEngine instance
	 * @throws Exception
	 */
	protected static ScriptEngine getScriptingEngine() throws Exception {
		if (mappingScript == null) {
			throw new RuntimeException("GenericFeature is not configured properly. Parameter 'mappingScript' is missing or wrong.");
		}
		if (engine == null) {
			initializeScripting();
		}
		return engine;
	}

	/**
	 * Initialize the ScriptEngine and compile the mapping script
	 * @throws Exception
	 */
	protected static void initializeScripting() throws Exception {
		String scriptName = mappingScript.getFilename();
		String extension = scriptName.substring(scriptName.lastIndexOf('.') + 1, scriptName.length());
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByExtension(extension);
		if (compile) {
			if (engine instanceof Compilable) {
				Compilable compilable = (Compilable)engine;
				compiledScript = compilable.compile(new InputStreamReader(mappingScript.getInputStream()));
			}
		}
	}
}
