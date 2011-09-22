package de.ingrid.iplug.wfs.dsc.record;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.record.mapper.CreateIdfMapper;
import de.ingrid.iplug.wfs.dsc.record.mapper.WfsIdfMapper;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.xml.XMLUtils;

public class MapperToIngridTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	public void testMapper() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		CreateIdfMapper createIdfMapper = new CreateIdfMapper();
		WfsIdfMapper wfsIdfMapper = new WfsIdfMapper();
		wfsIdfMapper.setCompile(false);
		wfsIdfMapper.setMappingScript(new File("src/main/resources/mapping/pegelonline-wfs-1.1.0_to_idf-1.0.0.js"));

		String testRecordId = "21212262e8a1112a80f26f18255da2e0";
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
		assertTrue(documentString.contains("<h1>RHEIN RUHRORT (km 780.8)</h1>"));
		assertTrue(documentString.contains("<p>19.09.2011 16:15:00: 280.0cm</p>"));
	}
}
