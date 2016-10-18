package com.asksunny.rt;

import java.util.List;

public interface MessageSubscriber 
{
	static final String SUBSCRIPTION_WILDCARD = "*";
	
	List<MessageSubscription> getSubscriptions();
	
	void onNewMessage(FlowMessage message);
	
}
