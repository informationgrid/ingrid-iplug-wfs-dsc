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

package de.ingrid.iplug.wfs.dsc.wfsclient;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestConstants;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.utils.PlugDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WFSClientTestLocal {

	private WFSFactory factory;

	@BeforeEach
	public void setUp() {
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		this.factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		this.factory.configure(desc);
	}

	@Test
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
		assertFalse(cap.isSupportingOperations(new String[] { "MyFunction" }),
				"Server does not support MyFunction");

		assertTrue(cap.isSupportingOperations(new String[] { Operation.DESCRIBE_FEATURE_TYPE.toString(),
						Operation.GET_FEATURE.toString() }),
				"Server supports DescribeFeatureType and GetFeature");
	}

	@Test
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
		assertFalse(cap.isSupportingOperations(new String[] { "MyFunction" }),
				"Server does not support MyFunction");

		assertTrue(cap.isSupportingOperations(new String[] { Operation.DESCRIBE_FEATURE_TYPE.toString(),
						Operation.GET_FEATURE.toString() }),
				"Server supports DescribeFeatureType and GetFeature");
	}

	@Test
	public void testGetOperationUrl() throws Exception {

		// set up client
		WFSClient client = this.factory.createClient();

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		assertTrue("http://www.pegelonline.wsv.de:80/webservices/gis/aktuell/wfs".
				equals(cap.getOperationUrl(Operation.GET_FEATURE)),
				"GetFeature URL is correct");
	}

	@Test
	public void testGetTypeNames() throws Exception {

		// set up client
		WFSClient client = this.factory.createClient();

		// do request
		WFSCapabilities cap = client.getCapabilities();

		// tests
		String[] typeNames = cap.getFeatureTypeNames();
		/*~~(Recipe failed with an exception.
java.lang.ClassCastException: class org.openrewrite.java.tree.J$Binary cannot be cast to class org.openrewrite.java.tree.J$MethodInvocation (org.openrewrite.java.tree.J$Binary and org.openrewrite.java.tree.J$MethodInvocation are in unnamed module of loader org.codehaus.plexus.classworlds.realm.ClassRealm @2e78213c)
  org.openrewrite.java.testing.cleanup.AssertTrueEqualsToAssertEquals$1.visitMethodInvocation(AssertTrueEqualsToAssertEquals.java:76)
  org.openrewrite.java.testing.cleanup.AssertTrueEqualsToAssertEquals$1.visitMethodInvocation(AssertTrueEqualsToAssertEquals.java:51)
  org.openrewrite.java.tree.J$MethodInvocation.acceptJava(J.java:3611)
  org.openrewrite.java.tree.J.accept(J.java:63)
  org.openrewrite.TreeVisitor.visit(TreeVisitor.java:277)
  org.openrewrite.TreeVisitor.visitAndCast(TreeVisitor.java:356)
  org.openrewrite.java.JavaVisitor.visitRightPadded(JavaVisitor.java:1264)
  org.openrewrite.java.JavaVisitor.lambda$visitBlock$4(JavaVisitor.java:367)
  ...)~~>*/assertTrue(typeNames.length == 1 && typeNames[0].equals("gk:waterlevels"),
				"Expected type names found");
	}

	@Test
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
		assertEquals(TestConstants.PEGELONLINE_FEATURES, result.getNumberOfFeatures(), "Number of fetched features matches");
	}

	@Test
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
		assertEquals(70, result.getNumberOfFeatures(), "Number of fetched features matches");
	}
}
