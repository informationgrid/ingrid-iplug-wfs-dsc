/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.index;

import de.ingrid.admin.JettyStarter;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.index.mapper.impl.ScriptedDocumentMapper;
import de.ingrid.iplug.wfs.dsc.om.WfsSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;
import junit.framework.TestCase;

public class MapperToIndexTest extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		new JettyStarter( false );
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		ScriptedDocumentMapper mapper = SimpleSpringBeanFactory.INSTANCE.getBean("recordMapper", ScriptedDocumentMapper.class);

		String testRecordId = "21212262e8a1112a80f26f18255da2e0";
		WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
		ElasticDocument doc = new ElasticDocument();
		try {
			mapper.map(new WfsSourceRecord(wfsRecord), doc);
		} catch (Throwable t) {
			System.out.println(t);
		}

		assertTrue("Lucene doc found.", doc != null);
		assertEquals(testRecordId, doc.get("t01_object.obj_id"));
		System.out.println(doc);
		assertEquals("RHEIN RUHRORT (km 780.8)", doc.get("title"));
		assertEquals("19.09.2011 16:15:00: 280.0cm", doc.get("summary"));
	}
}
