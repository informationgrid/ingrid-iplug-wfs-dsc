/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.index.mapper;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.ScriptEngine;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
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

	private File mappingScript;
	private boolean compile = false;

	private static final Logger log = Logger.getLogger(WfsDocumentMapper.class);

	@Override
	public void map(SourceRecord record, Document doc) throws Exception {
		if (this.mappingScript == null) {
			log.error("Mapping script is not set!");
			throw new IllegalArgumentException("Mapping script is not set!");
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
			parameters.put("log", log);
			ScriptEngine.execute(this.mappingScript, parameters, this.compile);
		} catch (Exception e) {
			log.error("Error mapping source record to lucene document.", e);
			throw e;
		}
	}

	public void setMappingScript(File mappingScript) {
		this.mappingScript = mappingScript;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}
}
