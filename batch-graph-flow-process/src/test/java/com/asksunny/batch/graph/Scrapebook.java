package com.asksunny.batch.graph;

import java.util.List;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class Scrapebook {

	public Scrapebook() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.setProperty("python.console.encoding",  "UTF-8");
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> sefs = manager.getEngineFactories();
		for (ScriptEngineFactory scriptEngineFactory : sefs) {
			System.out.println(scriptEngineFactory.getEngineName());
		}
		ScriptEngine engine = manager.getEngineByName("Nashorn");
		System.out.println(engine instanceof Compilable);
		
		engine = manager.getEngineByName("groovy");
		System.out.println(engine instanceof Compilable);		
		
		engine = manager.getEngineByName("ruby");
		System.out.println(engine instanceof Compilable);
		
		engine = manager.getEngineByName("python");
		System.out.println(engine instanceof Compilable);

	}

}
