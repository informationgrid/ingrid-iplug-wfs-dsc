/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import junit.framework.TestCase;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.PlugDescription;

public class UpdateJobTestLocal extends TestCase {

	public void testExecute() throws Exception {

		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		WFSFactory factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		factory.configure(desc);

		UpdateJob job = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_UPDATE_JOB, UpdateJob.class);
		job.execute();

		Cache cache = job.getCache();
		assertEquals(487, cache.getCachedRecordIds().size());
	}
}