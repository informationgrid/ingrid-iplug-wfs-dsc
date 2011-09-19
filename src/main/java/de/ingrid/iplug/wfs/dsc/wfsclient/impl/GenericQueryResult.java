/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.utils.xpath.XPathUtils;

public class GenericQueryResult implements WFSQueryResult {

	protected static final XPathUtils xPathUtils = new XPathUtils(WfsNamespaceContext.INSTANCE);

	protected WFSQuery query = null;
	protected Document document = null;
	protected List<WFSFeature> features = null;
	protected int featuresTotal = 0;

	@Override
	public void initialize(Document document, WFSQuery query, WFSFactory factory) throws Exception {
		this.query = query;
		this.document = document;
		this.features = new ArrayList<WFSFeature>();

		// parse the document and create the feature list
		Integer numMatched = xPathUtils.getInt(document, "/wfs:FeatureCollection/@numberOfFeatures");
		if (numMatched != null) {
			this.featuresTotal = numMatched.intValue();

			NodeList featureNodes = xPathUtils.getNodeList(document, "/wfs:FeatureCollection/gml:featureMembers/child::*");
			if (featureNodes != null) {
				for (int i=0; i<featureNodes.getLength(); i++) {
					// create the feature
					WFSFeature feature = factory.createFeature();
					feature.initialize(featureNodes.item(i), factory);
					this.features.add(feature);
				}
			}
		}
	}

	@Override
	public WFSQuery getQuery() {
		return this.query;
	}

	@Override
	public Document getOriginalResponse() {
		return this.document;
	}

	@Override
	public int getNumberOfFeaturesTotal() {
		return this.featuresTotal;
	}

	@Override
	public int getNumberOfFeatures() {
		if (this.features != null)
			return this.features.size();
		return 0;
	}

	@Override
	public List<WFSFeature> getFeatureList() {
		return this.features;
	}
}
