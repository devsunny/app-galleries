package com.asksunny.batch.graph;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EmbeddedScriptWorkflowTask extends AbstractWorkflowTask {

	private String scriptEngineName = "nashorn";
	private String scriptSource = "print(\"Please provide script source\")";

	public EmbeddedScriptWorkflowTask() {
	}

	
	protected void executeTask() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(getScriptEngineName());
		engine.getContext().setAttribute("flowContext", getFlowContext(), ScriptContext.ENGINE_SCOPE);
		engine.getContext().setAttribute("cliContext", getFlowContext().getCliArgument(), ScriptContext.ENGINE_SCOPE);
		engine.eval(getScriptSource());
	}

	public String getScriptEngineName() {
		return scriptEngineName;
	}

	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}

	public String getScriptSource() {
		return scriptSource;
	}

	public void setScriptSource(String scriptSource) {
		this.scriptSource = scriptSource;
	}

}
