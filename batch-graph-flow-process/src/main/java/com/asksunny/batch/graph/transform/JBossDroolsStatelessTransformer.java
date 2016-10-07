package com.asksunny.batch.graph.transform;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import com.asksunny.batch.graph.BatchFlowContext;

public class JBossDroolsStatelessTransformer implements RecordTransformer {
	private KieContainer kc;
	private StatelessKieSession ksession;
	private String ruleName;
	private BatchFlowContext flowContext;

	@Override
	public void init(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
		kc = KieServices.Factory.get().getKieClasspathContainer();
		ksession = kc.newStatelessKieSession("DecisionTableKS");
	}

	@Override
	public Object transform(Object transformee) throws Exception {
		ksession.execute(transformee);
		return transformee;
	}

	@Override
	public void shutdown() throws Exception 
	{
	}

}
