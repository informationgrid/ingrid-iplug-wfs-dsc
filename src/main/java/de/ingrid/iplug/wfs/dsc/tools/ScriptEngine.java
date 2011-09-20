package de.ingrid.iplug.wfs.dsc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;

/**
 * This class allows to execute scripts
 * @author ingo@wemove.com
 */
public class ScriptEngine {

	protected static Map<String, ScriptingResource> instances = new Hashtable<String, ScriptingResource>();

	protected static class ScriptingResource {
		public javax.script.ScriptEngine engine;
		public CompiledScript compiledScript;
	}

	/**
	 * Execute the given script with the given parameters
	 * @param script The script file
	 * @param parameters The parameters
	 * @param compile Boolean indicating whether to compile the script or not
	 * @return Object
	 * @throws Exception
	 */
	public static Object execute(File script, Map<String, Object> parameters, boolean compile) throws Exception {

		Object result = null;

		// get the scripting resource
		ScriptingResource resource = getScriptingResource(script, compile);
		javax.script.ScriptEngine engine = resource.engine;
		CompiledScript compiledScript = resource.compiledScript;

		// add the parameters
		Bindings bindings = resource.engine.createBindings();
		bindings.putAll(parameters);

		// execute the script
		if (compiledScript != null) {
			result = compiledScript.eval(bindings);
		} else {
			result = engine.eval(new InputStreamReader(new FileInputStream(script)), bindings);
		}
		return result;
	}

	/**
	 * Get the ScriptEngine instance
	 * @param script The script file
	 * @return ScriptingResource instance
	 * @throws Exception
	 */
	protected static ScriptingResource getScriptingResource(File script, boolean compile) throws Exception {
		String filename = script.getAbsolutePath();
		if (!instances.containsKey(filename)) {
			initializeScripting(script, compile);
		}
		return instances.get(filename);
	}

	/**
	 * Initialize the ScriptEngine and compile the mapping script
	 * @param script The script file
	 * @param compile Boolean indicating whether to compile the script or not
	 * @throws Exception
	 */
	protected static void initializeScripting(File script, boolean compile) throws Exception {
		String filename = script.getAbsolutePath();
		String extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length());

		ScriptEngineManager mgr = new ScriptEngineManager();
		javax.script.ScriptEngine engine = mgr.getEngineByExtension(extension);
		CompiledScript compiledScript = null;
		if (compile) {
			if (engine instanceof Compilable) {
				Compilable compilable = (Compilable)engine;
				compiledScript = compilable.compile(new InputStreamReader(new FileInputStream(script)));
			}
		}
		ScriptEngine.ScriptingResource resource = new ScriptEngine.ScriptingResource();
		resource.engine = engine;
		resource.compiledScript = compiledScript;

		// register the scripting resource for later use
		instances.put(filename, resource);
	}
}
