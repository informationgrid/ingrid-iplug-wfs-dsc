/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.ScriptEngine;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;

/**
 * Implementation of a WFS feature .
 *
 * The feature is defined by the following XML documents:
 * - GetFeature response
 */
public class GenericFeature extends GenericRecord implements WFSFeature {

	private static final int NUM_SOURCE_NODES = 1;

	private static final Logger log = Logger.getLogger(GenericFeature.class);

	@Override
	protected int getNumberOfSourceNodes() {
		return NUM_SOURCE_NODES;
	}

	@Override
	protected String extractRecordId(List<Node> nodes) throws Exception {
		if (this.idMappingScript == null) {
			throw new RuntimeException(this.getClass().getName() + " is not configured properly. Parameter 'idMappingScript' is missing or wrong.");
		}
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("featureNode", nodes.get(0));
		parameters.put("xPathUtils", this.xPathUtils);
		parameters.put("javaVersion", System.getProperty( "java.version" ));
		parameters.put("log", log);
		File[] scripts = new File[]{ this.idMappingScript };
		Map<String, Object> results = ScriptEngine.execute(scripts, parameters, this.compile);
		String id = (String)results.get(this.idMappingScript.getAbsolutePath());
		return id;
	}
}
