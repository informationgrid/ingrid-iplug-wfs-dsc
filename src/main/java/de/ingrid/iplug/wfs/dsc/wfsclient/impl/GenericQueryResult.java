/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class GenericQueryResult implements WFSQueryResult {

	protected static final XPathUtils xPathUtils = new XPathUtils(new WfsNamespaceContext());
	private static final Logger log = Logger.getLogger(GenericQueryResult.class);

	private WFSFactory factory = null;
	protected WFSQuery query = null;
	protected Document document = null;
	protected List<WFSFeature> features = null;
	protected int featuresTotal = 0;

	@Override
	public void configure(WFSFactory factory) {
		this.factory  = factory;
	}

	@Override
	public void initialize(Document document, WFSQuery query) throws Exception {
		if (this.factory == null) {
			throw new RuntimeException("WFSQueryResult is not configured properly. Make sure to call WFSQueryResult.configure.");
		}

		this.query = query;
		this.document = document;
		this.features = new ArrayList<WFSFeature>();
		
		// parse the document and create the feature list
		// NOTE: in version 1.1.0 all features are encapsulated in a featureMembers node
		//       in version 1.0.0 each feature is encapsulated in a featureMember node
		NodeList featureNodes = xPathUtils.getNodeList(document,
				"/wfs:FeatureCollection/gml:featureMembers/child::*|/wfs:FeatureCollection/gml:featureMember/child::*");
		if (featureNodes != null) {
			for (int i=0; i<featureNodes.getLength(); i++) {
				// create the feature
				try {
					WFSFeature feature = this.factory.createFeature();
					feature.initialize(featureNodes.item(i));
					this.features.add(feature);										
				} catch (Exception ex) {
					String msg = "Problems creating WFSFeature from feature node '" + featureNodes.item(i) + "', we skip this one !";
					log.error(msg + " -> " + ex.getMessage());
					if (log.isDebugEnabled()) {
						log.debug("Caused Exception:", ex);
					}
				}
			}
			this.featuresTotal = this.features.size();
		}
		
		// Check whether only total number of features was queried (e.g. ...SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=xxx&RESULTTYPE=hits).
		// Then total number is in numberOfFeatures attribute in top FeatureCollection node
		if (featureNodes == null || featureNodes.getLength() == 0) {
			Integer numFeatures = xPathUtils.getInt(document, "/wfs:FeatureCollection/@numberOfFeatures");
			if (numFeatures != null) {
				featuresTotal = numFeatures;
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
