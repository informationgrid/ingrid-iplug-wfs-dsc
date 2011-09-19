/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.index.mapper;

import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;

/**
 * Script based source record to lucene document mapping. This class takes a
 * {@link Resource} as parameter to specify the mapping script. The scripting
 * engine will be automatically determined from the extension of the mapping
 * script.
 * <p />
 * If the {@link compile} parameter is set to true, the script is compiled, if
 * the ScriptEngine supports compilation.
 * 
 * @author joachim@wemove.com
 * 
 */
public class ScriptedDocumentMapper implements IRecordMapper {

	private Resource mappingScript;

	private boolean compile = false;

	private ScriptEngine engine;
	private CompiledScript compiledScript;

	private static final Logger log = Logger
			.getLogger(ScriptedDocumentMapper.class);

	@Override
	public void map(SourceRecord record, Document doc) throws Exception {
		if (this.mappingScript == null) {
			log.error("Mapping script is not set!");
			throw new IllegalArgumentException("Mapping script is not set!");
		}

		if (!(record instanceof WfsCacheSourceRecord)) {
			log.error("Source Record is not a WfsCacheSourceRecord!");
			throw new IllegalArgumentException(
					"Source Record is not a WfsCacheSourceRecord!");
		}

		WFSFeature wfsRecord = (WFSFeature) (((WfsCacheSourceRecord) record).get(WfsCacheSourceRecord.WFS_RECORD));

		try {
			if (this.engine == null) {
				String scriptName = this.mappingScript.getFilename();
				String extension = scriptName.substring(scriptName
						.lastIndexOf('.') + 1, scriptName.length());
				ScriptEngineManager mgr = new ScriptEngineManager();
				this.engine = mgr.getEngineByExtension(extension);
				if (this.compile) {
					if (this.engine instanceof Compilable) {
						Compilable compilable = (Compilable) this.engine;
						this.compiledScript = compilable
								.compile(new InputStreamReader(this.mappingScript
										.getInputStream()));
					}
				}
			}
			Bindings bindings = this.engine.createBindings();
			bindings.put("wfsRecord", wfsRecord);
			bindings.put("document", doc);
			bindings.put("log", log);
			if (this.compiledScript != null) {
				this.compiledScript.eval(bindings);
			} else {
				this.engine.eval(new InputStreamReader(this.mappingScript
						.getInputStream()), bindings);
			}
		} catch (Exception e) {
			log.error("Error mapping source record to lucene document.", e);
			throw e;
		}
	}

	public Resource getMappingScript() {
		return this.mappingScript;
	}

	public void setMappingScript(Resource mappingScript) {
		this.mappingScript = mappingScript;
	}

	public boolean isCompile() {
		return this.compile;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}

}
