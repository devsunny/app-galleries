package com.asksunny.batch.graph;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThrowExceptionTaskTest {

	@Test(expected=Throwable.class)
	public void test() {		
		GraphFlowBootstrap.main(new String[]{"test-throwException-context.xml", "throwExceptionTask"});
	}

}
