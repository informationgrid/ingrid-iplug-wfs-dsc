package de.ingrid.iplug.wfs.dsc.index;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.index.mapper.WfsDocumentMapper;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;

public class MapperToIndexTest extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		WfsDocumentMapper mapper = new WfsDocumentMapper();
		mapper.setCompile(false);
		mapper.setMappingScript(new File("src/main/resources/mapping/pegelonline-wfs-1.1.0_to_lucene-igc-1.0.3.js"));

		String testRecordId = "21212262e8a1112a80f26f18255da2e0";
		WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
		Document doc = new Document();
		try {
			mapper.map(new WfsCacheSourceRecord(wfsRecord), doc);
		} catch (Throwable t) {
			System.out.println(t);
		}

		assertTrue("Lucene doc found.", doc != null);
		assertEquals(testRecordId, doc.get("t01_object.obj_id"));
		assertEquals("RHEIN RUHRORT (km 780.8)", doc.get("title"));
		assertEquals("2011-09-19T15:15:00+01:00: 280.0cm", doc.get("summary"));
	}
}
