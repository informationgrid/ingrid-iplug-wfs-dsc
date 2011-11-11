package de.ingrid.iplug.wfs.dsc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class allows to execute scripts
 * @author ingo@wemove.com
 */
public class ScriptEngine {

	protected static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	protected static Map<String, javax.script.ScriptEngine> engines = new Hashtable<String, javax.script.ScriptEngine>();
	protected static Map<String, CompiledScript> compiledScripts = new Hashtable<String, CompiledScript>();

	/**
	 * Execute the given scripts with the given parameters
	 * @param scripts The script files
	 * @param parameters The parameters
	 * @param compile Boolean indicating whether to compile the script or not
	 * @return Map with the absolute paths of the scripts as keys and the execution results as values
	 * If an execution returns null, the result will not be added
	 * @throws Exception
	 */
	public static Map<String, Object> execute(File[] scripts, Map<String, Object> parameters, boolean compile) throws Exception {

		Map<Integer, Bindings> bindings = new Hashtable<Integer, Bindings>();
		Map<String, Object> results = new Hashtable<String, Object>();

		for (File script : scripts) {
			// get the engine for the script
			javax.script.ScriptEngine engine = getEngine(script);

			// initialize/get the bindings
			if (!bindings.containsKey(engine.hashCode())) {
				Bindings newBindings = engine.createBindings();
				newBindings.putAll(parameters);
				bindings.put(engine.hashCode(), newBindings);
			}
			Bindings curBindings = bindings.get(engine.hashCode());

			// execute the script
			CompiledScript compiledScript = null;
			Object result = null;
			if (compile && (compiledScript = getCompiledScript(script)) != null) {
				result = compiledScript.eval(curBindings);
			} else {
				result = engine.eval(new InputStreamReader(new FileInputStream(script)), curBindings);
			}
			if (result != null) {
				results.put(script.getAbsolutePath(), result);
			}
		}
		return results;
	}

	/**
	 * Get the compiled version of the given script
	 * @param script The script file
	 * @return CompiledScript
	 * @throws FileNotFoundException
	 * @throws ScriptException
	 */
	protected static CompiledScript getCompiledScript(File script) throws FileNotFoundException, ScriptException {
		String filename = script.getAbsolutePath();
		if (!compiledScripts.containsKey(filename)) {
			javax.script.ScriptEngine engine = getEngine(script);
			if (engine instanceof Compilable) {
				Compilable compilable = (Compilable)engine;
				CompiledScript compiledScript = compilable.compile(new InputStreamReader(new FileInputStream(script)));
				compiledScripts.put(filename, compiledScript);
			}
		}
		return compiledScripts.get(filename);
	}

	/**
	 * Get the scripting engine for the given script file
	 * @param script The script file
	 * @return javax.script.ScriptEngine
	 */
	protected static javax.script.ScriptEngine getEngine(File script) {
		String filename = script.getAbsolutePath();
		String extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
		if (!engines.containsKey(extension)) {
			javax.script.ScriptEngine engine = scriptEngineManager.getEngineByExtension(extension);
			engines.put(extension, engine);
		}
		return engines.get(extension);
	}
}
