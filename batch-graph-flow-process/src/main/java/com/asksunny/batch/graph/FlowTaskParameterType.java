package com.asksunny.batch.graph;

public enum FlowTaskParameterType {
	None, CLIArgumentContext, BatchFlowContext, CLIArgument, BatchFlowContextObject, SystemProperties, SystemEnvs;

	public static Object getParameter(BatchFlowContext context, FlowTaskParameterType pType, String parameterName) {
		switch (pType) {
		case CLIArgumentContext:
			return context.getCliArgument();
		case BatchFlowContext:
			return context;
		case CLIArgument:
			return context.getCliArgument().get(parameterName);
		case BatchFlowContextObject:
			return context.get(parameterName);
		case SystemProperties:
			return System.getProperties();
		case SystemEnvs:
			return System.getenv();
		case None:
			return null;
		default:
			return null;
		}
	}
}
