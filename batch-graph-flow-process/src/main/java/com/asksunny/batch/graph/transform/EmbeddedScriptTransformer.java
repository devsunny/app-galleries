package com.asksunny.batch.graph.transform;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.ScriptLanguage;

public class EmbeddedScriptTransformer implements RecordTransformer {

	private ScriptLanguage scriptLanguage = ScriptLanguage.JAVASCRIPT;
	private String scriptSource;
	public static final String TRANSFORMEE_KEY = "transformee";
	public static final String FLOWCONTEXT_KEY = "flowContext";

	private BatchFlowContext flowContext = null;
	private CompiledScript compiledScript = null;
	private ScriptEngine engine = null;

	public EmbeddedScriptTransformer() {

	}

	@Override
	public void init(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	@Override
	public Object transform(Object transformee) throws Exception {
		if (compiledScript == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
			engine = manager.getEngineByName(getScriptEngineName());
			Compilable compilingEngine = (Compilable) engine;
			compiledScript = compilingEngine.compile(getScriptSource());
		}
		Bindings bindings = engine.createBindings();
		bindings.put("flowContext", getFlowContext());
		bindings.put("cliContext", getFlowContext().getCliArgument());
		bindings.put(TRANSFORMEE_KEY, transformee);
		compiledScript.eval(bindings);
		Object ret = compiledScript.getEngine().get(TRANSFORMEE_KEY);
		return ret;
	}

	public String getScriptEngineName() {
		switch (scriptLanguage) {
		case JAVASCRIPT:
			return "Nashorn";
		case GROOVY:
			return "groovy";
		case PYTHON:
			return "python";
		case RUBY:
			return "ruby";
		default:
			return "Nashorn";
		}
	}

	public ScriptLanguage getScriptLanguage() {
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguage scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}

	public String getScriptSource() {
		return scriptSource;
	}

	public void setScriptSource(String scriptSource) {
		this.scriptSource = scriptSource;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

	public void setCompiledScript(CompiledScript compiledScript) {
		this.compiledScript = compiledScript;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
