/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.wfs.dsc.index;

import org.junit.jupiter.api.Test;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestConstants;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.index.mapper.impl.ScriptedDocumentMapper;
import de.ingrid.iplug.wfs.dsc.om.WfsSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZDMMapperToIndexTestLocal {

	/**
	 * @throws Exception
	 */
	@Test
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_zdm.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", "");
		factory.configure(desc);

		ScriptedDocumentMapper mapper = SimpleSpringBeanFactory.INSTANCE.getBean("recordMapper", ScriptedDocumentMapper.class);

		String[] testRecordIds = new String[] {
				"955299742e63f37188188b862290ee",
				"40a385f3895512bc48c9e2a95d92fea6",
				};

		for (String testRecordId : testRecordIds) {
			WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
			ElasticDocument doc = new ElasticDocument();
			try {
				mapper.map(new WfsSourceRecord(wfsRecord), doc);
			} catch (Throwable t) {
				System.out.println(t);
			}

			assertTrue(doc != null, "Lucene doc found.");
			assertEquals(testRecordId, doc.get("t01_object.obj_id"));
			System.out.println(doc);

			if (testRecordId.equals("955299742e63f37188188b862290ee")) {
				assertEquals("abfluss.78351782", doc.get("title"));
				assertEquals("EPSG:4326: 10.068015, 53.456649 / 10.068015, 53.456649", doc.get("summary"));
				assertEquals("Bunthaus Süd", doc.get("station_name"));
			}
			if (testRecordId.equals("40a385f3895512bc48c9e2a95d92fea6")) {
				assertEquals("NebenflussKm.26,8", doc.get("title"));
				assertEquals("EPSG:4326: 9.229423, 53.628519 / 9.229423, 53.628519", doc.get("summary"));
				assertEquals("Oste", doc.get("gewaesser"));
				assertEquals("26,8 - Oste - 024", doc.get("suchfeld"));
			}
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMapperFeatureTypes() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_zdm.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		ScriptedDocumentMapper mapper = SimpleSpringBeanFactory.INSTANCE.getBean("recordMapper", ScriptedDocumentMapper.class);

		String[] testRecordIds = new String[] {
				"70aa386652857405492ad7bf322b27",
				};

		for (String testRecordId : testRecordIds) {
			WFSFeatureType wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeatureType(), factory);
			ElasticDocument doc = new ElasticDocument();
			try {
				mapper.map(new WfsSourceRecord(wfsRecord), doc);
			} catch (Throwable t) {
				System.out.println(t);
			}

			assertTrue(doc != null, "Lucene doc found.");
			assertEquals(testRecordId, doc.get("t01_object.obj_id"));
			System.out.println(doc);

			if (testRecordId.equals("70aa386652857405492ad7bf322b27")) {
				assertEquals("70aa386652857405492ad7bf322b27", doc.get("t01_object.obj_id"));
				assertEquals("German Water Levels", doc.get("title"));
				assertEquals("German water levels of federal waterways from pegelonline.wsv.de. - "+TestConstants.PEGELONLINE_FEATURES+" Feature(s)", doc.get("summary"));
				assertEquals(TestConstants.PEGELONLINE_FEATURES, doc.get("number_of_features"));
			}
		}
	}
}
