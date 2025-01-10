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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import de.ingrid.iplug.wfs.dsc.ConfigurationKeys;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.ingrid.iplug.wfs.dsc.TestServer;
import de.ingrid.iplug.wfs.dsc.TestUtil;
import de.ingrid.iplug.wfs.dsc.cache.impl.DefaultFileCache;
import de.ingrid.iplug.wfs.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.PlugDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheTest {

	private final String cachePath = "./test_case_cache";
	private Cache cache = null;
	private WFSFactory factory = null;

	@Test
	public void testPut() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id));
		assertTrue(file.exists(), "The record exists in the filesystem.");
	}

	@Test
	public void testExists() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		Cache cache = this.setupCache();
		assertTrue(cache.isCached(id), "The record exists in the cache.");
		assertFalse(cache.isCached("12345"), "The record does not exist in the cache.");
	}

	@Test
	public void testGet() throws Exception {

		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		this.putRecord(id);

		Cache cache = this.setupCache();
		WFSFeature record = cache.getRecord(id);
		assertEquals(id,record.getId(),"The cached record has the requested id.");
	}

	@Test
	public void testGetIds() throws Exception {

		String[] ids = new String[] {
				"60ddc975c8b9af7e8fa61ebe967e5eb7",
				"21212262e8a1112a80f26f18255da2e0"
		};

		this.putRecord(ids[0]);
		this.putRecord(ids[1]);

		Cache cache = this.setupCache();
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue(cachedIds.contains(ids[0]), "The first record is cached.");
		assertTrue(cachedIds.contains(ids[1]), "The second record is cached.");
		assertFalse(cachedIds.contains("12345"), "The record is not cached.");
	}

	@Test
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
		assertTrue(cachedIds.contains(ids[0]), "The first record is cached.");
		assertFalse(cachedIds.contains(ids[1]),"The second record is removed.");
	}

	@Test
	public void testEncoding() throws Exception {

		String id = "21212262e8a1112a80f26f18255da2e0";

		this.putRecord(id);

		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id));
		assertTrue(file.exists(), "The record exists in the filesystem.");
		assertTrue(cache.isCached(id), "The record exists in the cache.");

		// test get
		WFSFeature record = cache.getRecord(id);
		assertEquals(id,record.getId(),"The cached record has the requested id.");

		// test get ids
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue(cachedIds.contains(id), "The record is cached.");

		// test remove
		cache.removeRecord(id);
		assertFalse(cache.isCached(id),"The record is removed from the cache.");
	}

	@Test
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
		assertEquals(cachedIds.size(),0,"No files are cached.");
	}

	@Test
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
		assertEquals(originalValue,TestUtil.getRecordValue(originalRecordInTransaction),"The cached record value has not changed, since the transaction is not committet.");

		// commit the transaction
		tmpCache.commitTransaction();

		// get original record after transaction is committed
		WFSFeature originalRecordAfterTransaction = cache.getRecord(id);
		assertEquals(modifiedValue,TestUtil.getRecordValue(originalRecordAfterTransaction),"The cached record value is changed after the transaction is committet.");

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertFalse(tmpPath.exists(),"The temporary cache is deleted.");
	}

	@Test
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
		assertEquals(originalTitle,TestUtil.getRecordValue(originalRecordInTransaction),"The cached record value has not changed, since the transaction is not committet.");

		// rollback the transaction
		tmpCache.rollbackTransaction();

		// get original record after transaction is committed
		WFSFeature originalRecordAfterTransaction = cache.getRecord(id);
		assertEquals(originalTitle,TestUtil.getRecordValue(originalRecordAfterTransaction),"The cached record value is changed after the transaction is committet.");

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertFalse(tmpPath.exists(),"The temporary cache is deleted.");
	}

	@Test
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
		assertFalse(tmpCache.isCached(id),"The record is deleted from temp cache.");

		// check if the record is still in the original cache
		assertTrue(cache.isCached(id), "The cached record still exists, since the transaction is not committet.");

		// commit the transaction
		tmpCache.commitTransaction();

		// check if the original record is deleted after transaction is committed
		assertFalse(cache.isCached(id),"The cached record deleted after the transaction is committet.");

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertFalse(tmpPath.exists(),"The temporary cache is deleted.");
	}

	@Test
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
		assertFalse(tmpCache.isCached(id),"The record is deleted from temp cache.");

		// check if the record is still in the original cache
		assertTrue(cache.isCached(id), "The cached record still exists, since the transaction is not committet.");

		// rollback the transaction
		tmpCache.rollbackTransaction();

		// check if the original record is deleted after transaction is committed
		assertTrue(cache.isCached(id), "The cached record still exists after the transaction is committet.");

		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertFalse(tmpPath.exists(),"The temporary cache is deleted.");
	}

	@Test
	public void testLastCommitDate() throws Exception {

		Cache cache = this.setupCache();

		// start transaction
		Cache tmpCache = cache.startTransaction();

		// commit the transaction
		tmpCache.commitTransaction();

		// check if commit date is today
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		assertEquals(df.format(cache.getLastCommitDate()),df.format(new Date()),"The comit date is today.");
	}

	@Test
	public void testInitialCache() throws Exception {

		Cache cache = this.setupCache();

		// start transaction
		Cache tmpCache = cache.startTransaction();

		// check if the tmp cache instance is not the initial cache instance
		assertTrue(tmpCache != cache, "The temp cache is not the initial cache.");

		// check if the tmp cache's initial instance is the initial cache instance
		assertEquals(tmpCache.getInitialCache(),cache,"The temp cache's initial instance is the initial cache.");

		// rollback the transaction
		tmpCache.rollbackTransaction();
	}

	@Test
	public void testGetOriginalResponse() throws Exception {
		String id = "60ddc975c8b9af7e8fa61ebe967e5eb7";

		// create original set
		WFSFeature originalRecord = TestUtil.getRecord(id, this.factory.createFeature(), this.factory);
		String xml = StringUtils.nodeToString(originalRecord.getOriginalResponse().get(0));
		assertTrue(xml.indexOf("53.568523252032215 9.703319238921454") > -1, "The String '53.568523252032215 9.703319238921454' is in the transformed original response string.");
	}

	/**
	 * Helper methods
	 */

	@BeforeEach
	public void setUp() {
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_pegelonline.xml");
		this.factory = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.WFS_FACTORY, WFSFactory.class);

		PlugDescription desc = new PlugDescription();
		desc.put("serviceUrl", TestServer.PEGELONLINE.getCapUrl());
		this.factory.configure(desc);
	}

	@AfterEach
	public void tearDown() {
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
