/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.utils.PlugDescription;

public class WFSClientTest extends TestCase {

	String storedRecordId = null;


	public void testGetCapabilitiesKVPGet() throws Exception {

		TestServer server = TestServer.PEGELONLINE;

		// set up factory - KVPGet requests
		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", server.getCapUrl());
		Map<String, String> requestImpl = new Hashtable<String, String>();
		requestImpl.put(Operation.GET_CAPABILITIES.toString(), WFSFactoryTest.wfsRequestKVPGetImpl);
		desc.put("wfsRequestImpl", requestImpl);
		WFSFactory f = WFSFactoryTest.createFactory(desc);

		// set up client
		WFSClient client = f.createClient();
		client.configure(f);

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		assertFalse("Server does not support MyFunction",
				cap.isSupportingOperations(new String[] { "MyFunction" }));

		assertTrue("Server supports DescribeFeatureType and GetFeature",
				cap.isSupportingOperations(new String[] { Operation.DESCRIBE_FEATURE_TYPE.toString(),
						Operation.GET_FEATURE.toString() }));
	}

	public void testGetCapabilitiesPost() throws Exception {

		TestServer server = TestServer.PEGELONLINE;

		// set up factory - Soap requests
		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", server.getCapUrl());
		WFSFactory f = WFSFactoryTest.createFactory(desc);

		// set up client
		WFSClient client = f.createClient();
		client.configure(f);

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		assertFalse("Server does not support MyFunction",
				cap.isSupportingOperations(new String[] { "MyFunction" }));

		assertTrue("Server supports DescribeFeatureType and GetFeature",
				cap.isSupportingOperations(new String[] { Operation.DESCRIBE_FEATURE_TYPE.toString(),
						Operation.GET_FEATURE.toString() }));
	}

	public void testGetOperationUrl() throws Exception {

		TestServer server = TestServer.PEGELONLINE;

		// set up factory - Soap requests
		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", server.getCapUrl());
		WFSFactory f = WFSFactoryTest.createFactory(desc);

		// set up client
		WFSClient client = f.createClient();
		client.configure(f);

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		assertTrue("GetFeature URL is correct",
				"http://www.pegelonline.wsv.de:80/webservices/gis/aktuell/wfs".
				equals(cap.getOperationUrl(Operation.GET_FEATURE)));
	}

	public void testGetFeature() throws Exception {

		TestServer server = TestServer.PEGELONLINE;
		int recordCount = 482;

		// set up factory - Soap requests
		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", server.getCapUrl());
		WFSFactory f = WFSFactoryTest.createFactory(desc);

		// set up client
		WFSClient client = f.createClient();
		client.configure(f);

		// create the query
		WFSQuery query = server.getQuery(f.createQuery());

		query.setTypeName("gk:waterlevels");

		// do request
		WFSQueryResult result = client.getFeature(query);

		// tests
		assertTrue("Fetched "+recordCount+" records from the server",
				recordCount == result.getNumberOfFeatures());
	}

	public void testGetFeatureWithFilter() throws Exception {

		TestServer server = TestServer.PEGELONLINE;
		int recordCount = 57;

		// set up factory - Soap requests
		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", server.getCapUrl());
		WFSFactory f = WFSFactoryTest.createFactory(desc);

		// set up client
		WFSClient client = f.createClient();
		client.configure(f);

		// create the query

		String filterStr = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And>" +
				"<ogc:PropertyIsEqualTo>" +
				"<ogc:PropertyName>water</ogc:PropertyName><ogc:Literal>ELBE</ogc:Literal>" +
				"</ogc:PropertyIsEqualTo>" +
				"</ogc:And></ogc:Filter>";
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document filter = docBuilder.parse(new InputSource(new StringReader(filterStr)));
		WFSQuery query = server.getQuery(f.createQuery());

		query.setTypeName("gk:waterlevels");
		query.setFilter(filter);

		// do request
		WFSQueryResult result = client.getFeature(query);

		// tests
		assertTrue("Fetched "+recordCount+" records from the server",
				recordCount == result.getNumberOfFeatures());
	}
}
