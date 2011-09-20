/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;
import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.cache.impl.DefaultFileCache;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;

public class CacheTest extends TestCase {

	private final String cachePath = "./test_case_cache";
	private Cache cache = null;
	private WFSFactory factory = null;

	public void testPut() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id));
		assertTrue("The record exists in the filesystem.", file.exists());
	}

	public void testExists() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		Cache cache = this.setupCache();
		assertTrue("The record exists in the cache.", cache.isCached(id));
		assertFalse("The record does not exist in the cache.", cache.isCached("12345"));
	}

	public void testGet() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		Cache cache = this.setupCache();
		WFSFeature record = cache.getRecord(id);
		assertTrue("The cached record has the requested id.", id.equals(record.getId()));
	}

	public void testGetIds() throws Exception {

		String[] ids = new String[] {
				"60ddc975c8b9af7e8fa61ebe967e5eb7",
				"21212262e8a1112a80f26f18255da2e0"
		};

		this.putRecord(ids[0]);
		this.putRecord(ids[1]);

		Cache cache = this.setupCache();
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The first record is cached.", cachedIds.contains(ids[0]));
		assertTrue("The second record is cached.", cachedIds.contains(ids[1]));
		assertFalse("The record is not cached.", cachedIds.contains("12345"));
	}

	public void testRemoveRecord() throws Exception {

		String[] ids = new String[]{
				"60ddc975c8b9af7e8fa61ebe967e5eb7",
				"21212262e8a1112a80f26f18255da2e0"
		};

		this.putRecord(ids[0]);
		this.putRecord(ids[1]);

		Cache cache = this.setupCache();
		cache.removeRecord(ids[1]);
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The first record is cached.", cachedIds.contains(ids[0]));
		assertTrue("The second record is removed.", !cachedIds.contains(ids[1]));
	}

	public void testEncoding() throws Exception {

		String id = "21212262e8a1112a80f26f18255da2e0";

		this.putRecord(id);

		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id));
		assertTrue("The record exists in the filesystem.", file.exists());
		assertTrue("The record exists in the cache.", cache.isCached(id));

		// test get
		WFSFeature record = cache.getRecord(id);
		assertTrue("The cached record has the requested id.", id.equals(record.getId()));

		// test get ids
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The record is cached.", cachedIds.contains(id));

		// test remove
		cache.removeRecord(id);
		assertTrue("The record is removed from the cache.", !cache.isCached(id));
	}

	public void testRemoveAllRecords() throws Exception {

		String[] ids = new String[]{
				"21212262e8a1112a80f26f18255da2e0",
				"60ddc975c8b9af7e8fa61ebe967e5eb7"
		};

		this.putRecord(ids[0]);
		this.putRecord(ids[1]);

		Cache cache = this.setupCache();
		cache.removeAllRecords();
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("No files are cached.", cachedIds.size() == 0);
	}

	public void testTransactionModifyWithCommit() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		Cache cache = this.setupCache();

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String originalValue = TestUtil.getRecordValue(originalRecord);
		cache.putRecord(originalRecord);

		// start transaction
		Cache tmpCache = cache.startTransaction();
		WFSFeature modifiedRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String modifiedValue = "Modified Value "+System.currentTimeMillis();
		TestUtil.setRecordValue(modifiedRecord, modifiedValue);
		tmpCache.putRecord(modifiedRecord);

		// get original record while transaction is open
		WFSFeature originalRecordInTransaction = cache.getRecord(id);
		assertTrue("The cached record value has not changed, since the transaction is not committet.",
				originalValue.equals(TestUtil.getRecordValue(originalRecordInTransaction)));

		// commit the transaction
		tmpCache.commitTransaction();

		// get original record after transaction is committed
		WFSFeature originalRecordAfterTransaction = cache.getRecord(id);
		assertTrue("The cached record value is changed after the transaction is committet.",
				modifiedValue.equals(TestUtil.getRecordValue(originalRecordAfterTransaction)));

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionModifyWithRollback() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		Cache cache = this.setupCache();

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String originalTitle = TestUtil.getRecordValue(originalRecord);
		cache.putRecord(originalRecord);

		// start transaction
		Cache tmpCache = cache.startTransaction();
		WFSFeature modifiedRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String modifiedTitle = "Modified Value "+System.currentTimeMillis();
		TestUtil.setRecordValue(modifiedRecord, modifiedTitle);
		tmpCache.putRecord(modifiedRecord);

		// get original record while transaction is open
		WFSFeature originalRecordInTransaction = cache.getRecord(id);
		assertTrue("The cached record value has not changed, since the transaction is not committet.",
				originalTitle.equals(TestUtil.getRecordValue(originalRecordInTransaction)));

		// rollback the transaction
		tmpCache.rollbackTransaction();

		// get original record after transaction is committed
		WFSFeature originalRecordAfterTransaction = cache.getRecord(id);
		assertTrue("The cached record value is changed after the transaction is committet.",
				originalTitle.equals(TestUtil.getRecordValue(originalRecordAfterTransaction)));

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionRemoveWithCommit() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		Cache cache = this.setupCache();

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		cache.putRecord(originalRecord);

		// start transaction
		Cache tmpCache = cache.startTransaction();
		tmpCache.removeRecord(id);

		// check if the record is removed from the tmp cache
		assertTrue("The record is deleted from temp cache.", !tmpCache.isCached(id));

		// check if the record is still in the original cache
		assertTrue("The cached record still exists, since the transaction is not committet.", cache.isCached(id));

		// commit the transaction
		tmpCache.commitTransaction();

		// check if the original record is deleted after transaction is committed
		assertTrue("The cached record deleted after the transaction is committet.", !cache.isCached(id));

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionRemoveWithRollback() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		Cache cache = this.setupCache();

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		cache.putRecord(originalRecord);

		// start transaction
		Cache tmpCache = cache.startTransaction();
		tmpCache.removeRecord(id);

		// check if the record is removed from the tmp cache
		assertTrue("The record is deleted from temp cache.", !tmpCache.isCached(id));

		// check if the record is still in the original cache
		assertTrue("The cached record still exists, since the transaction is not committet.", cache.isCached(id));

		// rollback the transaction
		tmpCache.rollbackTransaction();

		// check if the original record is deleted after transaction is committed
		assertTrue("The cached record still exists after the transaction is committet.", cache.isCached(id));

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testLastCommitDate() throws Exception {

		Cache cache = this.setupCache();

		// start transaction
		Cache tmpCache = cache.startTransaction();

		// commit the transaction
		tmpCache.commitTransaction();

		// check if commit date is today
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue("The comit date is today.", df.format(cache.getLastCommitDate()).equals(df.format(new Date())));
	}

	public void testInitialCache() throws Exception {

		Cache cache = this.setupCache();

		// start transaction
		Cache tmpCache = cache.startTransaction();

		// check if the tmp cache instance is not the initial cache instance
		assertTrue("The temp cache is not the initial cache.", tmpCache != cache);

		// check if the tmp cache's initial instance is the initial cache instance
		assertTrue("The temp cache's initial instance is the initial cache.", tmpCache.getInitialCache() == cache);

		// rollback the transaction
		tmpCache.rollbackTransaction();
	}

	public void testGetOriginalResponse() throws Exception {
		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String xml = StringUtils.nodeToString(originalRecord.getOriginalResponse());
		assertTrue("The String '53.568523252032215 9.703319238921454' is in the transformed original response string.", xml.indexOf("53.568523252032215 9.703319238921454") > -1);

	}

	/**
	 * Helper methods
	 */

	@Override
	protected void setUp() {
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		this.factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		this.factory.configure(desc);
	}

	@Override
	protected void tearDown() {
		// delete cache
		TestUtil.deleteDirectory(new File(this.cachePath));
	}

	private Cache setupCache() {
		if (this.cache == null) {
			DefaultFileCache cache = new DefaultFileCache();
			cache.configure(this.factory);
			cache.setCachePath(this.cachePath);
			this.cache = cache;
		}
		return this.cache;
	}

	private void putRecord(String id) throws Exception {
		Cache cache = this.setupCache();
		WFSFeature record = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		cache.putRecord(record);
	}
}
