package com.asksunny.rt;

public interface MessageSubscription 
{
	static final String SUBSCRIPTION_DELIMITER = "[^A-Za-z0-9\\*]+";
	String getSubscriptionPattern();
	String getSubscriptionChannel();
	boolean accept(FlowMessage message);
	
}
