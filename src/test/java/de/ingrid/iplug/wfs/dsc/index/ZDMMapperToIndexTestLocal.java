/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.index.mapper.WfsDocumentMapper;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;
import junit.framework.TestCase;

public class ZDMMapperToIndexTestLocal extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_zdm.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", "");
		factory.configure(desc);

		WfsDocumentMapper mapper = SimpleSpringBeanFactory.INSTANCE.getBean("recordMapper", WfsDocumentMapper.class);

		String[] testRecordIds = new String[] {
				"955299742e63f37188188b862290ee",
				"40a385f3895512bc48c9e2a95d92fea6",
				};

		for (String testRecordId : testRecordIds) {
			WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
			ElasticDocument doc = new ElasticDocument();
			try {
				mapper.map(new WfsCacheSourceRecord(wfsRecord), doc);
			} catch (Throwable t) {
				System.out.println(t);
			}
			
			assertTrue("Lucene doc found.", doc != null);
			assertEquals(testRecordId, doc.get("t01_object.obj_id"));
			System.out.println(doc);

			if (testRecordId.equals("955299742e63f37188188b862290ee")) {
				assertEquals("abfluss.78351782", doc.get("title"));
				assertEquals("EPSG:4326: 53.456649, 10.068015 / 53.456649, 10.068015", doc.get("summary"));
				assertEquals("Bunthaus Süd", doc.get("station_name"));				
			}
			if (testRecordId.equals("40a385f3895512bc48c9e2a95d92fea6")) {
				assertEquals("NebenflussKm.26,8", doc.get("title"));
				assertEquals("EPSG:4326: 53.628519, 9.229423 / 53.628519, 9.229423", doc.get("summary"));
				assertEquals("Oste", doc.get("gewaesser"));				
				assertEquals("26,8 - Oste - 024", doc.get("suchfeld"));				
			}
		}
	}
}
