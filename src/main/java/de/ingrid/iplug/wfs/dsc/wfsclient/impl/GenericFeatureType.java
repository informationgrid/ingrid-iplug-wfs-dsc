/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;

/**
 * Implementation of a WFS feature type.
 *
 * The feature type is defined by the following XML documents:
 * - GetCapabilities response (<FeatureType> node)
 * - DescribeFeatureType response
 */
public class GenericFeatureType extends GenericRecord implements WFSFeatureType {

	private static final int NUM_SOURCE_NODES = 2;

	private static final Logger log = Logger.getLogger(GenericFeatureType.class);

	private String name;
	private int numberOfFeatures;
	private List<WFSFeature> features;

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
		parameters.put("featureTypeNode", nodes.get(0));
		parameters.put("featureTypeDescNode", nodes.get(1));
		parameters.put("xPathUtils", this.xPathUtils);
		parameters.put("javaVersion", System.getProperty( "java.version" ));
		parameters.put("log", log);
		File[] scripts = new File[]{ this.idMappingScript };
		Map<String, Object> results = ScriptEngine.execute(scripts, parameters, this.compile);
		String id = (String)results.get(this.idMappingScript.getAbsolutePath());
		return id;
	}

	@Override
	public void initialize(Node... nodes) throws Exception {
		super.initialize(nodes);

		// get the name
		this.name = this.xPathUtils.getString(this.nodes.get(0), "//wfs:FeatureType/wfs:Name");

		// get the number of features
		WFSClient client = factory.createClient();
		WFSQuery query = factory.createQuery();
		query.setTypeName(this.name);
		query.setResultType(ResultType.HITS);
		WFSQueryResult result = client.getFeature(query);
		this.numberOfFeatures = result.getNumberOfFeaturesTotal();

		// get features, if number below limit and get an empty response else
		int featurePreviewLimit = factory.getFeaturePreviewLimit();
		if (this.numberOfFeatures <= featurePreviewLimit) {
			query.setResultType(ResultType.RESULTS);
			query.setMaxFeatures(this.numberOfFeatures);
			query.setStartIndex(0);
			WFSQueryResult queryResult = client.getFeature(query);
			this.features = queryResult.getFeatureList();
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getNumberOfFeatures() {
		return this.numberOfFeatures;
	}

	@Override
	public List<WFSFeature> getFeatures() {
		return this.features;
	}
}
