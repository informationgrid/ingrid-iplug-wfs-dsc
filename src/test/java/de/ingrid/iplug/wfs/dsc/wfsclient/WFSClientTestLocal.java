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

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestConstants;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.utils.PlugDescription;
import junit.framework.TestCase;

public class WFSClientTestLocal extends TestCase {

	private WFSFactory factory;

	@Override
	protected void setUp() {
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		this.factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		this.factory.configure(desc);
	}

	public void testGetCapabilitiesKVPGet() throws Exception {

		// set up factory - KVPGet requests
		Map<String, String> requestImpl = new Hashtable<String, String>();
		requestImpl.put(Operation.GET_CAPABILITIES.toString(), WFSFactoryTest.wfsRequestKVPGetImpl);
		this.factory.setRequestImpl(requestImpl);

		// set up client
		WFSClient client = this.factory.createClient();

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

		// set up factory - Post requests
		Map<String, String> requestImpl = new Hashtable<String, String>();
		requestImpl.put(Operation.GET_CAPABILITIES.toString(), WFSFactoryTest.wfsRequestPostImpl);
		this.factory.setRequestImpl(requestImpl);

		// set up client
		WFSClient client = this.factory.createClient();

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

		// set up client
		WFSClient client = this.factory.createClient();

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		assertTrue("GetFeature URL is correct",
				"http://www.pegelonline.wsv.de:80/webservices/gis/aktuell/wfs".
				equals(cap.getOperationUrl(Operation.GET_FEATURE)));
	}

	public void testGetTypeNames() throws Exception {

		// set up client
		WFSClient client = this.factory.createClient();

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		String[] typeNames = cap.getFeatureTypeNames();
		assertTrue("Expected type names found",
				typeNames.length == 1 && typeNames[0].equals("gk:waterlevels"));
	}

	public void testGetFeature() throws Exception {

		TestServer server = TestServer.PEGELONLINE;

		// set up client
		WFSClient client = this.factory.createClient();

		// create the query
		WFSQuery query = server.getQuery(this.factory.createQuery());

		query.setTypeName("gk:waterlevels");

		// do request
		WFSQueryResult result = client.getFeature(query);

		// tests
		assertEquals("Number of fetched features matches", TestConstants.PEGELONLINE_FEATURES, result.getNumberOfFeatures());
	}

	public void testGetFeatureWithFilter() throws Exception {

		TestServer server = TestServer.PEGELONLINE;

		// set up client
		WFSClient client = this.factory.createClient();

		// create the query
		String filterStr = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And>" +
				"<ogc:PropertyIsEqualTo>" +
				"<ogc:PropertyName>water</ogc:PropertyName><ogc:Literal>ELBE</ogc:Literal>" +
				"</ogc:PropertyIsEqualTo>" +
				"</ogc:And></ogc:Filter>";
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document filter = docBuilder.parse(new InputSource(new StringReader(filterStr)));
		WFSQuery query = server.getQuery(this.factory.createQuery());

		query.setTypeName("gk:waterlevels");
		query.setFilter(filter);

		// do request
		WFSQueryResult result = client.getFeature(query);

		// tests
		assertEquals("Number of fetched features matches", 70, result.getNumberOfFeatures());
	}
}
