package com.asksunny.batch.graph.transform;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.asksunny.batch.graph.BatchFlowContext;

/**
 * This stateful rule engine should be used with memory foot print in mind.
 * 
 * @author SunnyLiu
 *
 */
public class JBossDroolsStatefulTransformer implements RecordTransformer {
	private KieContainer kc;
	private KieSession ksession;
	private String ruleName;
	private BatchFlowContext flowContext;

	@Override
	public void init(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
		kc = KieServices.Factory.get().getKieClasspathContainer();
		ksession = kc.newKieSession(getRuleName());
	}

	@Override
	public Object transform(Object transformee) throws Exception {
		ksession.insert(transformee);
		return transformee;
	}

	@Override
	public void shutdown() throws Exception {
		ksession.fireAllRules();
		ksession.dispose();
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

}
