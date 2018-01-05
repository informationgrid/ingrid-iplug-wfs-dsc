/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.tools.FileUtils;
import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.xml.XMLSerializer;
import de.ingrid.utils.xpath.XPathUtils;

public class TestUtil {

	protected final static Log log = LogFactory.getLog(TestUtil.class);
	protected static final XPathUtils xPathUtils = new XPathUtils(new WfsNamespaceContext());

	/**
	 * File related methods
	 */

	public static final boolean deleteDirectory(File directory) {
		if (!directory.exists()) {
			return true;
		}
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					if (!deleteDirectory(file)) {
						return false;
					}
				} else {
					if (!file.delete()) {
						return false;
					}
				}
			}
		}
		return directory.delete();
	}

	public static void copy(File sourceFile, File targetFile) {
		targetFile.getParentFile().mkdirs();
		try {
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(targetFile);
			copy(in, out, true);
		} catch (IOException e) {
			throw new RuntimeException("could not copy " + sourceFile + " to "
					+ targetFile, e);
		}
	}

	public static void copy(InputStream inputStream, OutputStream outputStream,
			boolean close) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, len);
		}
		if (close) {
			inputStream.close();
			outputStream.close();
		}
	}

	public static PlugDescription getPlugDescription() throws IOException {
		// read the PlugDescription
		File descFile = new File("src/conf/plugdescription_test_wfs.xml");
		XMLSerializer serializer = new XMLSerializer();
		serializer.aliasClass(PlugDescription.class.getName(), PlugDescription.class);
		PlugDescription desc = (PlugDescription)serializer.deSerialize(descFile);
		return desc;
	}

	/**
	 * Record related methods
	 */

	private final static String dataFolder = "./src/test/resources/test_records";

	public static Set<String> getRecordIds() {
		Set<String> recordIds = new HashSet<String>();

		// read all record ids from the storage
		File dataLocation = new File(dataFolder);
		File[] files = dataLocation.listFiles();
		if (files != null) {
			for (File file2 : files) {
				File file = file2;
				if (!file.isDirectory() && file.getName().endsWith(".xml")) {
					String filename = file.getName();
					recordIds.add(filename.substring(0, filename.lastIndexOf(".")));
				}
			}
		}
		return recordIds;
	}

	public static WFSFeature getRecord(String id, WFSFeature record, WFSFactory factory) {
		try {
			log.debug("getting Record '"+id+"'");
			return getRecordNode(id, record, factory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getRecordValue(WFSFeature record) {
		return xPathUtils.getString(record.getOriginalResponse(), "//*");
	}

	public static void setRecordValue(WFSFeature record, String value) {
		Node valueNode = xPathUtils.getNode(record.getOriginalResponse(), "//*");
		valueNode.setTextContent(value);
	}

	private static WFSFeature getRecordNode(String id, WFSFeature record, WFSFactory factory) throws Exception {

		File file = new File(dataFolder+"/"+FileUtils.encodeFileName(id)+".xml");
		StringBuilder content = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			while((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			input.close();
			input = null;

			log.debug("content: \n"+content.toString());
			Document document = StringUtils.stringToDocument(content.toString());
			record.initialize(document.getFirstChild());
			return record;
		}
		finally {
			if (input != null) {
				input.close();
			}
		}
	}
}
