/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.index.mapper;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.ScriptEngine;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Script based source record to lucene document mapping. This class takes a
 * {@link File} as parameter to specify the mapping script. The script
 * engine will be automatically determined from the extension of the mapping
 * script.
 * <p />
 * If the {@link compile} parameter is set to true, the script is compiled, if
 * the ScriptEngine supports compilation.
 * 
 * @author ingo@wemove.com
 * 
 */
public class WfsDocumentMapper implements IRecordMapper {

	private File[] mappingScripts;
	private boolean compile = false;

	private static final Logger log = Logger.getLogger(WfsDocumentMapper.class);

	@Override
	public void map(SourceRecord record, ElasticDocument doc) throws Exception {
		if (this.mappingScripts == null) {
			log.error("Mapping scripts are not set!");
			throw new IllegalArgumentException("Mapping scripts are not set!");
		}

		if (!(record instanceof WfsCacheSourceRecord)) {
			log.error("Source Record is not a WfsCacheSourceRecord!");
			throw new IllegalArgumentException("Source Record is not a WfsCacheSourceRecord!");
		}

		WFSFeature wfsRecord = (WFSFeature)record.get(WfsCacheSourceRecord.WFS_RECORD);
		XPathUtils xPathUtils = new XPathUtils(wfsRecord.getNamespaceContext());

		try {
			Map<String, Object> parameters = new Hashtable<String, Object>();
			parameters.put("wfsRecord", wfsRecord);
			parameters.put("document", doc);
			parameters.put("xPathUtils", xPathUtils);
			parameters.put("javaVersion", System.getProperty( "java.version" ));
			parameters.put("log", log);
			ScriptEngine.execute(this.mappingScripts, parameters, this.compile);
		} catch (Exception e) {
			log.error("Error mapping source record to lucene document.", e);
			throw e;
		}
	}

	public void setMappingScripts(File[] mappingScripts) {
		this.mappingScripts = mappingScripts;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}
}
