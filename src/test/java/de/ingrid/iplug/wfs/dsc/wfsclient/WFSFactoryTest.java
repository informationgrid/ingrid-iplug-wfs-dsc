/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.util.Hashtable;
import java.util.Map;

import junit.framework.TestCase;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.utils.PlugDescription;

public class WFSFactoryTest extends TestCase {

	public static final String wfsClientImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericClient";
	public static final String wfsCapabilitiesImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericCapabilities";
	public static final String wfsFeatureTypeImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericFeatureType";
	public static final String wfsRequestKVPGetImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.KVPGetRequest";
	public static final String wfsRequestPostImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.PostRequest";
	public static final String wfsRequestSoapImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.SoapRequest";
	public static final String wfsQueryImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericQuery";
	public static final String wfsQueryResultImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericQueryResult";
	public static final String wfsFeatureImpl = "de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericFeature";

	/**
	 * Sets up the test fixture.
	 * (Called before every test case method.)
	 */
	@SuppressWarnings("unchecked")
	public static WFSFactory createFactory(PlugDescription desc) {

		// create the factory
		WFSFactory f = new WFSFactory();

		// add default values, if not already defined
		if (!desc.containsKey("wfsClientImpl")) {
			f.setClientImpl(wfsClientImpl);
		}
		else {
			f.setClientImpl(desc.get("wfsClientImpl").toString());
		}

		if (!desc.containsKey("wfsCapabilitiesImpl")) {
			f.setCapabilitiesImpl(wfsCapabilitiesImpl);
		}
		else {
			f.setCapabilitiesImpl(desc.get("wfsCapabilitiesImpl").toString());
		}

		if (!desc.containsKey("wfsFeatureTypeImpl")) {
			f.setFeatureTypeImpl(wfsFeatureTypeImpl);
		}
		else {
			f.setFeatureTypeImpl(desc.get("wfsFeatureTypeImpl").toString());
		}

		if (!desc.containsKey("wfsRequestImpl")) {
			Map<String, String> requestImpl = new Hashtable<String, String>();
			requestImpl.put(Operation.GET_CAPABILITIES.toString(), wfsRequestPostImpl);
			requestImpl.put(Operation.DESCRIBE_FEATURE_TYPE.toString(), wfsRequestPostImpl);
			requestImpl.put(Operation.GET_FEATURE.toString(), wfsRequestPostImpl);
			f.setRequestImpl(requestImpl);
		}
		else {
			f.setRequestImpl((Map<String, String>)desc.get("wfsRequestImpl"));
		}

		if (!desc.containsKey("wfsQueryImpl")) {
			f.setQueryImpl(wfsQueryImpl);
		}
		else {
			f.setQueryImpl(desc.get("wfsQueryImpl").toString());
		}

		if (!desc.containsKey("wfsQueryResultImpl")) {
			f.setQueryResultImpl(wfsQueryResultImpl);
		}
		else {
			f.setQueryResultImpl(desc.get("wfsQueryResultImpl").toString());
		}

		if (!desc.containsKey("wfsFeatureImpl")) {
			f.setFeatureImpl(wfsFeatureImpl);
		}
		else {
			f.setFeatureImpl(desc.get("wfsFeatureImpl").toString());
		}

		if (!desc.containsKey("mappings")) {
			Map<String, String> mappings = new Hashtable<String, String>();
			mappings.put("featureToId", "src/test/resources/mapping/waterlevel-feature_to_feature-id.js");
			mappings.put("featureToDocument", "src/main/resources/mapping/wfs-1.1.0_to_lucene-igc-1.0.3.js");
			f.setMappings(mappings);
		}
		else {
			f.setMappings((Map<String, String>)desc.get("mappings"));
		}

		f.configure(desc);

		return f;
	}

	public void testCreation() throws Exception {
		WFSFactory f = createFactory(new PlugDescription());

		// tests
		assertTrue("createClient returns a WFSClient implementation",
				f.createClient() instanceof WFSClient);

		assertTrue("createCapabilities returns a WFSCapabilities implementation",
				f.createCapabilities() instanceof WFSCapabilities);

		assertTrue("createFeatureType returns a WFSFeatureType implementation",
				f.createFeatureType() instanceof WFSFeatureType);

		assertTrue("createRequest returns a WFSRequest implementation",
				f.createRequest(Operation.GET_CAPABILITIES) instanceof WFSRequest);

		assertTrue("createQuery returns a WFSQuery implementation",
				f.createQuery() instanceof WFSQuery);

		assertTrue("createQueryResult returns a WFSQueryResult implementation",
				f.createQueryResult() instanceof WFSQueryResult);

		assertTrue("createFeature returns a WFSFeature implementation",
				f.createFeature() instanceof WFSFeature);
	}
}
