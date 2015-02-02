/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.record;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.record.mapper.CreateIdfMapper;
import de.ingrid.iplug.wfs.dsc.record.mapper.WfsIdfMapper;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.xml.XMLUtils;

public class ZDMMapperToIngridTestLocal extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_zdm.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", "");
		factory.configure(desc);

		CreateIdfMapper createIdfMapper = SimpleSpringBeanFactory.INSTANCE.getBean("createIdfMapper", CreateIdfMapper.class);
		WfsIdfMapper wfsIdfMapper = SimpleSpringBeanFactory.INSTANCE.getBean("idfMapper", WfsIdfMapper.class);

		String testRecordId = "955299742e63f37188188b862290ee";
		WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		org.w3c.dom.Document idfDoc = docBuilder.newDocument();
		try {
			createIdfMapper.map(new WfsCacheSourceRecord(wfsRecord), idfDoc);
			wfsIdfMapper.map(new WfsCacheSourceRecord(wfsRecord), idfDoc);
		} catch (Throwable t) {
			System.out.println(t);
		}

		assertTrue("Idf found.", idfDoc.hasChildNodes());
		String documentString = XMLUtils.toString(idfDoc);
		System.out.println(documentString);
		assertTrue(documentString.contains("<h1>abfluss.78351782</h1>"));
		assertTrue(documentString.contains("<p>EPSG:4326: 53.456649, 10.068015 / 53.456649, 10.068015</p>"));
		assertTrue(documentString.contains("<li>STATION_NAME: Bunthaus Süd</li>"));
	}
}
