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

import java.util.ArrayList;
import java.util.List;

import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;
import junit.framework.TestCase;

public class WfsDscDocumentProducerTestLocal extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testAll() throws Exception {

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");

		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);
		factory.configure(desc);

		IDocumentProducer documentProducer = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_DOCUMENT_PRODUCER, IDocumentProducer.class);
		documentProducer.configure(desc);

		List<ElasticDocument> docs = new ArrayList<>();
		while (documentProducer.hasNext()) {
			ElasticDocument doc = documentProducer.next();
			docs.add(doc);
		}
		assertEquals("Number of mapped documents matches", 535, docs.size());
	}
}
