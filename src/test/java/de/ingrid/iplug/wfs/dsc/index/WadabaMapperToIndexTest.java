package de.ingrid.iplug.wfs.dsc.index;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.index.mapper.WfsDocumentMapper;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;

public class WadabaMapperToIndexTest extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_wadaba.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", "");
		factory.configure(desc);

		WfsDocumentMapper mapper = SimpleSpringBeanFactory.INSTANCE.getBean("recordMapper", WfsDocumentMapper.class);

		String testRecordId = "0129b32b06679f8f71326d65669deb5";
		WFSFeature wfsRecord = TestUtil.getRecord(testRecordId, factory.createFeature(), factory);
		Document doc = new Document();
		try {
			mapper.map(new WfsCacheSourceRecord(wfsRecord), doc);
		} catch (Throwable t) {
			System.out.println(t);
		}

		assertTrue("Lucene doc found.", doc != null);
		assertEquals(testRecordId, doc.get("t01_object.obj_id"));
		System.out.println(doc);
		assertEquals("Flussbuhne Nr.2, km 606,802 re.Ufer", doc.get("title"));
		assertEquals("Flussbuhnen - DE_DHDN_3GK2_NI100", doc.get("summary"));
		assertEquals("1222526130", doc.get("WADABA_ID"));
	}
}
