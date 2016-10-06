package com.asksunny.batch.graph;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EmbeddedScriptWorkflowTask extends AbstractWorkflowTask {

	private ScriptLanguage scriptLanguage = ScriptLanguage.JAVASCRIPT;

	private String scriptSource = "print(\"Please provide script source\")";
	private CompiledScript compiledScript = null;
	private ScriptEngine engine = null;

	public EmbeddedScriptWorkflowTask() {
	}

	protected void executeTask() throws Exception {
		if (compiledScript == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
			engine = manager.getEngineByName(getScriptEngineName());
			Compilable compilingEngine = (Compilable) engine;
			compiledScript = compilingEngine.compile(getScriptSource());
		}
		Bindings bindings = engine.createBindings();
		bindings.put("flowContext", getFlowContext());
		bindings.put("cliContext", getFlowContext().getCliArgument());
		compiledScript.eval(bindings);
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

	public String getScriptSource() {
		return scriptSource;
	}

	public void setScriptSource(String scriptSource) {
		this.scriptSource = scriptSource;

	}

	public ScriptLanguage getScriptLanguage() {
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguage scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}

}
