/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import org.junit.jupiter.api.Test;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestConstants;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.PlugDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateJobTestLocal {

	@Test
	public void testExecute() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		UpdateJob job = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_UPDATE_JOB, UpdateJob.class);
		job.execute();

		Cache cache = job.getCache();
		assertEquals(TestConstants.PEGELONLINE_FEATURES, cache.getCachedRecordIds().size(), "Number of cached features matches");
	}
}
