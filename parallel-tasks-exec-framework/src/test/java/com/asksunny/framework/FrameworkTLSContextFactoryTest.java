package com.asksunny.framework;

import static org.junit.Assert.*;

import javax.net.ssl.SSLContext;

import org.junit.Test;

public class FrameworkTLSContextFactoryTest {

	@Test
	public void test_master_context() {
		SSLContext context = FrameworkTLSContextFactory.createTaskMasterTLSContext();
	}
	
	
	@Test
	public void test_agent_context() {
		SSLContext context = FrameworkTLSContextFactory.createTaskAgentTLSContext();
	}

}
